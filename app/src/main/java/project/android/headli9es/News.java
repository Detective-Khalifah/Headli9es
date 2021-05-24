package project.android.headli9es;

public class News {

    private int totalArticles; // total number of articles for the query made
    private int pageSize; // total number of articles on current page
    private String title; // article header
    private String date; // publication date
    private String page; // {@link URL} of the news article
    private String source; // publisher/writer of the article
    private String description; // summary/sub-title of article
    private String category; // category/topic of the article

    /**
     * Constructor for {@link News} from NEWS API or New York Times API -- https://newsapi.org
     * @param theTitle
     * @param theDate
     * @param thePage
     * @param categoryOrSource
     * @param theDescription
     * @param theArticlesNumber
     */
    public News (String theTitle, String theDate, String thePage, String categoryOrSource,
                 String theDescription, int theArticlesNumber) {
        this.title = theTitle;
        this.page = thePage;
        this.date = theDate;
        this.source = categoryOrSource;
        this.description = theDescription;
        this.totalArticles = theArticlesNumber;
    }

    /**
     * Constructor for {@link News} from Guardian API -- https://content.guardianapis.com/
     * @param title
     * @param date
     * @param page
     * @param category
     * @param articlesNumber
     * @param pageSize
     */
    public News (String title, String date, String page, String category, int articlesNumber, int pageSize) {
        this.title = title;
        this.date = date;
        this.page = page;
        this.category = category;
        this.totalArticles = articlesNumber;
        this.pageSize = pageSize;
    }

    public String getTitle(){ return this.title; }
    public void setTitle(String title){ this.title = title; }

    public String getDate() { return this.date; }
    public void setDate(String date){ this.date = date; }

    public String getPage () { return page; }
    public void setPage (String page) { this.page = page; }

    public String getSource () { return source; }
    public void setSource(String source) { this.source = source; }

    public String getDescription() { return this.description; }
    public void setDescription (String description) { this.description = description; }

    public String getCategory () { return category; }
    public void setCategory (String category) { this.category = category; }

    public int getTotalArticles () {return this.totalArticles;}
    public void setTotalArticles (int numArticles) {this.totalArticles = numArticles;}

    public int getPageSize () { return pageSize; }
    public void setPageSize (int pageSize) { this.pageSize = pageSize; }
}
