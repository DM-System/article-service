package dm.system.article.service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "aws")
@Data
public class AwsProperties {

    private S3 s3;
    private String region;
    private Credentials credentials;
    private DynamoDB dynamoDB;

    @Data
    public static class S3 {
        private String bucketName;
    }

    @Data
    public static class Credentials {
        private String accessKey;
        private String secretKey;
    }

    @Data
    public static class DynamoDB {
        private String region;
        private String tableName;
        private String accessKey;
        private String secretKey;
        private String localEndpoint;
        private String gsi1IndexName;
    }
}
