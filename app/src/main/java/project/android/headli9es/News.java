package project.android.headli9es;

class News {

    private String articlesNumber;
    private String author; //
    private String description; //
    private String date; //
    private String page; // Main
    private String source; //
    private String title;

    public News (String theArticlesNumber, String theAuthor, String theDescription, String theDate, String theSource, String theTitle, String thePage) {
        this.articlesNumber = theArticlesNumber;
        this.author = theAuthor;
        this.description = theDescription;
        this.date = theDate;
        this.page = thePage;
        this.source = theSource;
        this.title = theTitle;
    }

    public String getArticlesNumber() {return this.articlesNumber;}
    public void setArticlesNumber(String numArticles) {this.articlesNumber = numArticles;}

    public String getAuthor(){return this.author;}
    public void setAuthor(String author){this.author = author;}

    public String getSource () {
        return source;
    }
    public void setSource(String source) {
        this.source = source;
    }

    public String getDescription() {
        return this.description;
    }
    public void setDescription (String description) {
        this.description = description;
    }

    public String getDate() {
        return this.date;
    }
    public void setDate(String date){
        this.date = date;
    }

    public String getTitle(){
        return this.title;
    }
    public void setTitle(String title){
        this.title = title;
    }

    public String getPage () {
        return page;
    }
    public void setPage (String page) {
        this.page = page;
    }
}
