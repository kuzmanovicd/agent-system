package proxy;

import java.util.ArrayList;
import java.util.HashMap;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import models.AID;
import models.AgentType;
import utils.HTTP;
import utils.Log;

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
    
    @GET
    @Path("/running")
    public ArrayList<AID> getRunningAgents();
    
    @PUT
    @Path("/running/{type}/{name}")
    public Response startAgent(@PathParam("type") String type, @PathParam("name") String name);
    
    @DELETE
    @Path("/running/{aid}")
    public Response stopAgent(@PathParam("aid") String aid);
}
