package it_geeks.info.gawla_app.Repositry.Models;

public class WinnerNews {

    private String NewsTitle;

    private String NewsBody;

    private String NewsImage;

    public WinnerNews(String newsTitle, String newsBody, String newsImage) {
        NewsTitle = newsTitle;
        NewsBody = newsBody;
        NewsImage = newsImage;
    }

    public String getNewsTitle() {
        return NewsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        NewsTitle = newsTitle;
    }

    public String getNewsBody() {
        return NewsBody;
    }

    public void setNewsBody(String newsBody) {
        NewsBody = newsBody;
    }

    public String getNewsImage() {
        return NewsImage;
    }

    public void setNewsImage(String newsImage) {
        NewsImage = newsImage;
    }
}
