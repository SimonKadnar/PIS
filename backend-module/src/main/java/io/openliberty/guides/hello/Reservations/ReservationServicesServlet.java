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

import io.openliberty.guides.hello.model.ReservationService;

@WebServlet("/Reservations/GetReservationServices")
public class ReservationServicesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {            

        System.out.println("Get for Reservation Services Data");
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa-hibernate-mysql");
        EntityManager em = emf.createEntityManager();
        
    TypedQuery<ReservationService> query = em.createNamedQuery("ReservationService.allRows", ReservationService.class);
    List<ReservationService> reservationServices = query.getResultList();

    // Transform data
    List<Map<String, Object>> rows = new ArrayList<>();


    for (ReservationService reservationService : reservationServices) {
    Map<String, Object> row = new HashMap<>();
    row.put("ReservationID", reservationService.getReservationId());
    row.put("ServiceID", reservationService.getServiceId());
    rows.add(row);
    }

    ObjectMapper mapper = new ObjectMapper();
    response.setContentType("application/json");
    response.getWriter().write(mapper.writeValueAsString(rows));
    

}
}
