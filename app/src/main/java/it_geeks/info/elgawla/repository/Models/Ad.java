package it_geeks.info.elgawla.repository.Models;

public class Ad {

    private int Id;

    private int SalonId;

    private String Title;

    private String Body;

    private String Image;

    private boolean Type;

    public Ad(int id, int salonId, String title, String body, String image, boolean type) {
        Id = id;
        SalonId = salonId;
        Title = title;
        Body = body;
        Image = image;
        Type = type;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getSalonId() {
        return SalonId;
    }

    public void setSalonId(int salonId) {
        SalonId = salonId;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getBody() {
        return Body;
    }

    public void setBody(String body) {
        Body = body;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public boolean isType() {
        return Type;
    }

    public void setType(boolean type) {
        Type = type;
    }
}
