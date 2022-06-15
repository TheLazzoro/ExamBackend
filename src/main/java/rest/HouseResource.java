package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtos.HouseDTO;
import dtos.HousesDTO;
import errorhandling.API_Exception;
import errorhandling.NotFoundException;
import facades.HouseFacade;
import utils.EMF_Creator;

import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/house")
public class HouseResource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();

    private static final HouseFacade FACADE =  HouseFacade.getHouseFacade(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Path("")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getHousesAll() {
        HousesDTO houses =  FACADE.getAll();
        return Response
                .ok()
                .entity(GSON.toJson(houses))
                .build();
    }

    @Path("{houseid}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getHouseById(@PathParam("houseid") long houseId) throws NotFoundException {
        return Response
                .ok()
                .entity(GSON.toJson(FACADE.getById(houseId)))
                .build();
    }

    @Path("create")
    @POST
    @RolesAllowed("admin")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createHouse(String content) throws API_Exception {
        HouseDTO toCreate = GSON.fromJson(content, HouseDTO.class);
        HouseDTO created = FACADE.create(toCreate);
        return Response
                .ok()
                .status(201)
                .entity(created)
                .build();
    }

}