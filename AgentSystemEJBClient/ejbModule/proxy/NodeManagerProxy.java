package proxy;

import java.util.ArrayList;
import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import models.AgentCenter;
import models.AgentType;
import utils.HTTP;
import utils.Log;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface NodeManagerProxy {

	@POST
	@Path("/node")
	public ArrayList<AgentCenter> nodeRegister(AgentCenter node);
	
	@POST
    @Path("/classes/{node}")
    public HashMap<String, ArrayList<AgentType>> newClassesFromNodeInHandshake(@PathParam("node") String node, ArrayList<AgentType> types);
	
	@POST
	@Path("/node/update")
	public Response nodeUpdate(ArrayList<AgentCenter> nodes);
	
	@GET
    @Path("/node")
    public String status();
	
	@GET
    @Path("/nodes")
    public ArrayList<AgentCenter> allNodes();
	
	@DELETE
	@Path("/node/{alias}")
	public Response deleteNode(@PathParam("alias") String alias);
}
