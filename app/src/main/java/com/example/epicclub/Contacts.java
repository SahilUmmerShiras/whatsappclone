package com.example.epicclub;

public class Contacts {

    public String dp, status, username;

    public Contacts(String dp, String status, String username) {
        this.dp = dp;
        this.status = status;
        this.username = username;
    }


    public Contacts() {
    }

    public String getDp() {
        return dp;
    }

    public void setDp(String dp) {
        this.dp = dp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
