package it_geeks.info.gawla_app.repository.Models;

public class SalonArchiveModel {

    private String ProductImage;

    private String ProductName;

    private String Date;

    private String Status;

    private int SalonId;

    public SalonArchiveModel(String productImage, String productName, String date, String status, int salonId) {
        ProductImage = productImage;
        ProductName = productName;
        Date = date;
        Status = status;
        SalonId = salonId;
    }

    public String getProductImage() {
        return ProductImage;
    }

    public void setProductImage(String productImage) {
        ProductImage = productImage;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public int getSalonId() {
        return SalonId;
    }

    public void setSalonId(int salonId) {
        SalonId = salonId;
    }
}
