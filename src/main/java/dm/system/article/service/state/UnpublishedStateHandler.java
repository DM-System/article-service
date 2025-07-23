package dm.system.article.service.state;

import dm.system.article.service.enums.ArticleState;
import dm.system.article.service.model.ArticleTransitionRequest;
import dm.system.article.service.service.ArticleService;

public class UnpublishedStateHandler implements ArticleStateHandler {

    private final ArticleService articleService;

    public UnpublishedStateHandler(ArticleService articleService) {
        this.articleService = articleService;
    }

    @Override
    public void transition(ArticleTransitionRequest request, ArticleState targetState) {
        switch (targetState) {
            case PUBLISHED -> {
                articleService.moveFileInS3(request.getArticleId(), request.getArticleCurrentState(), request.getArticleNewState());
                break;
            }
            default -> throw new IllegalStateException("Invalid transition from UNPUBLISHED to " + targetState);
        }
    }
}
