package it_geeks.info.elgawla.repository.Models;

//import com.emeint.android.fawryplugin.Plugininterfacing.PayableItem;

import java.io.Serializable;

public class Package implements Serializable {

    private int Id;
    private String Title, Price, Body, Color;

    public Package(int id, String title, String price, String body, String color) {
        Id = id;
        Title = title;
        Price = price;
        Body = body;
        Color = color;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getBody() {
        return Body;
    }

    public void setBody(String body) {
        Body = body;
    }

    public String getColor() {
        return Color;
    }

    public void setColor(String color) {
        Color = color;
    }

//    @Override
//    public String getFawryItemDescription() {
//        return Body;
//    }
//
//    @Override
//    public String getFawryItemSKU() {
//        return String.valueOf(Id);
//    }
//
//    @Override
//    public String getFawryItemPrice() {
//        return Price;
//    }
//
//    @Override
//    public String getFawryItemQuantity() {
//        return "1";
//    }
}
