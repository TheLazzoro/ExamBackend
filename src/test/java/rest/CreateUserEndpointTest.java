package rest;

import entities.Role;
import entities.User;
import errorhandling.UserAlreadyExistsException;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;

import static io.restassured.RestAssured.given;

public class CreateUserEndpointTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";

    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;

    private static User user, admin, both;
    
    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    @BeforeAll
    public static void setUpClass() {
        //This method must be called before you request the EntityManagerFactory
        EMF_Creator.startREST_TestWithDB();
        emf = EMF_Creator.createEntityManagerFactoryForTest();

        httpServer = startServer();
        //Setup RestAssured
        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;
        RestAssured.defaultParser = Parser.JSON;
    }

    @AfterAll
    public static void closeTestServer() {
        //Don't forget this, if you called its counterpart in @BeforeAll
        EMF_Creator.endREST_TestWithDB();
        
        httpServer.shutdownNow();
    }

    // Setup the DataBase (used by the test-server and this test) in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the EntityClass used below to use YOUR OWN (renamed) Entity class
    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            //Delete existing users and roles to get a "fresh" database
            em.createQuery("delete from User").executeUpdate();
            em.createQuery("delete from Role").executeUpdate();

            Role userRole = new Role("user");
            Role adminRole = new Role("admin");
            user = new User("user", "pass1234");
            user.addRole(userRole);
            admin = new User("admin", "test");
            admin.addRole(adminRole);
            both = new User("user_admin", "test");
            both.addRole(userRole);
            both.addRole(adminRole);
            em.persist(userRole);
            em.persist(adminRole);
            em.persist(user);
            em.persist(admin);
            em.persist(both);
            //System.out.println("Saved test data to database");
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Test
    public void testCreateUser() {
        String json = String.format("{username: \"%s\", password: \"%s\"}", "user2", "pass1234");
        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .when()
                .post("/create-user").then()
                .statusCode(201);
    }

    /*
    @Test
    public void testCreateUserAlreadyExists() throws UserAlreadyExistsException {
        String json = String.format("{username: \"%s\", password: \"%s\"}", "user", "pass1234");
        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .when()
                .post("/create-user").then()
                .statusCode(409);
    }
     */

    @Test
    public void serverIsRunning() {
        given().when().get("/info").then().statusCode(200);
    }

}
