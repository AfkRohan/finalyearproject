package com.example.chatapplication.Models;

public class MessagesModel {
    String Id, userName, message, messageId;
    Long timestamp;

    public MessagesModel(String id, String message, Long timestamp) {
        Id = id;
        this.message = message;
        this.timestamp = timestamp;
    }

    public MessagesModel(String id, String userName, String message) {
        Id = id;
        this.userName = userName;
        this.message = message;
    }
    public MessagesModel(String id, String message) {
        Id = id;
        this.message = message;
    }

    public MessagesModel(){
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getUserName() { return userName; }

    public void setUserName(String userName) { this.userName = userName; }

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
