package com.example.milkmate;

public class House {
    private String id;
    private String houseNumber;
    private String ownerName;

    public House() {
        // Firestore needs empty constructor
    }

    public House(String id, String houseNumber, String ownerName) {
        this.id = id;
        this.houseNumber = houseNumber;
        this.ownerName = ownerName;
    }

    public String getId() {
        return id;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }
}
