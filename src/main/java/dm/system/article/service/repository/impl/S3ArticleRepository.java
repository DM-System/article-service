package dm.system.article.service.repository.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import dm.system.article.service.enums.ArticleState;
import dm.system.article.service.enums.StorageServiceStrategy;
import dm.system.article.service.model.Article;
import dm.system.article.service.model.ArticleCreationResponse;
import dm.system.article.service.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository(StorageServiceStrategy.S3)
public class S3ArticleRepository implements ArticleRepository {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final ObjectMapper objectMapper;

    private final String bucketName;
    private final Duration presignedUrlDuration;

    public S3ArticleRepository(S3Client s3Client,
                          S3Presigner s3Presigner,
                          ObjectMapper objectMapper,
                          @Value("${aws.s3.bucket-name}") String bucketName,
                          @Value("${app.s3.presigned-url-duration-minutes}") long durationMinutes) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
        this.objectMapper = objectMapper;
        this.bucketName = bucketName;
        this.presignedUrlDuration = Duration.ofMinutes(durationMinutes);
    }

    @Override
    public ArticleCreationResponse save(Article article) {
        article.setId(UUID.randomUUID().toString());
        article.setPublishDate(Instant.now());
        article.setCurrentState(ArticleState.DRAFT);

        try {
            String jsonContent = objectMapper.writeValueAsString(article);
            String s3Key = article.getCurrentState().generateContentFolderPath(article.getId()) + article.getId() + ".json";

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .contentType("application/json")
                    .acl(ObjectCannedACL.PUBLIC_READ)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromString(jsonContent));

            URL publicUrl = s3Client.utilities().getUrl(builder -> builder.bucket(bucketName).key(s3Key));

            return new ArticleCreationResponse(article, publicUrl.toString());

        } catch (Exception e) {
            throw new RuntimeException("Error saving article to S3", e);
        }
    }

    @Override
    public void delete(String articleId, ArticleState state) {
        String prefix = state.getFolderPrefix() + articleId;

        try {
            String continuationToken = null;
            List<ObjectIdentifier> batch = new ArrayList<>(1000);

            do {
                ListObjectsV2Response response = s3Client.listObjectsV2(ListObjectsV2Request.builder()
                        .bucket(bucketName)
                        .prefix(prefix)
                        .continuationToken(continuationToken)
                        .build());

                for (S3Object obj : response.contents()) {
                    batch.add(ObjectIdentifier.builder().key(obj.key()).build());
                    if (batch.size() == 1000) {
                        performBatchDelete(batch);
                        batch.clear();
                    }
                }

                continuationToken = response.nextContinuationToken();
            } while (continuationToken != null);

            if (!batch.isEmpty()) {
                performBatchDelete(batch);
            }

            System.out.printf("Deleted article ID: %s from prefix: %s%n", articleId, prefix);

        } catch (S3Exception e) {
            System.err.printf("S3 error deleting article ID: %s at prefix: %s. Error: %s%n", articleId, prefix, e.awsErrorDetails().errorMessage());
            throw new RuntimeException("S3 error while deleting article", e);
        } catch (Exception e) {
            System.err.printf("Unexpected error deleting article ID: %s at prefix: %s. Error: %s%n", articleId, prefix, e.getMessage());
            throw new RuntimeException("Unexpected error while deleting article", e);
        }
    }

    private void performBatchDelete(List<ObjectIdentifier> objects) {
        if (objects.isEmpty()) return;

        s3Client.deleteObjects(DeleteObjectsRequest.builder()
                .bucket(bucketName)
                .delete(d -> d.objects(objects))
                .build());

        System.out.printf("Batch deleted %d objects%n", objects.size());
    }


    private String getArticleJsonS3Key(String articleId, ArticleState state) {
        return state.generateContentFolderPath(articleId) + articleId + ".json";
    }

    public String generatePresignedPutUrl(String articleState, String articleId, String fileType, String fullFileName) {
        String objectKey = ArticleState.valueOf(articleState).generateAssetsFolderPath(articleId) + fullFileName;
        // Define the S3 PUT object request
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .contentType(fileType)
                .build();

        // Create a pre-signed PUT object request with an expiration duration
        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(5)) // URL valid for 5 minutes
                .putObjectRequest(putObjectRequest)
                .build();

        // Generate the pre-signed URL
        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);

        // Return the string representation of the URL
        return presignedRequest.url().toString();
    }

    public String generatePresignedGetUrl(String objectKey) {
        // Define the S3 GET object request
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        // Create a pre-signed GET object request with an expiration duration
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofHours(1)) // URL valid for 1 hour for download
                .getObjectRequest(getObjectRequest)
                .build();

        // Generate the pre-signed URL
        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);

        // Return the string representation of the URL
        return presignedRequest.url().toString();
    }

    public void moveFileInS3(String articleId, String articleCurrentState, String articleNewState) {
        String sourcePrefix = ArticleState.valueOf(articleCurrentState).generateFolderPrefixOfArticle(articleId);
        String destinationPrefix = ArticleState.valueOf(articleNewState).generateFolderPrefixOfArticle(articleId);

        if (!sourcePrefix.endsWith("/")) sourcePrefix += "/";
        if (!destinationPrefix.endsWith("/")) destinationPrefix += "/";

        ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(sourcePrefix)
                .build();

        ListObjectsV2Response listResponse = s3Client.listObjectsV2(listRequest);

        for (S3Object object : listResponse.contents()) {
            String sourceKey = object.key();
            String fileNamePart = sourceKey.substring(sourcePrefix.length());
            String destinationKey = destinationPrefix + fileNamePart;

            // Copy each file
            CopyObjectRequest copyRequest = CopyObjectRequest.builder()
                    .sourceBucket(bucketName)
                    .sourceKey(sourceKey)
                    .destinationBucket(bucketName)
                    .destinationKey(destinationKey)
                    .build();

            s3Client.copyObject(copyRequest);

            // Delete original file
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(sourceKey)
                    .key(sourceKey)
                    .build();

            s3Client.deleteObject(deleteRequest);
        }
    }


}
