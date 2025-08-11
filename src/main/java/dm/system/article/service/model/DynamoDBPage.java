package dm.system.article.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DynamoDBPage<T> {

    private List<T> items;
    private Map<String, String> lastEvaluatedKey;

    public DynamoDBPage(List<T> items) {
        this.items = List.copyOf(items);
    }

}
