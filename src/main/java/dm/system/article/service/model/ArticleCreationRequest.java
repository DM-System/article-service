package dm.system.article.service.model;

import dm.system.article.service.entity.WebMetadataItem;
import dm.system.document.common.domain.document.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleCreationRequest {

    private WebMetadataItem webMetadataItem;
    private Document document;
}
