package com.example.se07203_b5;

public class User {
    private int id;
    private String username;
    private String password;
    private String email;

    public User(int id, String username, String password, String email)
    {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public String getUsername(){
        return this.username;
    }

    public String getPassword(){
        return this.password;
    }
    public String getEmail(){
        return this.email;
    }

    public int getId(){
        return this.id;
    }
}
