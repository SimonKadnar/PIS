package io.openliberty.guides.hello.Reservations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.openliberty.guides.hello.model.Reservation;

@WebServlet("/Reservations/GetReservations")
public class ReservationsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("Get for Reservation Data");
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa-hibernate-mysql");
        EntityManager em = emf.createEntityManager();

        TypedQuery<Reservation> query = em.createNamedQuery("Reservation.allRows", Reservation.class);
        List<Reservation> reservations = query.getResultList();

        List<Map<String, Object>> rows = new ArrayList<>();
        for (Reservation reservation : reservations) {
            Map<String, Object> rowData = new HashMap<>();
            rowData.put("ReservationID", reservation.getReservationId());
            rowData.put("RoomID", reservation.getRoom().getRoomId());
            rowData.put("Start", reservation.getStart());
            rowData.put("End", reservation.getEnd());
            rowData.put("CommingTime", reservation.getCommingTime());
            rowData.put("LeavingTime", reservation.getLeavingTime());
            rowData.put("State", reservation.getState());
            rowData.put("Cost", reservation.getCost());
            rowData.put("BusinessGuest", reservation.isBusinessGuest());
            rowData.put("Parking", reservation.isParking());
            rowData.put("CustomerID", reservation.getCustomer().getCustomerId());
            rows.add(rowData);
        }

        ObjectMapper mapper = new ObjectMapper();
        response.setContentType("application/json");
        response.getWriter().write(mapper.writeValueAsString(rows));
        

    }
}

        