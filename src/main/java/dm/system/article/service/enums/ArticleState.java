package dm.system.article.service.enums;

public enum ArticleState {
    DRAFT("DRAFT"),
    STAGED("STAGED"),
    PUBLISHED("PUBLISHED"),
    UNPUBLISHED("UNPUBLISHED"),
    ARCHIVED("ARCHIVED");

    private final String stateName;

    ArticleState(String stateName) {
        this.stateName = stateName;
    }

    public String getStateName() {
        return stateName;
    }
}
