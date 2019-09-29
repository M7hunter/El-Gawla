package it_geeks.info.elgawla.repository.Models;

public class VoteChild {

    private int Id;

    private String Title, Count;

    private boolean Voted;

    public VoteChild(int id, String title, String count, boolean voted) {
        Id = id;
        Title = title;
        Count = count;
        Voted = voted;
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

    public String getCount() {
        return Count;
    }

    public void setCount(String count) {
        Count = count;
    }

    public boolean isVoted() {
        return Voted;
    }

    public void setVoted(boolean voted) {
        Voted = voted;
    }
}
