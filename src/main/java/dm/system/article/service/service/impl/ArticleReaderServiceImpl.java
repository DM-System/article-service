package dm.system.article.service.service.impl;

import dm.system.article.service.entity.WebMetadataItem;
import dm.system.article.service.model.DynamoDBPage;
import dm.system.article.service.repository.MetadataRepository;
import dm.system.article.service.service.ArticleReaderService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ArticleReaderServiceImpl implements ArticleReaderService {

    private final MetadataRepository metadataRepository;

    public ArticleReaderServiceImpl(MetadataRepository metadataRepository) {
        this.metadataRepository = metadataRepository;
    }

    @Override
    public DynamoDBPage<WebMetadataItem> getArticlesByRecordState(String recordState, Map<String, String> lastEvaluatedKeyMap) {
        return metadataRepository.getArticlesByRecordState(recordState, lastEvaluatedKeyMap);
    }
}
