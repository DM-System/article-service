package dm.system.article.service.storagestrategy;

import dm.system.article.service.model.Article;

public interface ArticleStorageStrategy {

    void save(Article article);
}
