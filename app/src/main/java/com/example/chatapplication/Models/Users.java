package com.example.chatapplication.Models;

import java.util.ArrayList;

public class Users {

    String profilepic , userName , mail, password, userId, lastMessage, status;
    ArrayList<String> groupsIds;
    public Users(String profilepic, String userName, String mail, String password, String userId, String lastMessage, String status) {
        this.profilepic = profilepic;
        this.userName = userName;
        this.mail = mail;
        this.password = password;
        this.userId = userId;
        this.lastMessage = lastMessage;
        this.status = status;
    }

    // SignUp Constructor
    public Users(String userName, String mail, String password) {
        this.userName = userName;
        this.mail = mail;
        this.password = password;
    }

    public Users(){}

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ArrayList<String> getGroups() {
        return groupsIds;
    }

    public void setGroups(ArrayList<String> groups) { this.groupsIds = groupsIds; }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProfilepic() {
        return profilepic;
    }

    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

//    public String getUserId(String key) {
//
//        return userId;
//    }
//
//    public void setUserId(String userId) {
//
//        this.userId = userId;
//    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
