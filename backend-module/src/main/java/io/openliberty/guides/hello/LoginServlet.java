package io.openliberty.guides.hello;

import java.io.IOException;
import java.util.Date;

import javax.json.Json;
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

import com.fasterxml.jackson.databind.ObjectMapper;

import io.openliberty.guides.hello.model.Employee;
import io.openliberty.guides.hello.model.LoginRequest;

// Hash
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.math.BigInteger;

// JWT
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;


@WebServlet("/loginVerify")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { 
           
        ObjectMapper mapper = new ObjectMapper();
        LoginRequest loginRequest = mapper.readValue(request.getInputStream(), LoginRequest.class);
        String username = loginRequest.getUsername();
        String password = hashPassword(loginRequest.getPassword());

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa-hibernate-mysql");
        EntityManager em = emf.createEntityManager();
        try {

            TypedQuery<Employee> query = em.createNamedQuery("Employee.findByEmail", Employee.class);
            query.setParameter("email", username);

            Employee employee = query.getSingleResult();
            String dbRole = employee.getAssignment();
            String dbPassword = employee.getPassword();

            if (password.equals(dbPassword)){

                Algorithm algorithm = Algorithm.HMAC256("abcdefg");
                String token = JWT.create()
                .withIssuer("PIS")
                .withClaim("username", username)
                .withClaim("role", dbRole)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 8 * 60 * 60 * 1000)) // expire in 8 hours
                .sign(algorithm);

                String jsonString = Json.createObjectBuilder()
                    .add("token", token)
                    .build()
                    .toString();

                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(jsonString);

            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Incorrect password");
            }
            
        } catch (NoResultException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("Incorrect username");
        } finally {
            em.close();
            emf.close();
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            BigInteger number = new BigInteger(1, hash);
            StringBuilder hexString = new StringBuilder(number.toString(16));
            while (hexString.length() < 32) {
                hexString.insert(0, '0');
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}