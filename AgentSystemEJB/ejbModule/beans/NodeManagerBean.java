package beans;

import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.ejb.AccessTimeout;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Stateless;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import logger.Log;
import models.AgentCenter;
import models.AgentType;
import sun.management.resources.agent;
import utils.AppConst;
import utils.HTTP;

/**
 * Session Bean implementation class NodeManagerBean
 */
@Singleton
@Startup
@LocalBean
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@AccessTimeout(value = 5000)
public class NodeManagerBean implements NodeManagerBeanLocal {

	@EJB AppManagerBean appManager;
	@EJB AgentManager agentManager;
	@EJB HTTP HTTP;
	
	private ArrayList<AgentType> newAgentTypes;
	private AgentCenter newNode;
	private boolean handshakeInProgress;
	
    public NodeManagerBean() {
        // TODO Auto-generated constructor stub
    }
    
    @Lock(LockType.WRITE)
    public void prepareHandshake(AgentCenter node) {
    	this.newAgentTypes = new ArrayList<AgentType>();
    	this.newNode = node;
    	this.handshakeInProgress = true;
    }
    
    @Lock(LockType.WRITE)
    public void rollbackHandshake() {
    	//TODO
    	
    	clearHandshake();
    }
    
    @Lock(LockType.WRITE)
    public void commitHandshake() {
    	appManager.getAllCenters().add(newNode);
    	
    	for(AgentType type : newAgentTypes) {
    		agentManager.addAgentType(type);
    	}
    	
    	notifyNodes();
    	clearHandshake();
    }
    
    public void removeNode(AgentCenter node) {
    	//agentManager.
    }
    
    public void notifyNodes() {
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
    
    @Lock(LockType.WRITE)
    public void clearHandshake() {
    	this.newAgentTypes.clear();
    	this.newNode = null;
    	this.handshakeInProgress = false;
    }
    
    @Override
    @Lock(LockType.WRITE)
    public ArrayList<AgentCenter> nodeRegister(AgentCenter node) {
    	if(appManager.aliasExists(node.getAlias())) {
			Log.out(this, "Alias exist:" + node.getAlias());
			return null;
		}
    	
    	if(appManager.isMaster()) {
    		prepareHandshake(node);
    		String url = HTTP.gen(node.getAddress(), AppConst.WAR_NAME, AppConst.REST_ROOT) + "agents/classes";
    		Response response = HTTP.get(appManager.getClient(), url);
    		
    		// Step 2 - Handshake - retrieve the list of agent types from the new node
    		if(response.getStatus() >= 400) {
    			newAgentTypes = response.readEntity(new GenericType<ArrayList<AgentType>>() {});
        		response.close();
        		// Finalize step
        		commitHandshake();
    		} else {
    			// Rollback
    			rollbackHandshake();
    		}
    		
    	} else {
    		appManager.getAllCenters().add(node);
    	}
		
    	/*
		appManager.getAllCenters().add(node);
	
		for(AgentCenter n : appManager.getAllCenters()) {
			if(n.equals(appManager.getMasterCenter()) || n.equals(node)) {
				continue;
			}
			String uri = HTTP.gen(n.getAddress(), AppConst.WAR_NAME, AppConst.REST_ROOT) + "cluster/host-update";

			Response response = HTTP.post(appManager.getClient(), uri, appManager.getAllCenters());
			response.close();
		}
		*/
		return appManager.getAllCenters();
    }
    
    @Override
    public void updateHost(ArrayList<AgentCenter> nodes) {
    	appManager.getAllCenters().clear();
    	appManager.getAllCenters().addAll(nodes);
    }
    
    @Override
    public void unregister(AgentCenter node) {
    	for(AgentCenter n : appManager.getAllCenters()) {
    		if(n.equals(node)) {
    			appManager.getAllCenters().remove(n);
    			break;
    		}
    	}
    }
    
    @Override
    public boolean deleteNode(String alias) {
    	return false;
    }

}
