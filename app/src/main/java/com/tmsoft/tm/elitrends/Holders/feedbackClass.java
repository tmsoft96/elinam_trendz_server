package com.tmsoft.tm.elitrends.Holders;

public class feedbackClass {
    private String userId, feedback;
    private long time;
    private int number;

    public feedbackClass() {
    }

    public feedbackClass(String userId, String feedback, long time, int number) {
        this.userId = userId;
        this.feedback = feedback;
        this.time = time;
        this.number = number;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
