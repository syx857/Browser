package com.tech.domain;

public class User {

    public String name;
    public String password;
    public String phoneNumber;

    public User(String nickname, String password, String phoneNumber) {
        this.name = nickname;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }

    public User(String phoneNumber, String password) {
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

    public User(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
 }
