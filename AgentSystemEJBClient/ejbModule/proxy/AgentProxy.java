package proxy;

import java.util.ArrayList;
import java.util.HashMap;

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

import models.ACLMessage;
import models.AID;
import models.AgentType;
import models.Performative;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface AgentProxy {

	@GET
	@Path("/classes/my")
	public ArrayList<AgentType> getMyClasses();

	@GET
	@Path("/classes/all")
	public HashMap<String, ArrayList<AgentType>> getAllClasses();

	@POST
	@Path("/classes")
	public Response updateAllClasses(HashMap<String, ArrayList<AgentType>> types);

	@POST
	@Path("/running")
	public String updateAllRunningAgents(HashMap<String, AID> agents);

	@GET
	@Path("/running")
	public HashMap<String, AID> getAllRunningAgents();

	@PUT
	@Path("/running/{type}/{name}")
	public AID startAgent(@PathParam("type") String type, @PathParam("name") String name);

	@DELETE
	@Path("/running/{aid}")
	public AID stopAgent(@PathParam("aid") String aid);

	@GET
	@Path("/messages")
	Performative[] getPerformatives();

	@POST
	@Path("/message")
	public String sendMessage(ACLMessage message);

	@GET
	@Path("/messages/acl")
	ACLMessage getACL();

	@POST
	@Path("/broadcast")
	void broadcast(String msg);
}
