package dm.system.article.service.model;

import dm.system.document.common.domain.document.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleCreationResponse {

    private Document article;
    private String publicUrl;

}