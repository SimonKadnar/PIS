package io.openliberty.guides.hello.model;

import jakarta.persistence.*;
import java.util.Date;

@NamedQuery(name = "Person.findById", query = "SELECT p FROM Person p WHERE p.personID = :id")
@NamedQuery(name = "Person.findMaxId", query = "SELECT MAX(p.personID) FROM Person p")
@Entity
@Table(name = "Person")
public class Person {

    @Id
    @Column(name = "PersonID")
    private int personID;

    @Column(name = "LastName")
    private String lastName;

    @Column(name = "FirstName")
    private String firstName;

    @Column(name = "Email")
    private String email;

    @Column(name = "PhonePreselection")
    private String phonePreselection;

    @Column(name = "PhoneNumber")
    private String phoneNumber;

    @Column(name = "DocumentNumber")
    private String documentNumber;

    @Temporal(TemporalType.DATE)
    @Column(name = "dateOfBirth")
    private Date dateOfBirth;

    public int getPersonID() {
        return personID;
    }

    public void setPersonID(int personID) {
        this.personID = personID;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhonePreselection() {
        return phonePreselection;
    }

    public void setPhonePreselection(String phonePreselection) {
        this.phonePreselection = phonePreselection;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @Override
    public String toString() {
        return "Person{" +
                "personID=" + personID +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", email='" + email + '\'' +
                ", phonePreselection='" + phonePreselection + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", documentNumber='" + documentNumber + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                '}';
    }
}
