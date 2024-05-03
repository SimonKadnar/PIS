package io.openliberty.guides.hello;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;


@WebServlet("/UserInformation")
public class UserInformationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { 
                

        String token = request.getHeader("Authorization");
        if (token.equals("null")) {
            System.out.println("\nNo JWT token found provided\n");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT token not found");
        } 
        
        DecodedJWT jwt = JWT.decode(token);

        Date expiresAt = jwt.getExpiresAt();
        if (expiresAt.before(new Date())) {
            System.out.println("\nJWT token has expired\n");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT token has expired");
        }

        String username = jwt.getClaim("username").asString();
        String role = jwt.getClaim("role").asString();

        Map<String, String> map = new HashMap<>();
        map.put("username", username);
        map.put("role", role);

        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(map);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonString);
        response.setStatus(HttpServletResponse.SC_OK);

        System.out.println(jsonString);
    

    }
}
