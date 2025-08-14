package dm.system.article.service.service;

import dm.system.article.service.entity.WebMetadataItem;
import dm.system.article.service.model.ArticleCreationResponse;
import dm.system.article.service.model.AssetCreationRequest;
import dm.system.article.service.model.AssetCreationResponse;
import dm.system.document.common.domain.document.Document;

public interface ArticleWriterService {

    String CONTENT_FOLDER = "%s/content/%s.%s";
    String ASSET_FOLDER = "%s/assets/%s.%s";

    ArticleCreationResponse save(Document document, WebMetadataItem webMetadataItemRequest);

    ArticleCreationResponse updateArticle(Document document, WebMetadataItem webMetadataItemRequest);

    default String getArticlePath(String parentFolder, String resourceName, String extension) {
        return String.format(CONTENT_FOLDER, parentFolder, resourceName, extension);
    }

    default String getAssetPath(String parentFolder, String resourceName, String extension) {
        return String.format(ASSET_FOLDER, parentFolder, resourceName, extension);
    }
}
