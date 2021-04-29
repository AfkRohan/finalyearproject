package com.example.chatapplication.Models;

public class MessagesModel {
    String Id, userName, message, messageId,type,first;
    Long timestamp;
    boolean notificationReceived;

    public String isFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public boolean isNotificationReceived() {
        return notificationReceived;
    }

    public void setNotificationReceived(boolean notificationReceived) {
        this.notificationReceived = notificationReceived;
    }

    public MessagesModel(String id, String message, Long timestamp) {
        Id = id;
        this.message = message;
        this.timestamp = timestamp;
        this.type = "text";
    }

    public MessagesModel(String id, String userName, String message) {
        Id = id;
        this.userName = userName;
        this.message = message;
        this.type = "text";
    }

    //New Constructor for Receiving Notification.
    public MessagesModel(String id, String message, boolean notificationReceived){
        Id = id;
        this.message = message;
        this.notificationReceived = notificationReceived;
    }

    public MessagesModel(String id, String message) {
        Id = id;
        this.message = message;
        this.type="text";
    }

    public MessagesModel(String id,String message,String type,Long timestamp){
        Id = id;
        this.message=message;
        this.type=type;
        this.timestamp=timestamp;
    }

    public MessagesModel(String id, String userName, String message, String messageId, String type, String first, Long timestamp, boolean notificationReceived) {
        Id = id;
        this.userName = userName;
        this.message = message;
        this.messageId = messageId;
        this.type = type;
        this.first = first;
        this.timestamp = timestamp;
        this.notificationReceived = notificationReceived;
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

    public  String getType(){return type;}

    public void setType(String type){this.type=type;}

}
