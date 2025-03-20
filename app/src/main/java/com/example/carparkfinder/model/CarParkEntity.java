package com.example.carparkfinder.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/*
Creates a Room Database table named favorite_carparks
Stores car park ID, name, and location
Uses auto-incremented id as the primary key
Provides getter and setter methods
 */

@Entity(tableName = "favorite_carparks")
public class CarParkEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String carParkId;
    private String name;
    private String location;

    // Constructor
    public CarParkEntity(String carParkId, String name, String location) {
        this.carParkId = carParkId;
        this.name = name;
        this.location = location;
    }

    // Getters
    public int getId() { return id; }
    public String getCarParkId() { return carParkId; }
    public String getName() { return name; }
    public String getLocation() { return location; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setCarParkId(String carParkId) { this.carParkId = carParkId; }
    public void setName(String name) { this.name = name; }
    public void setLocation(String location) { this.location = location; }
}

