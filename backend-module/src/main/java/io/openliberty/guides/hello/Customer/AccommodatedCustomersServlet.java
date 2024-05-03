package io.openliberty.guides.hello.Customer;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import io.openliberty.guides.hello.model.Person;
import io.openliberty.guides.hello.model.Reservation;
import io.openliberty.guides.hello.model.Room;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

@WebServlet("/AccommodatedCustomers/GetAccommodatedCustomers")
public class AccommodatedCustomersServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { 

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa-hibernate-mysql");
        EntityManager em = emf.createEntityManager();
        try {

            TypedQuery<Object[]> query = em.createNamedQuery("Reservation.findCurrentReservations", Object[].class);
            query.setParameter("currentDate", new Date());
            List<Object[]> results = query.getResultList();

            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();

            for (Object[] result : results) {
                Reservation reservation = (Reservation) result[0];
                Person person = (Person) result[2];
                Room room = (Room) result[3];

                JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                objectBuilder.add("lastName", person.getLastName());
                objectBuilder.add("firstName", person.getFirstName());
                objectBuilder.add("roomNumber", room.getRoomId());
                objectBuilder.add("email", person.getEmail());
                objectBuilder.add("phoneNumber", person.getPhoneNumber());
                objectBuilder.add("startDate", reservation.getStart().toString());
                objectBuilder.add("endDate", reservation.getEnd().toString());
                objectBuilder.add("personID", person.getPersonID());

                arrayBuilder.add(objectBuilder);
            }

            String jsonString = arrayBuilder.build().toString();

            response.setContentType("application/json");
            response.getWriter().write(jsonString);
            response.setStatus(HttpServletResponse.SC_OK);
                    
        } catch (NoResultException e) {
            System.out.println("AccommodatedCustomersServlet: doGet: NoResultException");
        } finally {
            em.close();
            emf.close();
        }
        
    }
}