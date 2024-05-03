package io.openliberty.guides.hello.model;

import jakarta.persistence.*;
import java.util.Date;

@NamedQuery(name = "Employee.findByEmail",
            query = "SELECT e FROM Employee e JOIN e.person p WHERE p.email = :email")
@NamedQuery(name = "Employee.allRows", query = "SELECT e FROM Employee e")
@NamedQuery(name = "Employee.findById", query = "SELECT e FROM Employee e WHERE e.employeeId = :id")
@NamedQuery(name = "Employee.findMaxId", query = "SELECT MAX(e.employeeId) FROM Employee e")
@Entity
@Table(name = "Employee")
public class Employee {

    @Id
    @Column(name = "EmployeeID")
    private int employeeId;

    @Column(name = "Assignment")
    private String assignment;

    @Column(name = "Password")
    private String password;

    @Temporal(TemporalType.DATE)
    @Column(name = "WorkShift")
    private Date workShift;

    @OneToOne
    @JoinColumn(name = "EmployeeID")
    private Person person;

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getAssignment() {
        return assignment;
    }

    public void setAssignment(String assignment) {
        this.assignment = assignment;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getWorkShift() {
        return workShift;
    }

    public void setWorkShift(Date workShift) {
        this.workShift = workShift;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public void updateEmployee(String lastName, String firstName, String email, String assignment, String phonePreselection,String phoneNumber, String documentNumber, Date dateOfBirth, Date workShift, String password) {
        this.person.setLastName(lastName);
        this.person.setFirstName(firstName);
        this.person.setEmail(email);
        this.assignment = assignment;
        this.person.setPhonePreselection(phonePreselection);
        this.person.setPhoneNumber(phoneNumber);
        this.person.setDocumentNumber(documentNumber);
        this.person.setDateOfBirth(dateOfBirth);
        this.workShift = workShift;
        this.password = password;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "employeeId=" + employeeId +
                ", assignment='" + assignment + '\'' +
                ", password='" + password + '\'' +
                ", workShift=" + workShift +
                ", person=" + person +
                '}';
    }
}
