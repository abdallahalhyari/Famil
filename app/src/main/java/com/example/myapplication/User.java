package com.example.myapplication;


public class User {
    String Email;
    String name;
    String phone;
    String Id;
    String location;

    public void setId(String id) {
        Id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getId() {
        return Id;
    }

    public String getEmail() {
        return Email;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }
}