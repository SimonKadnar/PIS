package io.openliberty.guides.hello.Employees;

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

import io.openliberty.guides.hello.model.Employee;
import io.openliberty.guides.hello.model.Person;

@WebServlet("/Employees/DeleteEmployee")
public class DeleteEmployeeServlet  extends HttpServlet{
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

            em.getTransaction().begin();
            em.remove(employee);
            em.getTransaction().commit();

            TypedQuery<Person> query2 = em.createNamedQuery("Person.findById", Person.class);
            query2.setParameter("id", root.path("EmployeeID").asInt());
            Person person = query2.getSingleResult();

            em.getTransaction().begin();
            em.remove(person);
            em.getTransaction().commit();
        
            System.out.println("Deleted employee: " + employee);
            
        } catch (NoResultException e) {             
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("Employee not found");
        } finally {
            em.close();
            emf.close();
        }
    }
    
}
