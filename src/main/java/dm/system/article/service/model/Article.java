package dm.system.article.service.model;

import dm.system.article.service.enums.ArticleState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Article {

    private String id;
    private Instant publishDate;
    private ArticleState currentState;
    private List<Map<String, Object>> data;
    private Map<String, Object> metadata;

}
