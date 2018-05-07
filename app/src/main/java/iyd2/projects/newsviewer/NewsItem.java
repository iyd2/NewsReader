package iyd2.projects.newsviewer;

public class NewsItem {

    private String author;
    private String title;
    private String description;
    private String url;
    private String urlToImage;
    private String publishedAt;

    public NewsItem(String title, String description, String urlToImage) {
        this.title = title;
        this.description = description;
        this.urlToImage = urlToImage;
    }
}
