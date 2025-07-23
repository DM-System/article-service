package dm.system.article.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArticleTransitionRequest {

    private String articleId;
    private String articleCurrentState;
    private String articleNewState;

}
