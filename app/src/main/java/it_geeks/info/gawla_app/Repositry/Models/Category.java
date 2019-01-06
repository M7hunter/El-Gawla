package it_geeks.info.gawla_app.Repositry.Models;

public class Category {

    private String CategoryName;

    private String CategoryColor;

    public Category(String categoryName, String categoryColor) {
        CategoryName = categoryName;
        CategoryColor = categoryColor;
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
