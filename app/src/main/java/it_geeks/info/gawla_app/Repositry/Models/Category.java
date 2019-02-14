package it_geeks.info.gawla_app.Repositry.Models;

import java.util.List;

public class Category {

    private int CategoryId;

    private String CategoryName;

    private String CategoryColor;

    public Category(int categoryId, String categoryName, String categoryColor) {
        CategoryId = categoryId;
        CategoryName = categoryName;
        CategoryColor = categoryColor;
    }

    public int getCategoryId() {
        return CategoryId;
    }

    public void setCategoryId(int categoryId) {
        CategoryId = categoryId;
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String categoryName) {
        CategoryName = categoryName;
    }

    public String getCategoryColor() {
        return CategoryColor;
    }

    public void setCategoryColor(String categoryColor) {
        CategoryColor = categoryColor;
    }
}
