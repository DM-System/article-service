package dm.system.article.service.state;

import dm.system.article.service.enums.ArticleState;
import dm.system.article.service.service.ArticleService;

public class ArticleStateHandlerFactory {

    public static ArticleStateHandler create(ArticleState articleState, ArticleService articleService) {
        switch (articleState) {
            case NEW -> {
                return new NewStateHandler(articleService);
            }
            case DRAFT -> {
                return new DraftStateHandler(articleService);
            }
            case STAGED -> {
                return new StagedStateHandler(articleService);
            }
            case PUBLISHED -> {
                return new PublishedStateHandler(articleService);
            }
            case UNPUBLISHED -> {
                return new UnpublishedStateHandler(articleService);
            }
            case ARCHIVED -> {
                return new ArchivedStateHandler(articleService);
            }
            default -> throw new IllegalStateException("No handler for state: " + articleState);
        }
    }
}
