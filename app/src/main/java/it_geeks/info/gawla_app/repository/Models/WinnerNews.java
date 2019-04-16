package it_geeks.info.gawla_app.repository.Models;

import java.util.List;

public class WinnerNews {

    private int blog_id;

    private String blog_category;

    private String bog_title;

    private String blog_description;

    private String product_name;

    private String user_name;

    private List<String> blog_imagesArr;

    public WinnerNews(int blog_id, String blog_category, String bog_title, String blog_description, String product_name, String user_name, List<String> blog_imagesArr) {
        this.blog_id = blog_id;
        this.blog_category = blog_category;
        this.bog_title = bog_title;
        this.blog_description = blog_description;
        this.product_name = product_name;
        this.user_name = user_name;
        this.blog_imagesArr = blog_imagesArr;
    }

    public int getBlog_id() {
        return blog_id;
    }

    public void setBlog_id(int blog_id) {
        this.blog_id = blog_id;
    }

    public String getBlog_category() {
        return blog_category;
    }

    public void setBlog_category(String blog_category) {
        this.blog_category = blog_category;
    }

    public String getBog_title() {
        return bog_title;
    }

    public void setBog_title(String bog_title) {
        this.bog_title = bog_title;
    }

    public String getBlog_description() {
        return blog_description;
    }

    public void setBlog_description(String blog_description) {
        this.blog_description = blog_description;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public List<String> getBlog_imagesArr() {
        return blog_imagesArr;
    }

    public void setBlog_imagesArr(List<String> blog_imagesArr) {
        this.blog_imagesArr = blog_imagesArr;
    }
}
