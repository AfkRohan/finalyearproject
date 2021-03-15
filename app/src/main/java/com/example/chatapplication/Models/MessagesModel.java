package com.example.chatapplication.Models;

public class MessagesModel {
    String Id,message;
    Long timestamp;

    public MessagesModel(String id, String message, Long timestamp) {
        Id = id;
        this.message = message;
        this.timestamp = timestamp;
    }

    public MessagesModel(String id, String message) {
        Id = id;
        this.message = message;
    }

    public MessagesModel(){
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }


}
