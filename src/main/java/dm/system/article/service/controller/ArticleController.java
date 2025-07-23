package dm.system.article.service.controller;

import dm.system.article.service.enums.ArticleState;
import dm.system.article.service.model.Article;
import dm.system.article.service.model.ArticleCreationResponse;
import dm.system.article.service.model.ArticleTransitionRequest;
import dm.system.article.service.model.ArticleTransitionResponse;
import dm.system.article.service.service.ArticleService;
import dm.system.article.service.state.ArticleStateMachine;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

@RestController
@RequestMapping("/v1/api/articles")
public class ArticleController {

    private final ArticleService articleService;
    private final ArticleStateMachine articleStateMachine;

    public ArticleController(ArticleService articleService, ArticleStateMachine articleStateMachine) {
        this.articleService = articleService;
        this.articleStateMachine = articleStateMachine;
    }

    @PostMapping()
    public ResponseEntity<String> save(@RequestBody Article article) {
        ArticleCreationResponse response = articleService.save(article);
        return ResponseEntity.ok(response.getPublicUrl());
    }

    @DeleteMapping("/{articleId}/{state}")
    public ResponseEntity<Void> delete(@PathVariable("articleId") String articleId, @PathVariable("state") String state) {
        articleService.delete(articleId, ArticleState.valueOf(state));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/presigned-url")
    public ResponseEntity<String> generatePresignedUrl(@RequestParam("articleState") String articleState,
                                                       @RequestParam("articleId") String articleId,
                                                       @RequestParam("fileType") String fileType,
                                                       @RequestParam("fullFileName") String fullFileName) {
        String presignedUrl = articleService.getPresignedPutUrl(articleState, articleId, fileType, fullFileName);
        return ResponseEntity.ok(presignedUrl);
    }

    @PutMapping("/state-transition")
    public ResponseEntity<Void> articleStateTransition(@RequestParam("articleId") String articleId,
                                                       @RequestParam("articleCurrentState") String articleCurrentState,
                                                       @RequestParam("articleNewState") String articleNewState) {
        ArticleTransitionRequest transitionRequest = ArticleTransitionRequest.builder()
                .articleId(articleId)
                .articleCurrentState(articleCurrentState)
                .articleNewState(articleNewState)
                .build();
        articleStateMachine.transition(transitionRequest);
        articleService.moveFileInS3(articleId, articleCurrentState, articleNewState);
        return ResponseEntity.noContent().build();
    }
}
