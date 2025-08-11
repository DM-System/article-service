package dm.system.article.service.storagestrategy;

import dm.system.document.common.domain.document.Document;

public interface ArticleStorageStrategy {

    void save(Document article);
}
