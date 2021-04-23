package com.example.chatapplication.notification;

public class Data {
    private String sented;
    private String title;
    private String body;
    private String user;
    private int icon;

    public Data(String sented, String title, String body, String user, int icon) {
        this.sented = sented;
        this.title = title;
        this.body = body;
        this.user = user;
        this.icon = icon;
    }

    public Data() {
    }

    public String getSented() {
        return sented;
    }

    public void setSented(String sented) {
        this.sented = sented;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
