package io.openliberty.guides.hello.Customer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.openliberty.guides.hello.DatabaseUtil;

@WebServlet("/Customer/GetCustomer")
public class CustomerDetailServlet extends HttpServlet{
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {  
                
            System.out.println("Received request for customer detail");
            BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            
            String requestData = sb.toString();
            
            try {
                Connection connection = DatabaseUtil.getConnection();
                String query = "SELECT * FROM Person LEFT JOIN Customer on Person.PersonID = Customer.CustomerID WHERE Customer.CustomerID = " + requestData + ";";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery();
                System.out.println(resultSet);

                List<Map<String, Object>> rows = new ArrayList<>();
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();

                while (resultSet.next()) 
                {
                    Map<String, Object> rowData = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) 
                    {
                        String columnName = metaData.getColumnName(i);
                        Object value = resultSet.getObject(i);
                        rowData.put(columnName, value);
                    }
                    rows.add(rowData);
                }

                ObjectMapper mapper = new ObjectMapper();
                String jsonString = mapper.writeValueAsString(rows);

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(jsonString);
            } 
            catch (SQLException e) {
                e.printStackTrace();
            }
    }
}