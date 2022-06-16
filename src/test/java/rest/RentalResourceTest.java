package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtos.HouseDTO;
import dtos.HousesDTO;
import dtos.RentalDTO;
import dtos.RentalsDTO;
import entities.*;
import errorhandling.API_Exception;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import security.errorhandling.AuthenticationException;
import utils.EMF_Creator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
//Uncomment the line below, to temporarily disable this test
//@Disabled

public class RentalResourceTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";
    private static User admin;
    private static Role adminRole, userRole;
    private static House house1;
    private static Rental rental1;
    private static User user1, user2;
    private static Tenant tenant1, tenant2;

    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;

    private static Gson GSON = new GsonBuilder().create();

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
        //System.in.read();

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
            admin = new User("admin", "secret");
            adminRole = new Role("admin");
            admin.addRole(adminRole);
            userRole = new Role("user");

            house1 = new House("testaddress", "testcity", 2);
            user1 = new User("randomUser1", "randomPass1");
            user1.addRole(userRole);
            user2 = new User("randomUser2", "randomPass2");
            user2.addRole(userRole);
            tenant1 = new Tenant("Lasse", "Programmer", "11223344");
            tenant2 = new Tenant("Sebastian", "Programmer", "44332211");
            tenant1.setUser(user1);
            tenant2.setUser(user2);
            HashSet<Tenant> tenants = new HashSet<>();
            tenants.add(tenant1);
            tenants.add(tenant2);
            rental1 = new Rental(house1, new Date(), new Date(), 100, 200, tenant1, tenants);

            em.getTransaction().begin();
            em.createNamedQuery("Rental.deleteAllRows").executeUpdate();
            em.createNamedQuery("Tenant.deleteAllRows").executeUpdate();
            em.createNamedQuery("House.deleteAllRows").executeUpdate();
            em.createNamedQuery("User.deleteAllRows").executeUpdate();
            em.createNamedQuery("Role.deleteAllRows").executeUpdate();
            em.persist(house1);
            em.persist(adminRole);
            em.persist(userRole);
            em.persist(admin);
            em.persist(tenant1);
            em.persist(tenant2);
            em.persist(rental1);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    //This is how we hold on to the token after login, similar to that a client must store the token somewhere
    private static String securityToken;

    //Utility method to login and set the returned securityToken
    private static void login(String username, String password) {
        String json = String.format("{username: \"%s\", password: \"%s\"}", username, password);
        securityToken = given()
                .contentType("application/json")
                .body(json)
                //.when().post("/api/login")
                .when().post("/login")
                .then()
                .extract().path("token");
        //System.out.println("TOKEN ---> " + securityToken);
    }

    private void logOut() {
        securityToken = null;
    }

    @Test
    public void testServerIsUp() throws AuthenticationException, API_Exception, IOException {
        login("admin", "secret");
        given().
                when()
                .header("x-access-token", securityToken)
                .get("/house")
                .then()
                .statusCode(200);
    }

    @Test
    public void testCreateRental() {
        login("admin", "secret");
        RentalDTO rentalDTO = new RentalDTO(rental1);
        String rental = GSON.toJson(rentalDTO);
        given()
                .contentType(MediaType.APPLICATION_JSON)
                .and()
                .body(rental)
                .when()
                .header("x-access-token", securityToken)
                .post("rental/create")
                .then()
                .statusCode(201);
    }

    @Test
    public void testGetRentalsByUsername() {
        login(tenant1.getUser().getUserName(), "randomPass1");
        String username = tenant1.getUser().getUserName();

        RentalsDTO rentalDTO = given()
                .contentType(MediaType.APPLICATION_JSON)
                .header("x-access-token", securityToken)
                .get("/rental/user/{username}", username).then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .extract()
                .body()
                .jsonPath()
                .getObject("", RentalsDTO.class);

        assertEquals(1, rentalDTO.getRentals().size());
    }

    @Test
    public void testDelete() {
        login("admin", "secret");
        long id = rental1.getId();
        given().
                when()
                .header("x-access-token", securityToken)
                .delete("/rental/delete/{id}", id)
                .then()
                .statusCode(200);
    }
}
