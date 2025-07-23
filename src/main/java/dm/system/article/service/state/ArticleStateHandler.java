package dm.system.article.service.state;

import dm.system.article.service.enums.ArticleState;
import dm.system.article.service.model.ArticleTransitionRequest;

public interface ArticleStateHandler {

    void transition(ArticleTransitionRequest articleTransitionRequest, ArticleState targetState);

}
