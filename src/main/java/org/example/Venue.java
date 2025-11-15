package org.example;

import java.io.Serializable;

public class Venue implements Serializable {
    private static final long serialVersionUID = 1L;
    private String building;
    private String room;

    public Venue() {}

    public Venue(String building, String room) {
        this.building = building;
        this.room = room;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    @Override
    public String toString() {
        return building + " - " + room;
    }
}
