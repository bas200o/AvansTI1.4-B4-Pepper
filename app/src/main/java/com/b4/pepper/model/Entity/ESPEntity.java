package com.b4.pepper.model.Entity;

public class ESPEntity {

    // attributes
    private int id;
    private int seats;
    private boolean isAvailable;

    // getters
    public int getId() {

        return this.id;
    }

    public int getSeats() {

        return this.seats;
    }

    public boolean isAvailable() {

        return this.isAvailable;
    }

    // setters
    public void setId(int id) {

        this.id = id;
    }

    public void setSeats(int seats) {

        this.seats = seats;
    }

    public void setAvailable(boolean available) {

        this.isAvailable = available;
    }
}
