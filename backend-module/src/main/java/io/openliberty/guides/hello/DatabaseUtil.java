package io.openliberty.guides.hello;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.fasterxml.jackson.databind.JsonNode;

public class DatabaseUtil {
    public static Connection getConnection() throws SQLException 
    {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            throw new SQLException("MySQL JDBC Driver nenalezen.", e);
        }
        String jdbcUrl = "jdbc:mysql://localhost:3306/test_db";
        String username = "michal";
        String password = "average";
        return DriverManager.getConnection(jdbcUrl, username, password);
    }
    public static void initializeDatabase() throws SQLException, IOException 
    {
        try (Connection connection = getConnection()) {
            InputStream inputStream = DatabaseUtil.class.getResourceAsStream("/db/init.sql");
            StringBuilder stringBuilder = new StringBuilder();
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
            }
            String sqlScript = stringBuilder.toString();
            Statement statement = connection.createStatement();
            String[] sqlCommands1 = sqlScript.split(";");

            for (String sqlCommand : (sqlCommands1)) {
                sqlCommand = sqlCommand.trim(); 
                if (!sqlCommand.isEmpty()) { 
                    statement.execute(sqlCommand);
                }
            }

            InputStream inputStream2 = DatabaseUtil.class.getResourceAsStream("/db/init_data.sql");
            StringBuilder stringBuilder2 = new StringBuilder();
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream2))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder2.append(line).append("\n");
                }
            }
            String sqlScript2 = stringBuilder2.toString();
            String[] sqlCommands2 = sqlScript2.split(";");

            for (String sqlCommand : (sqlCommands2)) {
                sqlCommand = sqlCommand.trim(); 
                if (!sqlCommand.isEmpty()) { 
                    statement.execute(sqlCommand);
                }
            }

        }
    }

    public static ResultSet Selecet(String Enitity) throws SQLException, IOException 
    {
        Connection connection = getConnection();

        String sqlQuery = "SELECT * FROM " + Enitity;
        
        PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
        
        ResultSet resultSet = preparedStatement.executeQuery();
        
        return resultSet;
    }


    public static void Update(JsonNode root, String Enitity, String ID) throws SQLException, IOException 
    {
        System.out.println("Upadate for "+ Enitity);
        try (Connection connection = getConnection()) {
        
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = '"+ Enitity +"';");
        ResultSet NameOfColumns = preparedStatement.executeQuery();

        StringBuilder sqlQuery = new StringBuilder("UPDATE " + Enitity + " SET ");

        while (NameOfColumns.next()) {
            String columnName = NameOfColumns.getString("COLUMN_NAME");
            String jsonValue = root.path(columnName).asText(); root.path(columnName);
            if (jsonValue.matches("true|false")) {
                sqlQuery.append(columnName).append("=").append(root.path(columnName).asBoolean()).append(",");
            } 
            else {
                if (!jsonValue.matches("-?\\d+(\\.\\d+)?"))  
                    sqlQuery.append(columnName).append("=").append("\"" + jsonValue + "\"").append(",");
                else
                    sqlQuery.append(columnName).append("=").append(jsonValue).append(",");
            }
        }
        sqlQuery.deleteCharAt(sqlQuery.length() - 1);
        sqlQuery.append(" WHERE "+ ID + " = ").append(root.path(ID).asText()).append(";");

        PreparedStatement query = connection.prepareStatement(sqlQuery.toString());
        query.executeUpdate();

        NameOfColumns.close();
        preparedStatement.close();
        connection.close();
        }
    }

    public static void Add(JsonNode root, String Enitity, String ID) throws SQLException, IOException 
    {
        System.out.println("Add for " + Enitity);
        try (Connection connection = getConnection()) {

            PreparedStatement sql_command2 = connection.prepareStatement("SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = '"+ Enitity +"';");
            ResultSet NameOfColumns = sql_command2.executeQuery();

            StringBuilder sql_command3 = new StringBuilder("INSERT INTO " + Enitity + " ( ");
            StringBuilder sql_command4 = new StringBuilder(" VALUES (");
            while (NameOfColumns.next()) 
            {
                String columnName = NameOfColumns.getString("COLUMN_NAME");
                String jsonValue = root.path(columnName).asText(); root.path(columnName);
    
                sql_command3.append(columnName).append(", ");
                if (!jsonValue.matches("-?\\d+(\\.\\d+)?")) 
                    sql_command4.append("\"" + jsonValue + "\"").append(", ");
                else
                    sql_command4.append(jsonValue).append(", ");
            }
            sql_command3.deleteCharAt(sql_command3.length() - 2);
            sql_command3.append(") ");

            sql_command4.deleteCharAt(sql_command4.length() - 2);
            sql_command4.append(");");
            sql_command3.append(sql_command4);

            PreparedStatement query = connection.prepareStatement(sql_command3.toString());
            query.executeUpdate();

            NameOfColumns.close();
            sql_command2.close();
            connection.close();
        }
    }

    public static void Delete(JsonNode root, String Enitity, String ID) throws SQLException, IOException 
    {
        System.out.println("Delete for "+ Enitity);
        try (Connection connection = getConnection()) {
        
            String ElementID = root.path(ID).asText();
            PreparedStatement sql_command2 = connection.prepareStatement("DELETE FROM "+ Enitity +" WHERE "+ ID +" = "+ ElementID +";");
            sql_command2.executeUpdate();

            connection.close();
        }
    }
}