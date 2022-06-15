package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtos.HouseDTO;
import dtos.HousesDTO;
import entities.House;
import entities.Role;
import entities.User;
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
import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
//Uncomment the line below, to temporarily disable this test
//@Disabled

public class HouseResourceTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";
    private static User admin;
    private static Role adminRole;
    private static House house1;

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

            house1 = new House("testaddress", "testcity", 2);

            em.getTransaction().begin();
            em.createNamedQuery("House.deleteAllRows").executeUpdate();
            em.createNamedQuery("User.deleteAllRows").executeUpdate();
            em.createNamedQuery("Role.deleteAllRows").executeUpdate();
            em.persist(house1);
            em.persist(adminRole);
            em.persist(admin);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    //This is how we hold on to the token after login, similar to that a client must store the token somewhere
    private static String securityToken;

    //Utility method to login and set the returned securityToken
    private static void login(String role, String password) {
        String json = String.format("{username: \"%s\", password: \"%s\"}", role, password);
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
    public void testCreateHouse() {
        login("admin", "secret");
        HouseDTO dto = new HouseDTO("testhouse2", "testcity2", 4);
        String house = GSON.toJson(dto);
        given()
                .contentType(MediaType.APPLICATION_JSON)
                .and()
                .body(house)
                .when()
                .header("x-access-token", securityToken)
                .post("house/create")
                .then()
                .statusCode(201);
    }

    @Test
    public void testGetAll() throws Exception {
        login("admin", "secret");
        HousesDTO recipesDTO = given()
                .contentType(MediaType.APPLICATION_JSON)
                .header("x-access-token", securityToken)
                .get("/house").then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .extract()
                .body()
                .jsonPath()
                .getObject("", HousesDTO.class);

        List<String> recipeNames = recipesDTO
                .getHouses()
                .stream()
                .map(HouseDTO::getAddress)
                .collect(Collectors.toList());

        assertThat(recipeNames, hasItems("testaddress"));
    }

    @Test
    public void testGetHouseById() {
        HouseDTO houseDTO = given()
                .contentType(MediaType.APPLICATION_JSON)
                .get("/house/{id}", house1.getId()).then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .extract()
                .body()
                .jsonPath()
                .getObject("", HouseDTO.class);

        assertEquals(house1.getAddress(), houseDTO.getAddress());
        assertEquals(house1.getCity(), houseDTO.getCity());
    }
}
