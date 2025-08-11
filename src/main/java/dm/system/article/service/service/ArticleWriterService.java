package dm.system.article.service.service;

import dm.system.article.service.model.ArticleCreationResponse;
import dm.system.document.common.domain.document.Document;

public interface ArticleWriterService {

    String CONTENT_FOLDER = "%s/content/%s.%s";

    ArticleCreationResponse save(Document document);

    default String getArticlePath(String parentFolder, String resourceName, String extension) {
        return String.format(CONTENT_FOLDER, parentFolder, resourceName, extension);
    }
}
