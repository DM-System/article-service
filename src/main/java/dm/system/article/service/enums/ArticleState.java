package dm.system.article.service.enums;

public enum ArticleState {
    NEW("NEW", "articles/new/", "content/", "articles/new/assets/"),
    DRAFT("DRAFT", "articles/draft/", "content/", "assets/"),
    STAGED("STAGED", "articles/staged/", "content/", "assets/"),
    PUBLISHED("PUBLISHED", "articles/published/", "content/", "assets/"),
    UNPUBLISHED("UNPUBLISHED", "articles/unpublished/", "content/", "assets/"),
    ARCHIVED("ARCHIVED", "articles/archived/", "content/", "assets/");

    private final String stateName;
    private final String folderPrefix;
    private final String contentFolderPrefix;
    private final String assetsFolderPrefix;

    ArticleState(String stateName, String folderPrefix, String contentFolderPrefix, String assetsFolderPrefix) {
        this.stateName = stateName;
        this.folderPrefix = folderPrefix;
        this.contentFolderPrefix = contentFolderPrefix;
        this.assetsFolderPrefix = assetsFolderPrefix;
    }

    public String getStateName() {
        return stateName;
    }

    public String getFolderPrefix() {
        return folderPrefix;
    }

    public String getContentFolderPrefix() {
        return contentFolderPrefix;
    }

    public String getAssetsFolderPrefix() {
        return assetsFolderPrefix;
    }

    public String generateFolderPrefixOfArticle(String articleId) {
        return this.folderPrefix + articleId + "/";
    }

    public String generateContentFolderPath(String articleId) {
        return this.folderPrefix + articleId + "/" + this.contentFolderPrefix;
    }

    public String generateAssetsFolderPath(String articleId) {
        return this.folderPrefix + articleId + "/" + this.assetsFolderPrefix;
    }
}
