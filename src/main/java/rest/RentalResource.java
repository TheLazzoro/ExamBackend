package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtos.HouseDTO;
import dtos.HousesDTO;
import dtos.RentalDTO;
import errorhandling.API_Exception;
import errorhandling.NotFoundException;
import facades.RentalFacade;
import utils.EMF_Creator;

import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/rental")
public class RentalResource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();

    private static final RentalFacade FACADE =  RentalFacade.getRentalFacade(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Path("create")
    @POST
    @RolesAllowed("admin")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createRental(String content) throws API_Exception {
        RentalDTO toCreate = GSON.fromJson(content, RentalDTO.class);
        RentalDTO created = FACADE.create(toCreate);
        return Response
                .ok()
                .status(201)
                .entity(created)
                .build();
    }

    @Path("/user/{username}")
    @GET
    @RolesAllowed("user")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getRentalsByUsername(@PathParam("username") String username) throws NotFoundException {
        return Response
                .ok()
                .entity(GSON.toJson(FACADE.getRentalsByUsername(username)))
                .build();
    }

}