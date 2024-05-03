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
import net.bytebuddy.agent.builder.AgentBuilder.LocationStrategy.Simple;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.codec.digest.DigestUtils;

import io.openliberty.guides.hello.model.Employee;
import io.openliberty.guides.hello.model.Person;

@WebServlet("/Employees/AddEmployee")
public class AddEmployeeServlet extends HttpServlet {
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
        try{
            Employee employee = new Employee();
        
            TypedQuery<Integer> query = em.createNamedQuery("Person.findMaxId", Integer.class);
            int newid = query.getSingleResult();

            System.out.println("Root:");
            System.out.println(root);


            Date dateOfBirth = Date.valueOf(root.path("DateOfBirth").asText());
            Date workShift = Date.valueOf(root.path("WorkShift").asText());

            String password = root.path("Password").asText();
            String hashedPassword = DigestUtils.sha256Hex(password);

            employee.setEmployeeId(newid+1);
            employee.setAssignment(root.path("Assignment").asText());
            employee.setWorkShift(workShift);
            employee.setPassword(hashedPassword);

            Person person = new Person();
            person.setPersonID(newid+1);
            person.setLastName(root.get("LastName").asText());
            person.setFirstName(root.get("FirstName").asText());
            person.setEmail(root.get("Email").asText());
            person.setPhonePreselection(root.get("PhonePreselection").asText());
            person.setPhoneNumber(root.get("PhoneNumber").asText());
            person.setDocumentNumber(root.get("DocumentNumber").asText());
            person.setDateOfBirth(dateOfBirth);

            employee.setPerson(person);

            em.getTransaction().begin();    
            em.persist(person);              
            em.persist(employee);            
            em.getTransaction().commit();   

            response.setStatus(HttpServletResponse.SC_CREATED);
            response.setContentType("application/json");
            response.getWriter().println(mapper.writeValueAsString(employee));
        }

            catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("Employee was not created");
            }
        

    finally{
            em.close();
            emf.close();
        }

    
}
}
