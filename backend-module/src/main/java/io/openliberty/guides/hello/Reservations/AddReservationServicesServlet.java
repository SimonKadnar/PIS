package io.openliberty.guides.hello.Reservations;

import java.io.BufferedReader;
import java.io.IOException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Persistence;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.openliberty.guides.hello.model.Reservation;
import io.openliberty.guides.hello.model.ReservationService;
import io.openliberty.guides.hello.model.Service;

@WebServlet("/Reservations/AddReservationServices")
public class AddReservationServicesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        BufferedReader reader = request.getReader();
        String line = reader.readLine();
        reader.close();
        
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(line);

        int[] serviceIds = mapper.readValue(root.path("serviceIds").toString(), int[].class);
        int reservationId = root.path("reservationId").asInt();

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa-hibernate-mysql");
        EntityManager em = emf.createEntityManager();
        try {
            Reservation reservation = em.find(Reservation.class, reservationId);
            
            if (reservation != null) {
                for (int serviceId : serviceIds) {
                    Service service = em.find(Service.class, serviceId);
                
                    if (service != null) {
                        ReservationService reservationService = new ReservationService(serviceId, reservationId);
                        reservationService.setService(service);
                        reservationService.setReservation(reservation);
                
                        em.getTransaction().begin();
                        em.persist(reservationService);
                        em.getTransaction().commit();
                    }
                }
            
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.getWriter().write("Reservation services added successfully");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("Reservation not found");
            }
        } catch (NoResultException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("Failed to add reservation services");
        } finally {
            em.close();
            emf.close();
        }
    }
}