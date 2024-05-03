package io.openliberty.guides.hello.model;

import jakarta.persistence.*;

@NamedQuery(name = "Room.findById", query = "SELECT r FROM Room r WHERE r.roomId = :id")
@NamedQuery(name = "Room.findMaxId", query = "SELECT MAX(r.roomId) FROM Room r")
@NamedQuery(name = "Room.allRows", query = "SELECT r FROM Room r")
@Entity
@Table(name = "Room")
public class Room {

    @Id
    @Column(name = "RoomID")
    private int roomId;

    @Column(name = "TypeRoom")
    private String typeRoom;

    @Column(name = "Cost")
    private float cost;

    @Column(name = "Equip")
    private String equip;

    @Column(name = "State")
    private String state;

    @Column(name = "Beds")
    private int beds;

    // Getters and Setters
    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getTypeRoom() {
        return typeRoom;
    }

    public void setTypeRoom(String typeRoom) {
        this.typeRoom = typeRoom;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public String getEquip() {
        return equip;
    }

    public void setEquip(String equip) {
        this.equip = equip;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getBeds() {
        return beds;
    }

    public void setBeds(int beds) {
        this.beds = beds;
    }

    public void updateRoom(String typeRoom, float cost, String equip, String state, Integer beds) {
        this.typeRoom = typeRoom;
        this.cost = cost;
        this.equip = equip;
        this.state = state;
        this.beds = beds;
    }

    @Override
    public String toString() {
        return "Room{" +
                "roomId=" + roomId +
                ", typeRoom='" + typeRoom + '\'' +
                ", cost=" + cost +
                ", equip='" + equip + '\'' +
                ", state='" + state + '\'' +
                ", beds=" + beds +
                '}';
    }
}
