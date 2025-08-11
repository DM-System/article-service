package dm.system.article.service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dm.system.article.service.entity.WebMetadataItem;
import dm.system.article.service.model.ArticleCreationResponse;
import dm.system.article.service.model.DynamoDBPage;
import dm.system.article.service.service.ArticleReaderService;
import dm.system.article.service.service.ArticleWriterService;
import dm.system.document.common.domain.document.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/v1/api/articles")
public class ArticleController {

    private final ArticleWriterService articleWriterService;
    private final ArticleReaderService articleReaderService;

    public ArticleController(ArticleWriterService articleWriterService, ArticleReaderService articleReaderService) {
        this.articleWriterService = articleWriterService;
        this.articleReaderService = articleReaderService;
    }

    @GetMapping(value = "/state")
    public ResponseEntity<DynamoDBPage<WebMetadataItem>> getArticlesByRecordState(
            @RequestParam("recordState") String recordState,
            @RequestParam(value = "lastEvaluatedKey", required = false) String lastPartitionKey,
            @RequestParam(value = "lastSortKey", required = false) String lastSortKey,
            @RequestParam(value = "lastGsi1PartitionKey", required = false) String lastGsi1PartitionKey,
            @RequestParam(value = "lastGsi1SortKey", required = false) String lastGsi1SortKey) {
        Map<String, String> lastEvaluatedKeyMap = Map.of();

        if (Objects.nonNull(lastPartitionKey) && Objects.nonNull(lastSortKey) && Objects.nonNull(lastGsi1PartitionKey) && Objects.nonNull(lastGsi1SortKey)) {
            lastEvaluatedKeyMap = Map.of(
                    "PK", lastPartitionKey,
                    "SK", lastSortKey,
                    "GSI1PK", lastGsi1PartitionKey,
                    "GSI1SK", lastGsi1SortKey);
        }
        System.out.println();

        DynamoDBPage<WebMetadataItem> page = articleReaderService.getArticlesByRecordState(recordState, lastEvaluatedKeyMap);
        return ResponseEntity.ok(page);
    }

    @PostMapping()
    public ResponseEntity<String> save(@RequestBody Document article) {
        ArticleCreationResponse response = articleWriterService.save(article);
        return ResponseEntity.ok(response.getPublicUrl());
    }

    @DeleteMapping("/{articleId}/{state}")
    public ResponseEntity<Void> delete(@PathVariable("articleId") String articleId, @PathVariable("state") String state) {
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/presigned-url")
    public ResponseEntity<String> generatePresignedUrl(@RequestParam("articleState") String articleState,
                                                       @RequestParam("articleId") String articleId,
                                                       @RequestParam("fileType") String fileType,
                                                       @RequestParam("fullFileName") String fullFileName) {
        return ResponseEntity.ok().build();
    }

    @PutMapping("/state-transition")
    public ResponseEntity<Void> articleStateTransition(@RequestParam("articleId") String articleId,
                                                       @RequestParam("articleCurrentState") String articleCurrentState,
                                                       @RequestParam("articleNewState") String articleNewState) {
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/test")
    public String testDocument(@RequestBody Document document) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(document);
    }
}
