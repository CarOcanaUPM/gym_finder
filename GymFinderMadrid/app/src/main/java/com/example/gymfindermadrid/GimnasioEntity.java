package com.example.gymfindermadrid;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "gimnasios")
public class GimnasioEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String address;
    private String phoneNumber;
    private String website;
    private Float rating;
    private String imagePath;
    private String placeId; // Añadido si vas a guardar también el placeId de Google

    // Constructor
    public GimnasioEntity(String name, String address, Float rating, String imagePath) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.website = website;
        this.rating = rating;
        this.imagePath = imagePath;
    }

    // Constructor vacío (necesario para Room si usas setters)
    public GimnasioEntity() {}

    // Getters y setters

    public int getId() {
        return id;
    }

    public void setId(int i){this.id =i;}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }
}
