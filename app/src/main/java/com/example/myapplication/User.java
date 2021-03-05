package com.example.myapplication;



public class User  {
    private String Email;
    private String name;

    public User(String Email, String name) {
        this.Email = Email;
        this.name = name;

    }



    public String getEmail() {
        return Email;
    }
    public String getName() {
        return name;
    }

}