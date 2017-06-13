package rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
import models.AgentCenter;
import models.AgentType;
import utils.HTTP;

@RequestScoped
@Path("/cluster")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class NodeManagerApi {
	
	@EJB NodeManagerBean nodeManager;
	@EJB AgentManager agentManager;
	@EJB AppManagerBean appManager;
	@EJB HTTP HTTP;
	
	@POST
	@Path("/node")
	public Response nodeRegister(AgentCenter node) {
		Log.out(this, "POST /node");
		
		ArrayList<AgentCenter> nodes = nodeManager.nodeRegister(node);
		if(nodes == null) {
			return HTTP.BAD_400("There was error in registering new node.");
		}
		
		return HTTP.OK_200(nodes);
	}
	
	@POST
	@Path("/node/update")
	public Response nodeUpdate(ArrayList<AgentCenter> nodes) {
		Log.out(this, "POST /node");
		appManager.setAllCenters(nodes);
		return HTTP.OK_200(nodes);
	}
	
	@GET
	@Path("/agents/classes")
	public ArrayList<AgentType> agentTypes() {
		Log.out(this, "GET /agents/classes");
		return agentManager.getAgentTypes();
	}
	
	@POST
	@Path("/agents/classes")
	public Response newAgentTypes(AgentType type) {
		Log.out(this, "POST /agents/classes");
		if(agentManager.addAgentType(type)) {
			return HTTP.CREATED_201(type);
		}
		
		return HTTP.BAD_400("Agent Type already exists");
	}
	
	@POST
	@Path("/agents/running")
	public ArrayList<AID> newRunningAgents(ArrayList<AID> agents) {
		Log.out(this, "POST /agents/running");
		return agentManager.getRunningAgents();
	}
	
	@GET
    @Path("/node")
    public Response status() {
    	//Log.out(this, "status called");
    	return HTTP.OK_200("online");
    }
	
	@DELETE
	@Path("/node/{alias}")
	public Response deleteNode(@PathParam("alias") String alias) {
		return null;
	}

}
