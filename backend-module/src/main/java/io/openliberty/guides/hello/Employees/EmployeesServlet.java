package io.openliberty.guides.hello.Employees;

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

import io.openliberty.guides.hello.model.Employee;

@WebServlet("/Employees/GetEmployees")
public class EmployeesServlet extends HttpServlet{
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {  
        
        System.out.println("Get for Employee Data");
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa-hibernate-mysql");
        EntityManager em = emf.createEntityManager();

        TypedQuery<Employee> query = em.createNamedQuery("Employee.allRows", Employee.class);
        List<Employee> employees = query.getResultList();

        List<Map<String, Object>> rows = new ArrayList<>();
        for (Employee employee : employees) {
            Map<String, Object> rowData = new HashMap<>();
            rowData.put("EmployeeID", employee.getEmployeeId());
            rowData.put("LastName", employee.getPerson().getLastName());
            rowData.put("FirstName", employee.getPerson().getFirstName());
            rowData.put("Email", employee.getPerson().getEmail());
            rowData.put("Assignment", employee.getAssignment());
            rowData.put("PhonePreselection", employee.getPerson().getPhonePreselection());
            rowData.put("PhoneNumber", employee.getPerson().getPhoneNumber());
            rowData.put("DocumentNumber", employee.getPerson().getDocumentNumber());
            rowData.put("DateOfBirth", employee.getPerson().getDateOfBirth());
            rowData.put("WorkShift", employee.getWorkShift());
            rowData.put("Password", employee.getPassword());
            rows.add(rowData);
        }

            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.writeValueAsString(rows);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(jsonString);
    }
    
}
