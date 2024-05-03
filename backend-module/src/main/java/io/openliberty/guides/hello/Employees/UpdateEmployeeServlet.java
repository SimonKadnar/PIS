package io.openliberty.guides.hello.Employees;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Date;

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

import org.apache.commons.codec.digest.DigestUtils;

import io.openliberty.guides.hello.model.Employee;

@WebServlet("/Employees/UpdateEmployee")
public class UpdateEmployeeServlet extends HttpServlet{
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

            TypedQuery<Employee> query = em.createNamedQuery("Employee.findById", Employee.class);
            query.setParameter("id", root.path("EmployeeID").asInt()); 
            Employee employee = query.getSingleResult();

            Date dateOfBirth = Date.valueOf(root.path("DateOfBirth").asText());
            Date workShift = Date.valueOf(root.path("WorkShift").asText());

             String password = root.path("Password").asText();
             String hashedPassword = DigestUtils.sha256Hex(password);

        
            employee.updateEmployee(
                root.path("LastName").asText(),
                root.path("FirstName").asText(),
                root.path("Email").asText(),
                root.path("Assignment").asText(),
                root.path("PhonePreselection").asText(),
                root.path("PhoneNumber").asText(),
                root.path("DocumentNumber").asText(),
                dateOfBirth,
                workShift,
                hashedPassword
                );
            em.getTransaction().begin();
            em.merge(employee);
            em.getTransaction().commit();
        
            System.out.println("Updated employee: " + employee);
            
        } catch (NoResultException e) {             
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("Employee not found");
        } finally {
            em.close();
            emf.close();
        }
    }
    
}
