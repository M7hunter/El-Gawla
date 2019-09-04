package it_geeks.info.gawla_app.repository.Models;

import java.util.List;

public class Vote {

    private int Id;

    private String Color, Icon, Title, Count;

    private boolean Voted;

    private List<VoteChild> voteChildList;

    public Vote(int id, String color, String icon, String title, String count, boolean voted, List<VoteChild> voteChildList) {
        Id = id;
        Color = color;
        Icon = icon;
        Title = title;
        Count = count;
        Voted = voted;
        this.voteChildList = voteChildList;
    }

    public Vote(int vote_id, String vote_title, String vote_count) {
        Id = vote_id;
        Title = vote_title;
        Count = vote_count;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getColor() {
        return Color;
    }

    public void setColor(String color) {
        Color = color;
    }

    public String getIcon() {
        return Icon;
    }

    public void setIcon(String icon) {
        Icon = icon;
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

    public List<VoteChild> getVoteChildList() {
        return voteChildList;
    }

    public void setVoteChildList(List<VoteChild> voteChildList) {
        this.voteChildList = voteChildList;
    }
}
