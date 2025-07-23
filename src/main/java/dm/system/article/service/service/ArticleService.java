package dm.system.article.service.service;

import dm.system.article.service.enums.ArticleState;
import dm.system.article.service.model.Article;
import dm.system.article.service.model.ArticleCreationResponse;

public interface ArticleService {

    ArticleCreationResponse save(Article article);

    void delete(String articleId, ArticleState state);

    String getPresignedPutUrl(String articleState, String articleId, String fileType, String fullFileName);

    void moveFileInS3(String articleId, String articleCurrentState, String articleNewState);
}
