package io.openliberty.guides.hello.Customer;

import java.io.BufferedReader;
import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.sql.SQLException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.databind.node.ObjectNode;

import io.openliberty.guides.hello.DatabaseUtil;

@WebServlet("/Customer/UpdateCustomer")
public class UpdateCustomerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        BufferedReader reader = request.getReader();
        String line = reader.readLine();
        reader.close();
        
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(line);

        JsonNode dateOfBirthNode = root.get("dateOfBirth");
        System.out.println(root);
        if (dateOfBirthNode.isNumber()) {
            long milliseconds = dateOfBirthNode.asLong();
            Date dateOfBirth = new Date(milliseconds);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDateOfBirth = dateFormat.format(dateOfBirth);

            ((ObjectNode) root).put("dateOfBirth", formattedDateOfBirth);
        }

        System.out.println(root);

        try 
        {
            DatabaseUtil.Update(root, "Person", "PersonID");
            DatabaseUtil.Update(root, "Customer", "CustomerID");
            
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}