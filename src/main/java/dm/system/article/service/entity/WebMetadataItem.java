package dm.system.article.service.entity;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@DynamoDbBean
public class WebMetadataItem {

    // --- Primary Key Attributes ---
    private String pk;
    private String sk;

    // --- Core Entity Attributes ---
    private String entityType;
    private String recordState;
    private String title;
    private String summary;
    private String s3ContentPath;
    private Boolean isMultiPage;
    private Integer pageCount;
    private BigDecimal price;
    private Long likeCount;
    private String authorId;
    private String lastUpdatedBy;

    // --- Comment Attributes ---
    private String commentId;
    private String parentCommentId;
    private String commentText;

    // --- Shared / GSI Attributes ---
    private String createdAt;
    private String lastUpdatedAt;

    private String gsi1pk;
    private String gsi1sk;

    // --- Getters and Setters ---

    @DynamoDbPartitionKey
    @DynamoDbAttribute(value = "PK")
    public String getPk() { return pk; }

    @DynamoDbSortKey
    @DynamoDbAttribute(value = "SK")
    public String getSk() { return sk; }

    @DynamoDbSecondaryPartitionKey(indexNames = "EntityType_RecordState_GSI")
    @DynamoDbAttribute(value = "GSI1PK")
    public String getGsi1pk() { return gsi1pk; }


    @DynamoDbSecondarySortKey(indexNames = "EntityType_RecordState_GSI")
    @DynamoDbAttribute(value = "GSI1SK")
    public String getGsi1sk() { return gsi1sk; }

    public void setGsi1pk(String gsi1pk) { this.gsi1pk = gsi1pk; }

    public void setGsi1sk(String gsi1sk) { this.gsi1sk = gsi1sk; }

    public void setPk(String pk) { this.pk = pk; }

    public void setSk(String sk) { this.sk = sk; }

    public static Map<String, String> getLastEvaluatedKey(Map<String, AttributeValue> lastEvaluatedKey) {
        return Map.of(
                "PK", lastEvaluatedKey.get("PK").s(),
                "SK", lastEvaluatedKey.get("SK").s(),
                "GSI1PK", lastEvaluatedKey.get("GSI1PK").s(),
                "GSI1SK", lastEvaluatedKey.get("GSI1SK").s()
        );
    }

    public static Map<String, AttributeValue> buildLastEvaluatedKey(Map<String, String> lastEvaluatedKeyMap) {
        return Map.of(
                "PK", AttributeValue.builder().s(lastEvaluatedKeyMap.get("PK")).build(),
                "SK", AttributeValue.builder().s(lastEvaluatedKeyMap.get("SK")).build(),
                "GSI1PK", AttributeValue.builder().s(lastEvaluatedKeyMap.get("GSI1PK")).build(),
                "GSI1SK", AttributeValue.builder().s(lastEvaluatedKeyMap.get("GSI1SK")).build()
        );
    }
}