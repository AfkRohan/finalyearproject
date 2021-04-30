package com.example.chatapplication.Notifications;

public class Data {
    private String user;
    private String body;
    private String title;
    private String sent;
    private int icon;

    public Data(String user, String body, String title, String sented, int icon) {
        this.user = user;
        this.body = body;
        this.title = title;
        this.sent = sented;
        this.icon = icon;
    }

    public Data(){

    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSented() {
        return sent;
    }

    public void setSented(String sented) {
        this.sent = sented;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
