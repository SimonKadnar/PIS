package io.openliberty.guides.hello.model;

import jakarta.persistence.*;

@Entity
@NamedQuery(name = "Service.findById", query = "SELECT s FROM Service s WHERE s.serviceId = :id")
@NamedQuery(name = "Service.findMaxId", query = "SELECT MAX(s.serviceId) FROM Service s")
@NamedQuery(name = "Service.allRows", query = "SELECT s FROM Service s")
@Table(name = "Service")
public class Service {

    @Id
    @Column(name = "ServiceID")
    private int serviceId;

    @Column(name = "Name")
    private String name;

    @Column(name = "Cost")
    private float cost;

    @Column(name = "Availability")
    private String availability;

    @Column(name = "Description")
    private String description;

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void updateService(String name, float cost, String availability, String description) {
        this.name = name;
        this.cost = cost;
        this.availability = availability;
        this.description = description;
    }

    @Override
    public String toString() {
        return "Service{" +
                "serviceId=" + serviceId +
                ", name='" + name + '\'' +
                ", cost=" + cost +
                ", availability='" + availability + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
