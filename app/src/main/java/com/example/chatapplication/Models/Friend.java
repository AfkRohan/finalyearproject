package com.example.chatapplication.Models;

public class Friend {
    public String userName, status, profilePic;

    public Friend(){

    }
    public Friend(String userName, String status, String profilePic) {
        this.userName = userName;
        this.status = status;
        this.profilePic = profilePic;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

}
