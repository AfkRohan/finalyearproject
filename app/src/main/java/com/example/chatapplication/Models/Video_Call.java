package com.example.chatapplication.Models;

public class Video_Call {

    private String sRoom;
    private String rRoom;
    private String currentTime;

    public String getsRoom() {
        return sRoom;
    }

    public void setsRoom(String sRoom) {
        this.sRoom = sRoom;
    }

    public String getrRoom() {
        return rRoom;
    }

    public void setrRoom(String rRoom) {
        this.rRoom = rRoom;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public Video_Call(String sRoom, String rRoom, String currentTime) {
        this.sRoom = sRoom;
        this.rRoom = rRoom;
        this.currentTime = currentTime;
    }

    public Video_Call() {
    }
}
