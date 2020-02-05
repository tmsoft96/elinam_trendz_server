package com.tmsoft.tm.elitrends.Holders;

public class chatUserNotify {
    private String message, from, read;
    private long time;

    public chatUserNotify() {
    }

    public chatUserNotify(String message, String from, String read, long time) {
        this.message = message;
        this.from = from;
        this.read = read;
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getRead() {
        return read;
    }

    public void setRead(String read) {
        this.read = read;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
