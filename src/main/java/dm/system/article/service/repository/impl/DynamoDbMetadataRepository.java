package dm.system.article.service.repository.impl;

import dm.system.article.service.config.AwsProperties;
import dm.system.article.service.entity.WebMetadataItem;
import dm.system.article.service.model.DynamoDBPage;
import dm.system.article.service.repository.MetadataRepository;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.util.*;

@Repository
public class DynamoDbMetadataRepository implements MetadataRepository {

    private final DynamoDbTable<WebMetadataItem> metadataTable;
    private final DynamoDbIndex<WebMetadataItem> gsi1;

    public DynamoDbMetadataRepository(AwsProperties awsProperties, DynamoDbEnhancedClient enhancedClient) {
        this.metadataTable = enhancedClient.table(awsProperties.getDynamoDB().getTableName(),
                TableSchema.fromClass(WebMetadataItem.class));
        this.gsi1 = this.metadataTable.index(awsProperties.getDynamoDB().getGsi1IndexName());
    }

    @Override
    public List<WebMetadataItem> getFullItemCollection(String pk) {
        QueryConditional queryConditional = QueryConditional
                .keyEqualTo(Key.builder().partitionValue(pk).build());

        return metadataTable.query(queryConditional).items().stream().toList();
    }

    @Override
    public List<WebMetadataItem> findByGsi1IndexPk(String gsi1Pk) {
        QueryConditional queryConditional = QueryConditional
                .keyEqualTo(Key.builder().partitionValue(gsi1Pk).build());

        QueryEnhancedRequest request = QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .scanIndexForward(false)
                .build();

        var result = gsi1.query(request).stream().map(e -> e.items())
                .flatMap(e -> e.stream()).toList();

        return result;
    }

    @Override
    public void save(WebMetadataItem item) {
        metadataTable.putItem(item);
    }

    @Override
    public WebMetadataItem update(WebMetadataItem updatdWebMetadataItem) {
        try {
            return metadataTable.updateItem(updatdWebMetadataItem);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public DynamoDBPage getArticlesByRecordState(String recordState, Map<String, String> lastEvaluatedKeyMap) {
        QueryEnhancedRequest.Builder queryEnhancedRequestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(k -> k.partitionValue(recordState)))
                .consistentRead(false)
                .limit(10);

        if (Objects.nonNull(lastEvaluatedKeyMap) && !lastEvaluatedKeyMap.isEmpty()) {
            queryEnhancedRequestBuilder.exclusiveStartKey(WebMetadataItem.buildLastEvaluatedKey(lastEvaluatedKeyMap));
        }

        QueryEnhancedRequest request = queryEnhancedRequestBuilder.build();

        SdkIterable<Page<WebMetadataItem>> pages = gsi1.query(request);

        Iterator<Page<WebMetadataItem>> iterator = pages.iterator();

        if (!iterator.hasNext()) return new DynamoDBPage(Collections.emptyList());

        Page<WebMetadataItem> page = iterator.next();
        List<WebMetadataItem> articles = page.items();

        DynamoDBPage.DynamoDBPageBuilder dynamoDBPageBuilder = DynamoDBPage.builder();
        dynamoDBPageBuilder.items(articles);

        if (Objects.nonNull(page.lastEvaluatedKey()) && !page.lastEvaluatedKey().isEmpty()) {
            dynamoDBPageBuilder.lastEvaluatedKey(WebMetadataItem.getLastEvaluatedKey(page.lastEvaluatedKey()));
        }

        return dynamoDBPageBuilder.build();
    }
}
