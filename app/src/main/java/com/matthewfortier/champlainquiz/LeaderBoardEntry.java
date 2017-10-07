package com.matthewfortier.champlainquiz;

public class LeaderBoardEntry {

    private String mInitials;
    private int mScore;

    public String getInitials() {
        return mInitials;
    }

    public void setInitials(String mInitials) {
        this.mInitials = mInitials;
    }

    public int getScore() {
        return mScore;
    }

    public void setScore(int mScore) {
        this.mScore = mScore;
    }

    public LeaderBoardEntry() {
    }

    public LeaderBoardEntry(String mInitials, int mScore) {
        this.mInitials = mInitials;
        this.mScore = mScore;
    }
}
