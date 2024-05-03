package io.openliberty.guides.hello.model;

import jakarta.persistence.*;

@Entity
@NamedQuery(name = "Customer.allRows", query = "SELECT c FROM Customer c")
@NamedQuery(name = "Customer.findMaxId", query = "SELECT MAX(c.customerId) FROM Customer c")
@Table(name = "Customer")
public class Customer {

    @Id
    @Column(name = "CustomerID")
    private int customerId;

    @Column(name = "Allergy")
    private String allergy;

    @Column(name = "Handicap")
    private boolean handicap;

    @Column(name = "Address")
    private String address;

    @Column(name = "Subscription")
    private boolean subscription;

    @OneToOne
    @JoinColumn(name = "CustomerID")
    private Person person;

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getAllergy() {
        return allergy;
    }

    public void setAllergy(String allergy) {
        this.allergy = allergy;
    }

    public boolean isHandicap() {
        return handicap;
    }

    public void setHandicap(boolean handicap) {
        this.handicap = handicap;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isSubscription() {
        return subscription;
    }

    public void setSubscription(boolean subscription) {
        this.subscription = subscription;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", allergy='" + allergy + '\'' +
                ", handicap=" + handicap +
                ", address='" + address + '\'' +
                ", subscription=" + subscription +
                ", person=" + person +
                '}';
    }
}