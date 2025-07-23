package dm.system.article.service.repository;

import dm.system.article.service.enums.ArticleState;
import dm.system.article.service.model.Article;
import dm.system.article.service.model.ArticleCreationResponse;

import java.util.Locale;

public interface ArticleRepository {

    ArticleCreationResponse save(Article article);

    void delete(String articleId, ArticleState state);

    String generatePresignedPutUrl(String articleState, String articleId, String fileType, String fullFileName);

    void moveFileInS3(String articleId, String articleCurrentState, String articleNewState);
}
