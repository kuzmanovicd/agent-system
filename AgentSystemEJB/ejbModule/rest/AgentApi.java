package rest;

import java.util.ArrayList;
import java.util.HashMap;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import beans.AgentManager;
import beans.AppManagerBean;
import beans.NodeManagerBean;
import logger.Log;
import models.AID;
import models.AgentType;
import utils.HTTP;

@RequestScoped
@Path("/agents")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AgentApi {

	@EJB NodeManagerBean nodeManager;
	@EJB AgentManager agentManager;
	@EJB AppManagerBean appManager;
	@EJB HTTP HTTP;
	
    @GET
    @Path("/classes")
    public ArrayList<AgentType> getMyClasses() {
    	Log.out(this, "GET /agents/classes");
		return agentManager.getMyAgentTypes();
    }
    
    @POST
    @Path("/classes")
    public Response updateAllClasses(HashMap<String, ArrayList<AgentType>> types) {
    	Log.out(this, "POST /agents/classes");
		agentManager.setAgentTypes(types);
    	return HTTP.OK_200("ok");
    }
    
    @GET
    @Path("/running")
    public ArrayList<AID> getRunningAgents() {
    	Log.out(this, "GET /agents/running");
    	return agentManager.getMyRunningAgentsAID();
    }
    
    @PUT
    @Path("/running/{type}/{name}")
    public Response startAgent(@PathParam("type") String type, @PathParam("name") String name) {
    	//Agent starter
		return null;
    	
    }
    
    @DELETE
    @Path("/running/{aid}")
    public Response stopAgent(@PathParam("aid") String aid) {
    	//Agent stopper
		return null;
    }
    
    
}
