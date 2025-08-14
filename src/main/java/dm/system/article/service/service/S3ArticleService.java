package dm.system.article.service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dm.system.article.service.enums.ArticleState;
import dm.system.article.service.enums.StorageServiceStrategy;
import dm.system.article.service.model.ArticleCreationResponse;
import dm.system.article.service.model.S3PresignedUrlRequest;
import dm.system.article.service.model.S3PresignedUrlResponse;
import dm.system.article.service.util.Util;
import dm.system.document.common.domain.document.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Duration;
import java.util.List;

@Repository
public class S3ArticleService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final ObjectMapper objectMapper;

    private final String bucketName;

    public S3ArticleService(S3Client s3Client, S3Presigner s3Presigner,
                            ObjectMapper objectMapper,
                            @Value("${aws.s3.bucket-name}") String bucketName) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
        this.objectMapper = objectMapper;
        this.bucketName = bucketName;
    }

    public ArticleCreationResponse save(Document document, String s3PathKey) {

        try {
            String jsonContent = objectMapper.writeValueAsString(document);

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3PathKey)
                    .contentType("application/json")
                    .acl(ObjectCannedACL.PUBLIC_READ)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromString(jsonContent));

            URL publicUrl = s3Client.utilities()
                    .getUrl(builder -> builder.bucket(bucketName).key(s3PathKey).build()).toURI().toURL();

            return ArticleCreationResponse.builder()
                    .document(document)
                    .publicUrl(publicUrl.toString())
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Error saving article to S3", e);
        }
    }

    public void delete(String articleId, ArticleState state) {
    }

    private void performBatchDelete(List<ObjectIdentifier> objects) {
    }

    private String getArticleJsonS3Key(String articleId, ArticleState state) {
        return null;
    }

    public S3PresignedUrlResponse generatePresignedPutUrl(S3PresignedUrlRequest request) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(request.getObjectKey())
                .contentType(Util.detectContentType(request.getObjectKey()))
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(r -> r
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(putObjectRequest)
        );


        try {
            URL publicUrl = s3Client.utilities()
                    .getUrl(builder -> builder.bucket(bucketName).key(request.getObjectKey()).build()).toURI().toURL();

            return S3PresignedUrlResponse.builder()
                    .presignedUrl(presignedRequest.url().toString())
                    .publicUrl(publicUrl.toString())
                    .build();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public String generatePresignedGetUrl(String objectKey) {
        return null;
    }

    public void moveFileInS3(String articleId, String articleCurrentState, String articleNewState) {
    }
}
