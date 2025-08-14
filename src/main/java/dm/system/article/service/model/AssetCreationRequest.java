package dm.system.article.service.model;

import dm.system.article.service.entity.WebMetadataItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssetCreationRequest {

    private WebMetadataItem webMetadataItem;
}
