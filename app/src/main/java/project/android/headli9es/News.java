package project.android.headli9es;

public class News {

    private int articlesNumber;
    private String content; //
    private String description; //
    private String date; //
    private String page; // Main
    private String source; //
    private String title;

    public News (int theArticlesNumber, String theContent, String theDescription, String theDate,
                 String theSource, String theTitle, String thePage) {
        this.articlesNumber = theArticlesNumber;
        this.content = theContent;
        this.description = theDescription;
        this.date = theDate;
        this.source = theSource;
        this.title = theTitle;
        this.page = thePage;
    }

    public int getArticlesNumber() {return this.articlesNumber;}
    public void setArticlesNumber(int numArticles) {this.articlesNumber = numArticles;}

    public String getContent (){return this.content;}
    public void setContent (String content){this.content = content;}

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
