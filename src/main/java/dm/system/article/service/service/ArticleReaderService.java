package dm.system.article.service.service;

import dm.system.article.service.entity.WebMetadataItem;
import dm.system.article.service.model.DynamoDBPage;

import java.util.Map;

public interface ArticleReaderService {

    DynamoDBPage<WebMetadataItem> getArticlesByRecordState(String recordState, Map<String, String> lastEvaluatedKeyMap);
}
