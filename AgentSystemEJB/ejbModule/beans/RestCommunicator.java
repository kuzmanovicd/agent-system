package beans;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import agents.AgentManager;
import models.AgentCenter;
import models.AgentType;
import utils.AppConst;
import utils.HTTP;
import utils.Log;

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
    		
    		//running etc....
    	}
    	Log.out(this, "notifyNodes done");
    }
    
    
    @Override
    public void registerNode(AgentCenter thisCenter, AgentCenter masterCenter) {
    	
    }
    
    public void unregisterNode() {
    	
    }

}
