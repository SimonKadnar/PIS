package io.openliberty.guides.hello.model;

import jakarta.persistence.*;

@Entity
@NamedQuery(name = "ReservationService.findByReservationId", query = "SELECT rs FROM ReservationService rs WHERE rs.id.reservationId = :reservationId")
@NamedQuery(name = "ReservationService.allRows", query = "SELECT rs FROM ReservationService rs")
@Table(name = "ReservationService")
public class ReservationService {

    @EmbeddedId
    private ReservationServiceId id;

    @ManyToOne
    @MapsId("reservationId") 
    @JoinColumn(name = "ReservationID")
    private Reservation reservation;

    @ManyToOne
    @MapsId("serviceId") 
    @JoinColumn(name = "ServiceID")
    private Service service;

    
    public ReservationService() {
    }

    

    public ReservationService(int serviceId, int reservationId) {
        this.id = new ReservationServiceId(serviceId, reservationId);
        id.setServiceId(serviceId);
        id.setReservationId(reservationId);
        
    }

    public ReservationServiceId getId() {
        return id;
    }

    public void setId(ReservationServiceId id) {
        this.id = id;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public int getServiceId() {
        return id.getServiceId();
    }

    public int getReservationId() {
        return id.getReservationId();
    }

    public void setServiceId(int serviceId) {
        id.setServiceId(serviceId);
    }

    public void setReservationId(int reservationId) {
        id.setReservationId(reservationId);
    }



    @Override
    public String toString() {
        return "ReservationService{" +
                "id=" + id +
                '}';
    }
}
