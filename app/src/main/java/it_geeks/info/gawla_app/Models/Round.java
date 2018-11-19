package it_geeks.info.gawla_app.Models;

public class Round {

    private String ProductName;

    private String ProductImage;

    private String ProductCategory;

    private String ProductPrice;

    private String ProductDescription;

    private String StartTime;

    private String EndTime;

    private String JoinedMembersNumber;

    public Round() {
    }

    public Round(String productName, String productImage, String productCategory, String productPrice, String productDescription, String startTime, String endTime, String joinedMembersNumber) {
        ProductName = productName;
        ProductImage = productImage;
        ProductCategory = productCategory;
        ProductPrice = productPrice;
        ProductDescription = productDescription;
        StartTime = startTime;
        EndTime = endTime;
        JoinedMembersNumber = joinedMembersNumber;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getProductImage() {
        return ProductImage;
    }

    public void setProductImage(String productImage) {
        ProductImage = productImage;
    }

    public String getProductCategory() {
        return ProductCategory;
    }

    public void setProductCategory(String productCategory) {
        ProductCategory = productCategory;
    }

    public String getProductPrice() {
        return ProductPrice;
    }

    public void setProductPrice(String productPrice) {
        ProductPrice = productPrice;
    }

    public String getProductDescription() {
        return ProductDescription;
    }

    public void setProductDescription(String productDescription) {
        ProductDescription = productDescription;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public String getJoinedMembersNumber() {
        return JoinedMembersNumber;
    }

    public void setJoinedMembersNumber(String joinedMembersNumber) {
        JoinedMembersNumber = joinedMembersNumber;
    }
}
