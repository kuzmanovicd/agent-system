package beans;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

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
	@EJB NodeManagerBean nodeManager;
	private ResteasyClient restClient;
	
    public RestCommunicator() {
    	restClient = new ResteasyClientBuilder().connectionPoolSize(50).build();
    }
    
    @Override
    public void removeNode(AgentCenter node) {
    	//agentManager.stopAgentsForCenter(node);
    	agentManager.getAgentTypes().remove(node.getAlias());
    	
    	for(AgentCenter ac : nodeManager.getAllCenters()) {
    		if(ac.equals(node)) {
    			nodeManager.getAllCenters().remove(ac);
    			break;
    		}
    	}
    	
    	notifyNodes();
    }
    
    @Override
    public void notifyNodes() {
    	Log.out(this, "notifyNodes");
    	for(AgentCenter node : nodeManager.getAllCenters()) {
    		if(node.equals(appManager.getThisCenter())) {
    			continue;
    		}
    		
    		String url = HTTP.gen(node.getAddress(), AppConst.WAR_NAME, AppConst.REST_ROOT) + "/cluster/node/update";
    		HTTP.post(restClient, url, nodeManager.getAllCenters()).close();
    		
    		url = HTTP.gen(node.getAddress(), AppConst.WAR_NAME, AppConst.REST_ROOT) + "/agents/classes";
    		HTTP.post(restClient, url, agentManager.getAgentTypes()).close();
    	}
    }
    
    @Override
    public ArrayList<AgentType> retrieveAgentCenterClasses(AgentCenter node) {
    	Log.out(this, "REST retrieveAgentCenterClasses");
    	Log.out(this, node.toString());
    	
    	String url = HTTP.gen(node.getAddress(), AppConst.WAR_NAME, AppConst.REST_ROOT) + "agents/classes";
		Response response = HTTP.get(restClient, url);
		Log.out(this, "ISPISALO!!!");
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
    public void registerNode(AgentCenter thisCenter, AgentCenter masterCenter) {
    	try {
    		Log.out(this, "registerNode");
        	String uri = HTTP.gen(masterCenter.getAddress(), AppConst.WAR_NAME, AppConst.REST_ROOT) + "cluster/node";
        	Log.out(this, uri);
    		Response response = HTTP.post(restClient, uri, thisCenter);
    		Log.out(this, "after HTTP.post");
    		if(response.getStatus() >= 400) {
    			Log.out(this, "registerNode - unsuccessful");
    		}
    		response.close();
    	} catch (Exception e) {
    		Log.out(this, "registerNode EXCEPTION");
    	}
    	
    }
    
    public void unregisterNode() {
    	
    }

}
