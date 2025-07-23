package dm.system.article.service.state;

import dm.system.article.service.enums.ArticleState;
import dm.system.article.service.model.ArticleTransitionRequest;
import dm.system.article.service.service.ArticleService;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

import static dm.system.article.service.enums.ArticleState.*;

@Component
public class ArticleStateMachine {

    private final ArticleService articleService;

    Map<String, Set<String>> allowedTransitions = Map.of(
            NEW.getStateName(), Set.of(DRAFT.getStateName()),
            DRAFT.getStateName(), Set.of(STAGED.getStateName()),
            STAGED.getStateName(), Set.of(DRAFT.getStateName(), PUBLISHED.getStateName()),
            PUBLISHED.getStateName(), Set.of(UNPUBLISHED.getStateName(), ARCHIVED.getStateName()),
            UNPUBLISHED.getStateName(), Set.of(PUBLISHED.getStateName())
    );

    public ArticleStateMachine(ArticleService articleService) {
        this.articleService = articleService;
    }

    public void transition(ArticleTransitionRequest articleTransitionRequest) {
        Set<String> validStates = allowedTransitions.get(articleTransitionRequest.getArticleCurrentState());
        if (validStates == null || !validStates.contains(articleTransitionRequest.getArticleNewState())) {
            throw new IllegalStateException("No handler for state: " + articleTransitionRequest.getArticleCurrentState()
                    + " -> " + articleTransitionRequest.getArticleNewState());
        }
        ArticleStateHandler handler = ArticleStateHandlerFactory.create(ArticleState.valueOf(articleTransitionRequest.getArticleCurrentState()),
                articleService);
        handler.transition(articleTransitionRequest, ArticleState.valueOf(articleTransitionRequest.getArticleNewState()));
    }

}
