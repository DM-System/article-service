package dm.system.article.service.enums;

public enum EntityType {

    ARTICLE("ARTICLE"),
    COURSE("COURSE");

    private String value;

    EntityType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
