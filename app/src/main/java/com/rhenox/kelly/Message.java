package com.rhenox.kelly;

public class Message {
    private String text;
    private String time;
    private boolean belongsToCurrentUser;

    public Message(){

    }
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

    public void setText(String text) {
        this.text = text;
    }

    public void setBelongsToCurrentUser(boolean belongsToCurrentUser) {
        this.belongsToCurrentUser = belongsToCurrentUser;
    }

    public boolean isBelongsToCurrentUser(){
        return belongsToCurrentUser;
    }
}
