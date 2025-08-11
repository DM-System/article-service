package dm.system.article.service.model;

import dm.system.document.common.domain.document.block.Block;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArticleTransitionResponse {

    private Block article;
    private String publicUrl;

}
