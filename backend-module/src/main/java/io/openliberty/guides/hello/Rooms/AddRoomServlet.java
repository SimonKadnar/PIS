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

@WebServlet("/Rooms/AddRoom")
public class AddRoomServlet extends HttpServlet {
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
            Room room = new Room();

            room.setRoomId(root.path("RoomID").asInt());
            room.updateRoom(
                root.path("TypeRoom").asText(), 
                Float.parseFloat(root.path("Cost").asText()),  
                root.path("Equip").asText(),  
                root.path("State").asText(),  
                root.path("Beds").asInt()
                );

            em.getTransaction().begin();    
            em.persist(room);               
            em.getTransaction().commit();   
        
            System.out.println("Room was created");
            
        } catch (NoResultException e) {             
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("Room was not created");
        } finally {
            em.close();
            emf.close();
        }
    }
}