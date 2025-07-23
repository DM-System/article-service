package dm.system.article.service.state;

import dm.system.article.service.enums.ArticleState;
import dm.system.article.service.model.ArticleTransitionRequest;
import dm.system.article.service.service.ArticleService;

public class DraftStateHandler implements ArticleStateHandler {
    private final ArticleService articleService;

    public DraftStateHandler(ArticleService articleService) {
        this.articleService = articleService;
    }

    @Override
    public void transition(ArticleTransitionRequest request, ArticleState targetState) {
        switch (targetState) {
            case STAGED:
                articleService.moveFileInS3(request.getArticleId(), request.getArticleCurrentState(), request.getArticleNewState());
                break;
            default:
                throw new IllegalStateException("Invalid transition from DRAFT to " + targetState);
        }
    }

}