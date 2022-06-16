package security;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dtos.UserDTO;
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
    private static Gson GSON = new GsonBuilder().create();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(String jsonString) throws API_Exception, UserAlreadyExistsException {
        UserDTO userDTO = GSON.fromJson(jsonString, UserDTO.class);
        if(userDTO.getUsername() == null || userDTO.getPassword() == null)
            throw new API_Exception("Malformed JSON Supplied", 400);

        USER_FACADE.createUser(userDTO);
        String msg = "{\"msg\":\"Account successfully created!\"}";

        return Response
                .ok()
                .entity(msg)
                .status(201)
                .build();
    }
}