package rest;

import java.util.ArrayList;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import models.Agent;
import models.AgentType;

@RequestScoped
@Path("/agents")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AgentApi {

    @GET
    @Path("/classes")
    public ArrayList<AgentType> getClasses() {
		return null;
    }
    
    @GET
    @Path("/running")
    public ArrayList<Agent> getRunningAgents() {
    	return null;
    }
    
    @PUT
    @Path("/running/{type}/{name}")
    public Response startAgent(@PathParam("type") String type, @PathParam("name") String name) {
		return null;
    	
    }
    
    @DELETE
    @Path("/running/{aid}")
    public Response stopAgent(@PathParam("aid") String aid) {
		return null;
    	
    }
    
    
}
