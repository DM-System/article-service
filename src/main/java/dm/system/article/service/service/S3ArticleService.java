package dm.system.article.service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dm.system.article.service.enums.ArticleState;
import dm.system.article.service.enums.StorageServiceStrategy;
import dm.system.article.service.model.ArticleCreationResponse;
import dm.system.document.common.domain.document.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.net.URL;
import java.util.List;

@Repository(StorageServiceStrategy.S3)
public class S3ArticleService {

    private final S3Client s3Client;
    private final ObjectMapper objectMapper;

    private final String bucketName;

    public S3ArticleService(S3Client s3Client,
                            ObjectMapper objectMapper,
                            @Value("${aws.s3.bucket-name}") String bucketName) {
        this.s3Client = s3Client;
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

            PutObjectResponse response = s3Client.putObject(putObjectRequest, RequestBody.fromString(jsonContent));

            URL publicUrl = s3Client.utilities()
                    .getUrl(builder -> builder.bucket(bucketName).key(s3PathKey).build()).toURI().toURL();

            return new ArticleCreationResponse(document, publicUrl.toString());

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

    public String generatePresignedPutUrl(String articleState, String articleId, String fileType, String fullFileName) {
        return null;
    }

    public String generatePresignedGetUrl(String objectKey) {
        return null;
    }

    public void moveFileInS3(String articleId, String articleCurrentState, String articleNewState) {
    }
}
