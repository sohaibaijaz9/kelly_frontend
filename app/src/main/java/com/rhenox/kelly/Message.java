package com.rhenox.kelly;

public class Message {
    private String text;
    private String time;
    private boolean belongsToCurrentUser;

    public Message(String text, String time, boolean belongsToCurrentUser) {
        this.text = text;
        this.time = time;
        this.belongsToCurrentUser = belongsToCurrentUser;
    }

    public String getText() {
        return text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isBelongsToCurrentUser(){
        return belongsToCurrentUser;
    }
}
