package io.openliberty.guides.hello.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.*;

@Embeddable
public class ReservationServiceId implements Serializable {

    @Column(name = "ReservationID")
    private int reservationId;

    @Column(name = "ServiceID")
    private int serviceId;

    
    public ReservationServiceId() {
    }

    public ReservationServiceId(int reservationId, int serviceId) {
        this.reservationId = reservationId;
        this.serviceId = serviceId;
    }

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReservationServiceId)) return false;
        ReservationServiceId that = (ReservationServiceId) o;
        return reservationId == that.reservationId &&
                serviceId == that.serviceId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(reservationId, serviceId);
    }
}
