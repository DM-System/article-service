package dm.system.article.service.repository;

import dm.system.article.service.entity.WebMetadataItem;
import dm.system.article.service.model.DynamoDBPage;

import java.util.List;
import java.util.Map;

public interface MetadataRepository {

    List<WebMetadataItem> getFullItemCollection(String pk);

    List<WebMetadataItem> findByGsi1IndexPk(String gsi1Pk);

    void save(WebMetadataItem item);

    DynamoDBPage getArticlesByRecordState(String recordState, Map<String, String> lastEvaluatedKeyMap);

}
