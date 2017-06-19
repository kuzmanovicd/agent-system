package rest;

import java.util.ArrayList;
import java.util.HashMap;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import beans.AppManagerBean;
import beans.NodeManagerBean;
import models.AgentCenter;
import models.AgentType;
import proxy.NodeManagerProxy;
import utils.HTTP;

@Stateless
// @RequestScoped
@Path("/cluster")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class NodeManagerApi implements NodeManagerProxy {

	@EJB
	NodeManagerBean nodeManager;

	@EJB
	AppManagerBean appManager;

	// step 1 from slave
	@POST
	@Path("/node")
	public ArrayList<AgentCenter> nodeRegister(AgentCenter node) {
		//Log.out(this, "POST /node");
		return nodeManager.nodeRegister(node);
	}

	// step 2 from slave
	@POST
	@Path("/classes/{node}")
	public HashMap<String, ArrayList<AgentType>> newClassesFromNodeInHandshake(@PathParam("node") String node, ArrayList<AgentType> types) {
		//Log.out(this, "POST /agents/classes/{node} - " + node);
		return nodeManager.confirmHandshake(node, types);

	}

	@POST
	@Path("/node/update")
	public Response nodeUpdate(ArrayList<AgentCenter> nodes) {
		//Log.out(this, "POST /node");
		nodeManager.setAllCenters(nodes);
		return HTTP.OK_200(nodes);
	}

	@GET
	@Path("/node")
	public String status() {
		return "Node is online.";
	}

	@GET
	@Path("/nodes")
	public ArrayList<AgentCenter> allNodes() {
		// //Log.out(this, "status called");
		return nodeManager.getAllCenters();
	}

	@DELETE
	@Path("/node/{alias}")
	public Response deleteNode(@PathParam("alias") String alias) {
		nodeManager.nodeDelete(alias);
		return HTTP.OK_200("ok");
	}

}
