package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtos.TenantsDTO;
import errorhandling.NotFoundException;
import facades.HouseFacade;
import facades.TenantFacade;
import utils.EMF_Creator;

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/tenant")
public class TenantResource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();

    private static final TenantFacade FACADE =  TenantFacade.getTenantFacade(EMF);
    private static final HouseFacade FACADE_HOUSE =  HouseFacade.getHouseFacade(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Path("")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getTenantsAll() {
        TenantsDTO houses =  FACADE.getAll();
        return Response
                .ok()
                .entity(GSON.toJson(houses))
                .build();
    }

    @Path("{username}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getTenantByUsername(@PathParam("username") String username) throws NotFoundException {
        return Response
                .ok()
                .entity(GSON.toJson(FACADE.getByUsername(username)))
                .build();
    }

    @Path("/house/{houseId}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getTenantsByHouseId(@PathParam("houseId") long id) throws NotFoundException {
        return Response
                .ok()
                .entity(GSON.toJson(FACADE.getAllByHouse(FACADE_HOUSE.getById(id))))
                .build();
    }
}