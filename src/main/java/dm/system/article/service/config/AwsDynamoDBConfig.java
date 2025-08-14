package dm.system.article.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;

import java.net.URI;
import java.util.Objects;

@Component
public class AwsDynamoDBConfig {

    private final AwsProperties awsProperties;

    public AwsDynamoDBConfig(AwsProperties awsProperties) {
        this.awsProperties = awsProperties;
    }

    private DynamoDbClient dynamoDbClient() {
        DynamoDbClientBuilder builder = DynamoDbClient.builder();

        if (Objects.nonNull(awsProperties.getDynamoDB().getLocalEndpoint())) {
            builder.endpointOverride(URI.create(awsProperties.getDynamoDB().getLocalEndpoint()));
        } else {
            builder
                    .region(Region.of(awsProperties.getDynamoDB().getRegion()))
                    .credentialsProvider(StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(awsProperties.getDynamoDB().getAccessKey(),
                            awsProperties.getDynamoDB().getSecretKey())));
        }

        return builder.build();
    }

    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient() {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient())
                .build();
    }
}
