package io.openliberty.guides.hello.Customer;

import java.io.BufferedReader;
import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.sql.Date;
import java.util.logging.Logger;


import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.openliberty.guides.hello.model.Customer;
import io.openliberty.guides.hello.model.Person;

@WebServlet("/Customer/AddCustomer")
public class AddCustomerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(AddCustomerServlet.class.getName());

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        BufferedReader reader = request.getReader();
        String line = reader.readLine();
        reader.close();

        logger.info("Received data: " + line);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(line);

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa-hibernate-mysql");
        EntityManager em = emf.createEntityManager();
        try{
            Customer customer = new Customer();

            TypedQuery<Integer> query = em.createNamedQuery("Person.findMaxId", Integer.class);
            int newId = query.getSingleResult();

            Date dateOfBirth = Date.valueOf(root.get("DateOfBirth").asText());


            customer.setCustomerId(newId + 1);
            customer.setAllergy(root.get("Allergy").asText());
            customer.setHandicap(root.get("Handicap").asBoolean());
            customer.setAddress(root.get("Address").asText());
            customer.setSubscription(root.get("Subscription").asBoolean());

            Person person = new Person();
            person.setPersonID(customer.getCustomerId());
            person.setLastName(root.get("LastName").asText());
            person.setFirstName(root.get("FirstName").asText());
            person.setEmail(root.get("Email").asText());
            person.setPhonePreselection(root.get("PhonePreselection").asText());
            person.setPhoneNumber(root.get("PhoneNumber").asText());
            person.setDocumentNumber(root.get("DocumentNumber").asText());
            person.setDateOfBirth(dateOfBirth);

            customer.setPerson(person);

            em.getTransaction().begin();
            em.persist(person);
            em.persist(customer);
            em.getTransaction().commit();

            response.setStatus(HttpServletResponse.SC_CREATED);
            response.setContentType("application/json");
            response.getWriter().println(mapper.writeValueAsString(customer));


              logger.info("Customer created: " + customer.getCustomerId());

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Error occurred while adding customer: " + e.getMessage());

             logger.severe("Error occurred while adding customer: " + e.getMessage());

        } finally {
            em.close();
            emf.close();
        }
    }
}