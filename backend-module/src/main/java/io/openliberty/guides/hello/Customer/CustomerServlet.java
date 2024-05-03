package io.openliberty.guides.hello.Customer;

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

import io.openliberty.guides.hello.model.Customer;


@WebServlet("/Customer/GetCustomers")
public class CustomerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {  

          

            System.out.println("Get for Customer Data");
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa-hibernate-mysql");
            EntityManager em = emf.createEntityManager();

            TypedQuery<Customer> query = em.createNamedQuery("Customer.allRows", Customer.class);
            List<Customer> customers = query.getResultList();

            for (Customer customer : customers) {
                System.out.println(customer);
            }

            

            List<Map<String, Object>> rows = new ArrayList<>();
            for (Customer customer : customers) {
                Map<String, Object> rowData = new HashMap<>();
                rowData.put("customerId", customer.getCustomerId());
                rowData.put("allergy", customer.getAllergy());
                rowData.put("handicap", customer.isHandicap());
                rowData.put("address", customer.getAddress());
                rowData.put("subscription", customer.isSubscription());
                rowData.put("person", customer.getPerson());
                rows.add(rowData);
            }



            ObjectMapper mapper = new ObjectMapper();
            response.setContentType("application/json");
            for (Map<String, Object> row : rows) {
                System.out.println(row);
            }
            response.getWriter().write(mapper.writeValueAsString(rows));
    }
}


