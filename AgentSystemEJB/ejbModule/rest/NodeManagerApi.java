package rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
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

@Stateless
//@RequestScoped
@Path("/cluster")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class NodeManagerApi {
	
	@EJB NodeManagerBean nodeManager;
	@EJB AppManagerBean appManager;
	
	//step 1 from slave
	@POST
	@Path("/node")
	public Response nodeRegister(AgentCenter node) {
		Log.out(this, "POST /node");
		
		ArrayList<AgentCenter> nodes = nodeManager.nodeRegister(node);
		if(nodes == null) {
			return HTTP.BAD_400("There was error in registering new node.");
		}
		
		return HTTP.CREATED_201("ok");
	}
	
	//step 2 from slave
	@POST
    @Path("/classes/{node}")
    public Response newClassesFromNodeInHandshake(@PathParam("node") String node, ArrayList<AgentType> types) {
    	Log.out(this, "POST /agents/classes/{node} - " + node);
		if(nodeManager.confirmHandshake(node, types)) {
			return HTTP.OK_200("ok");
		}
		return HTTP.BAD_400("There was some error");
    	
    }
	
	@POST
	@Path("/node/update")
	public Response nodeUpdate(ArrayList<AgentCenter> nodes) {
		Log.out(this, "POST /node");
		nodeManager.setAllCenters(nodes);
		return HTTP.OK_200(nodes);
	}
	
	@GET
    @Path("/node")
    public Response status() {
    	//Log.out(this, "status called");
    	return HTTP.OK_200("online");
    }
	
	@GET
    @Path("/nodes")
    public ArrayList<AgentCenter> allNodes() {
    	//Log.out(this, "status called");
    	return nodeManager.getAllCenters();
    }
	
	@DELETE
	@Path("/node/{alias}")
	public Response deleteNode(@PathParam("alias") String alias) {
		return null;
	}

}
