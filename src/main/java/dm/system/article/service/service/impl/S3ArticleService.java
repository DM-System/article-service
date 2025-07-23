package dm.system.article.service.service.impl;

import dm.system.article.service.enums.ArticleState;
import dm.system.article.service.model.Article;
import dm.system.article.service.model.ArticleCreationResponse;
import dm.system.article.service.repository.ArticleRepository;
import dm.system.article.service.service.ArticleService;
import org.springframework.stereotype.Service;

@Service
public class S3ArticleService implements ArticleService {

    private final ArticleRepository articleRepository;

    public S3ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @Override
    public ArticleCreationResponse save(Article article) {
        return articleRepository.save(article);
    }

    @Override
    public void delete(String articleId, ArticleState state) {
        articleRepository.delete(articleId, state);
    }

    @Override
    public String getPresignedPutUrl(String articleState, String articleId, String fileType, String fullFileName) {
        return articleRepository.generatePresignedPutUrl(articleState, articleId, fileType, fullFileName);
    }

    @Override
    public void moveFileInS3(String articleId, String articleCurrentState, String articleNewState) {
        articleRepository.moveFileInS3(articleId, articleCurrentState, articleNewState);
    }
}
