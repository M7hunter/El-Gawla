package it_geeks.info.gawla_app.repository.Models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Category implements Serializable {

    @PrimaryKey
    private int CategoryId;

    private String CategoryName;

    private String CategoryColor;

    private String CategoryImage;

    public Category() {
    }

    public Category(int categoryId, String categoryName, String categoryColor) {
        CategoryId = categoryId;
        CategoryName = categoryName;
        CategoryColor = categoryColor;
    }

    public Category(int categoryId, String categoryName, String categoryColor, String categoryImage) {
        CategoryId = categoryId;
        CategoryName = categoryName;
        CategoryColor = categoryColor;
        CategoryImage = categoryImage;
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

    public String getCategoryImage() {
        return CategoryImage;
    }

    public void setCategoryImage(String categoryImage) {
        CategoryImage = categoryImage;
    }
}
