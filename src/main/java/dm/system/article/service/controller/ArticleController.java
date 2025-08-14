package dm.system.article.service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dm.system.article.service.entity.WebMetadataItem;
import dm.system.article.service.model.*;
import dm.system.article.service.service.ArticleReaderService;
import dm.system.article.service.service.ArticleWriterService;
import dm.system.article.service.service.S3ArticleService;
import dm.system.document.common.domain.api.ApiResponse;
import dm.system.document.common.domain.document.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/v1/api/articles")
@CrossOrigin("*")
public class ArticleController {

    private final ArticleWriterService articleWriterService;
    private final ArticleReaderService articleReaderService;
    private final S3ArticleService s3ArticleService;

    public ArticleController(ArticleWriterService articleWriterService, ArticleReaderService articleReaderService, S3ArticleService s3ArticleService) {
        this.articleWriterService = articleWriterService;
        this.articleReaderService = articleReaderService;
        this.s3ArticleService = s3ArticleService;
    }

    @GetMapping(value = "/state")
    public ResponseEntity<ApiResponse<DynamoDBPage>> getArticlesByRecordState(
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

        DynamoDBPage<WebMetadataItem> page = articleReaderService.getArticlesByRecordState(recordState, lastEvaluatedKeyMap);
        return ResponseEntity.ok().body(ApiResponse.<DynamoDBPage>builder()
                .data(page)
                .code(200)
                .success(true)
                .error(new ArrayList<>())
                .message("All articles fetched successfully")
                .build());
    }

    @PostMapping("/presigned-url")
    public ResponseEntity<ApiResponse<S3PresignedUrlResponse>> getPresignedUrlForFile(@RequestBody S3PresignedUrlRequest request) {
        S3PresignedUrlResponse response = s3ArticleService.generatePresignedPutUrl(request);
        return ResponseEntity.ok(ApiResponse.builder()
                .data(response)
                .code(200)
                .success(true)
                .error(new ArrayList<>())
                .message("Presigned URL generated successfully")
                .build());
    }

    @PostMapping("/draft")
    public ResponseEntity<ArticleCreationResponse> save(@RequestBody ArticleCreationRequest request) {
        ArticleCreationResponse response = articleWriterService.save(request.getDocument(), request.getWebMetadataItem());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/asset-upload")
    public ResponseEntity<ApiResponse<AssetCreationResponse>> uploadAsset(@RequestBody AssetCreationRequest request) {
        return ResponseEntity.ok().body(ApiResponse.<AssetCreationResponse>builder()
                .code(200)
                .success(true)
                .error(new ArrayList<>())
                .message("Asset uploaded successfully")
                .build());
    }

    @PutMapping("/update")
    public ResponseEntity<ArticleCreationResponse> update(@RequestBody ArticleCreationRequest request) {
        ArticleCreationResponse response = articleWriterService.updateArticle(request.getDocument(),
                request.getWebMetadataItem());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{articleId}/{state}")
    public ResponseEntity<Void> delete(@PathVariable("articleId") String articleId, @PathVariable("state") String state) {
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/test")
    public String testDocument(@RequestBody Document document) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(document);
    }
}
