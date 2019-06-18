package it_geeks.info.gawla_app.repository.Models;

public class MyCardModel {

    private String CardColor;

    private String CardCategory;

    private String CardStatus;

    private int SalonId;

    public MyCardModel(String cardColor, String cardCategory, String cardStatus, int salonId) {
        CardColor = cardColor;
        CardCategory = cardCategory;
        CardStatus = cardStatus;
        SalonId = salonId;
    }

    public String getCardColor() {
        return CardColor;
    }

    public void setCardColor(String cardColor) {
        CardColor = cardColor;
    }

    public String getCardCategory() {
        return CardCategory;
    }

    public void setCardCategory(String cardCategory) {
        CardCategory = cardCategory;
    }

    public String getCardStatus() {
        return CardStatus;
    }

    public void setCardStatus(String cardStatus) {
        CardStatus = cardStatus;
    }

    public int getSalonId() {
        return SalonId;
    }

    public void setSalonId(int salon) {
        SalonId = salon;
    }
}
