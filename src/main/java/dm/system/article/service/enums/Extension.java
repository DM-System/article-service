package dm.system.article.service.enums;

public enum Extension {
    JSON("json"),
    XML("xml"),
    TXT("txt"),
    CSV("csv"),
    XLS("xls"),
    XLSX("xlsx"),
    DOC("doc"),
    DOCX("docx"),
    PDF("pdf"),
    PPT("ppt"),
    PPTX("pptx"),
    ZIP("zip"),
    GIF("gif"),
    JPG("jpg"),
    PNG("png"),
    SVG("svg"),
    HTML("html");

    private String value;

    Extension(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
