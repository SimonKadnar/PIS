package io.openliberty.guides.hello.Rooms;

import java.io.BufferedReader;
import java.io.IOException;

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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.openliberty.guides.hello.model.Room;

@WebServlet("/Rooms/DeleteRoom")
public class DeleteRoomServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        BufferedReader reader = request.getReader();
        String line = reader.readLine();
        reader.close();
        
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(line);


        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa-hibernate-mysql");
        EntityManager em = emf.createEntityManager();
        try {

            TypedQuery<Room> query = em.createNamedQuery("Room.findById", Room.class);
            query.setParameter("id", root.path("RoomID").asInt()); 
            Room room = query.getSingleResult();

            em.getTransaction().begin();
            em.remove(room);
            em.getTransaction().commit();
        
            System.out.println("Deleted room: " + room);
            
        } catch (NoResultException e) {             
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("Room not found");
        } finally {
            em.close();
            emf.close();
        }
    }
}