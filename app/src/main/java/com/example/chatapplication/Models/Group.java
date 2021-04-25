package com.example.chatapplication.Models;

import java.util.ArrayList;

public class Group {
    String groupId, groupName, groupAdminId, groupAdminName, groupDesc, groupIcon;
    Long createdAt;
    ArrayList<String> groupMembers;

    public String getGroupIcon() {
        return groupIcon;
    }

    public void setGroupIcon(String groupIcon) {
        this.groupIcon = groupIcon;
    }

    public Group(String groupName, String groupAdminId, Long createdAt, ArrayList<String> groupMembers) {
        this.groupName = groupName;
        this.groupAdminId = groupAdminId;
        this.createdAt = createdAt;
        this.groupMembers = groupMembers;
    }

    public Group() {

    }
    public Group(String groupId, String groupName, String groupAdminId, String groupAdminName, Long createdAt, ArrayList<String> groupMembers) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupAdminId = groupAdminId;
        this.groupAdminName = groupAdminName;
        this.createdAt = createdAt;
        this.groupMembers = groupMembers;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupAdminId() {
        return groupAdminId;
    }

    public void setGroupAdminId(String groupAdminId) {
        this.groupAdminId = groupAdminId;
    }

    public String getGroupAdminName() {
        return groupAdminName;
    }

    public void setGroupAdminName(String groupAdminName) {
        this.groupAdminName = groupAdminName;
    }

    public String getGroupDesc() {
        return groupDesc;
    }

    public void setGroupDesc(String groupDesc) {
        this.groupDesc = groupDesc;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public ArrayList<String> getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(ArrayList<String> groupMembers) {
        this.groupMembers = groupMembers;
    }
}
