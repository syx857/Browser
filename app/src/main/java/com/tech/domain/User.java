package com.tech.domain;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {
    @PrimaryKey
    public String userID;
    public String nickname;
    public String phoneNumber;
    public String password;
}
