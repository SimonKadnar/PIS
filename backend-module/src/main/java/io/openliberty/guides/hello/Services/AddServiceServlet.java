package io.openliberty.guides.hello.Services;

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

import io.openliberty.guides.hello.model.Service;

@WebServlet("/Services/AddService")
public class AddServiceServlet extends HttpServlet {
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
            //create and update new service 
            Service service = new Service();

            //get the biggest Serviceid
            TypedQuery<Integer> query = em.createNamedQuery("Service.findMaxId", Integer.class);
            int newid = query.getSingleResult();
        
            service.setServiceId(newid+1);
            //udate service parameters
            service.updateService(
                root.path("Name").asText(), 
                Float.parseFloat(root.path("Cost").asText()),   
                root.path("Availability").asText(),  
                root.path("Description").asText()
                );

            //send service to db 
            em.getTransaction().begin();
            em.persist(service);
            em.getTransaction().commit();
        
            System.out.println("Service was created");
            
        } catch (NoResultException e) {             
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("Service was not created");
        } finally {
            em.close();
            emf.close();
        }
    }
}