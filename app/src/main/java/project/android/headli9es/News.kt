package project.android.headli9es

class News {
    var totalArticles // total number of articles for the query made
            : Int
    var pageSize // total number of articles on current page
            = 0
    var title // article header
            : String
    var date // publication date
            : String
    var page // {@link URL} of the news article
            : String
    var source // publisher/writer of the article
            : String
    var description // summary/sub-title of article
            : String? = null
    var category // category/topic of the article
            : String? = null

    /**
     * Constructor for [News] from NEWS API or New York Times API -- https://newsapi.org
     * @param theTitle
     * @param theDate
     * @param thePage
     * @param categoryOrSource
     * @param theDescription
     * @param theArticlesNumber
     */
    constructor(
        theTitle: String, theDate: String, thePage: String, categoryOrSource: String,
        theDescription: String?, theArticlesNumber: Int
    ) {
        title = theTitle
        page = thePage
        date = theDate
        source = categoryOrSource
        description = theDescription
        totalArticles = theArticlesNumber
    }

    /**
     * Constructor for [News] from Guardian API -- https://content.guardianapis.com/
     * @param title
     * @param date
     * @param page
     * @param source
     * @param category
     * @param articlesNumber
     * @param pageSize
     */
    constructor(
        title: String,
        date: String,
        page: String,
        source: String,
        category: String?,
        articlesNumber: Int,
        pageSize: Int
    ) {
        this.title = title
        this.date = date
        this.page = page
        this.source = source
        this.category = category
        totalArticles = articlesNumber
        this.pageSize = pageSize
    }
}