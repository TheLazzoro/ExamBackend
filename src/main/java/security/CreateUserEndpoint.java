package security;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import errorhandling.API_Exception;
import errorhandling.ExceptionDTO;
import errorhandling.UserAlreadyExistsException;
import facades.UserFacade;
import utils.EMF_Creator;

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/create-user")
public class CreateUserEndpoint {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    public static final UserFacade USER_FACADE = UserFacade.getUserFacade(EMF);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(String jsonString) throws API_Exception, UserAlreadyExistsException {
        String username;
        String password;
        try {
            JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
            username = json.get("username").getAsString();
            password = json.get("password").getAsString();
        } catch (Exception e) {
            throw new API_Exception("Malformed JSON Supplied", 400, e);
        }

        USER_FACADE.createUser(username, password);
        String msg = "{\"msg\":\"Account successfully created!\"}";

        return Response
                .ok()
                .entity(msg)
                .status(201)
                .build();
    }
}