package beans;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;

import logger.Log;
import models.AgentCenter;
import models.AgentType;
import utils.AppConst;
import utils.HTTP;

/**
 * Session Bean implementation class RestCommunicator
 */
@Stateless(name = AppConst.COMMUNICATOR_NAME)
@LocalBean
public class RestCommunicator implements CommunicatorLocal {

	@EJB AgentManager agentManager;
	@EJB AppManagerBean appManager;
	@EJB HTTP HTTP;
	
    public RestCommunicator() {
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public void removeNode(AgentCenter node) {
    	//agentManager.stopAgentsForCenter(node);
    	agentManager.getAgentTypes().remove(node.getAlias());
    	
    	for(AgentCenter ac : appManager.getAllCenters()) {
    		if(ac.equals(node)) {
    			appManager.getAllCenters().remove(ac);
    			break;
    		}
    	}
    	
    	notifyNodes();
    }
    
    @Override
    public void notifyNodes() {
    	Log.out(this, "notifyNodes");
    	for(AgentCenter node : appManager.getAllCenters()) {
    		if(node.equals(appManager.getThisCenter())) {
    			continue;
    		}
    		
    		String url = HTTP.gen(node.getAddress(), AppConst.WAR_NAME, AppConst.REST_ROOT) + "/cluster/node/update";
    		HTTP.post(appManager.getClient(), url, appManager.getAllCenters()).close();
    		
    		url = HTTP.gen(node.getAddress(), AppConst.WAR_NAME, AppConst.REST_ROOT) + "/agents/classes";
    		HTTP.post(appManager.getClient(), url, agentManager.getAgentTypes()).close();
    	}
    }
    
    @Override
    public ArrayList<AgentType> retrieveAgentCenterClasses(AgentCenter node) {
    	Log.out(this, "REST retrieveAgentCenterClasses");
    	String url = HTTP.gen(node.getAddress(), AppConst.WAR_NAME, AppConst.REST_ROOT) + "agents/classes";
		Response response = HTTP.get(appManager.getClient(), url);
		
		// Step 2 - Handshake - retrieve the list of agent types from the new node
		if(response.getStatus() < 400) {
			ArrayList<AgentType> types = response.readEntity(new GenericType<ArrayList<AgentType>>() {});
    		response.close();
    		return types;
		} else {
			return null;
		}
    }
    
    @Override
    public void registerNode(AgentCenter thisCenter, AgentCenter masterCenter, ResteasyClient client) {
    	Log.out(this, "registerNode");
    	String uri = HTTP.gen(masterCenter.getAddress(), AppConst.WAR_NAME, AppConst.REST_ROOT) + "cluster/node";
		Response response = HTTP.post(client, uri, thisCenter);
		if(response.getStatus() >= 400) {
			Log.out(this, "registerNode - unsuccessful");
		}
		response.close();
    }
    
    public void unregisterNode() {
    	
    }

}
