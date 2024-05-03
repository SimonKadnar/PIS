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

@WebServlet("/Services/UpdateService")
public class UpdateServiceServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        //read request
        BufferedReader reader = request.getReader();
        String line = reader.readLine();
        reader.close();

        //create tree 
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(line);

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa-hibernate-mysql");
        EntityManager em = emf.createEntityManager();
        try {

            // find service by id
            TypedQuery<Service> query = em.createNamedQuery("Service.findById", Service.class);
            query.setParameter("id", root.path("ServiceID").asInt()); 
            Service service = query.getSingleResult();
        
            //udate service parameters
            service.updateService(
                root.path("Name").asText(), 
                Float.parseFloat((root.path("Cost").asText())),   
                root.path("Availability").asText(),  
                root.path("Description").asText()
                );

            //send service to db 
            em.getTransaction().begin();
            em.merge(service);
            em.getTransaction().commit();
        
            System.out.println("Updated service: " + service);
            
        } catch (NoResultException e) {             
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("Service not found");
        } finally {
            em.close();
            emf.close();
        }
    }
}