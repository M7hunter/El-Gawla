package it_geeks.info.gawla_app.repository.Models;

public class WebPage {

    private int page_id;

    private String page_title;

    private String page_link;

    public WebPage(int page_id, String page_title, String page_link) {
        this.page_id = page_id;
        this.page_title = page_title;
        this.page_link = page_link;
    }

    public int getPage_id() {
        return page_id;
    }

    public void setPage_id(int page_id) {
        this.page_id = page_id;
    }

    public String getPage_title() {
        return page_title;
    }

    public void setPage_title(String page_title) {
        this.page_title = page_title;
    }

    public String getPage_link() {
        return page_link;
    }

    public void setPage_link(String page_link) {
        this.page_link = page_link;
    }
}
