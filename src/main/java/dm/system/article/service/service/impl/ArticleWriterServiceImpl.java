package dm.system.article.service.service.impl;

import dm.system.article.service.entity.WebMetadataItem;
import dm.system.article.service.enums.ArticleKey;
import dm.system.article.service.enums.ArticleState;
import dm.system.article.service.enums.EntityType;
import dm.system.article.service.enums.Extension;
import dm.system.article.service.model.ArticleCreationRequest;
import dm.system.article.service.model.ArticleCreationResponse;
import dm.system.article.service.model.AssetCreationRequest;
import dm.system.article.service.model.AssetCreationResponse;
import dm.system.article.service.repository.impl.DynamoDbMetadataRepository;
import dm.system.article.service.service.ArticleWriterService;
import dm.system.article.service.service.S3ArticleService;
import dm.system.article.service.util.Util;
import dm.system.document.common.domain.document.Audit;
import dm.system.document.common.domain.document.Document;
import dm.system.document.common.domain.document.MetaData;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ArticleWriterServiceImpl implements ArticleWriterService {

    private final S3ArticleService s3ArticleService;
    private final DynamoDbMetadataRepository dynamoRepository;

    public ArticleWriterServiceImpl(S3ArticleService s3ArticleService, DynamoDbMetadataRepository dynamoRepository) {
        this.s3ArticleService = s3ArticleService;
        this.dynamoRepository = dynamoRepository;
    }

    @Override
    public ArticleCreationResponse save(Document document, WebMetadataItem metadataItemRequest) {
        String pk = ArticleKey.getKey(ArticleKey.PK, Util.uuid());
        String sk = ArticleKey.getKey(ArticleKey.SK, Util.uuid());
        String gsi1pk = ArticleKey.getKey(ArticleKey.GSI1PK, EntityType.ARTICLE.getValue(), ArticleState.DRAFT.getStateName());
        String gsi1sk = ArticleKey.getKey(ArticleKey.GSI1SK, LocalDateTime.now().toString());
        String documentId = Util.uuid();

        document.setId(documentId);
        ArticleCreationResponse articleCreationResponse = s3ArticleService.save(document,
                getArticlePath(pk, documentId, Extension.JSON.getValue()));

        WebMetadataItem webMetadataItem = WebMetadataItem.builder()
                .pk(pk)
                .sk(sk)
                .entityType(EntityType.ARTICLE.getValue())
                .recordState(ArticleState.DRAFT.getStateName())
                .title(metadataItemRequest.getTitle())
                .summary(metadataItemRequest.getSummary())
                .s3ContentPath(articleCreationResponse.getPublicUrl())
                .isMultiPage(metadataItemRequest.getIsMultiPage())
                .pageCount(metadataItemRequest.getPageCount())
                .likeCount(metadataItemRequest.getLikeCount())
                .authorId(metadataItemRequest.getAuthorId())
                .lastUpdatedBy(LocalDateTime.now().toString())
                .createdAt(LocalDateTime.now().toString())
                .lastUpdatedAt(LocalDateTime.now().toString())
                .gsi1pk(gsi1pk)
                .gsi1sk(gsi1sk)
                .build();

        dynamoRepository.save(webMetadataItem);

        articleCreationResponse.setWebMetadataItem(webMetadataItem);

        return articleCreationResponse;
    }

    @Override
    public ArticleCreationResponse updateArticle(Document document, WebMetadataItem webMetadataItemRequest) {
        webMetadataItemRequest.setLastUpdatedBy("SYSTEM");
        webMetadataItemRequest.setLastUpdatedAt(LocalDateTime.now().toString());
        WebMetadataItem updatedWebMetadataItem = dynamoRepository.update(webMetadataItemRequest);

        ArticleCreationResponse articleCreationResponse = s3ArticleService.save(document,
                getArticlePath(webMetadataItemRequest.getPk(), document.getId(), Extension.JSON.getValue()));

        articleCreationResponse.setWebMetadataItem(updatedWebMetadataItem);

        return articleCreationResponse;
    }

}
