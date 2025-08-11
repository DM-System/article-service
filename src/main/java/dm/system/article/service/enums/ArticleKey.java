package dm.system.article.service.enums;

import java.time.LocalDateTime;

public enum ArticleKey {

    PK("ARTICLE#%s"),
    SK("%s"),

    GSI1PK("%s#%s"),
    GSI1SK("%s");

    private String value;

    ArticleKey(String value) {
        this.value = value;
    }

    public static String getKey(ArticleKey key, String... value) {
        return switch (key) {
            case PK, SK, GSI1PK -> {
                yield String.format(key.value, value);
            }
            case GSI1SK -> {
                yield LocalDateTime.parse(value[0]).toString();
            }
        };
    }
}
