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
	@EJB(beanName = AppConst.COMMUNICATOR_NAME)
	CommunicatorLocal communicator;
	
	private ArrayList<AgentType> newAgentTypes;
	private AgentCenter newNode;
	private boolean handshakeInProgress;
	
    public NodeManagerBean() {
        // TODO Auto-generated constructor stub
    }
    
    @Lock(LockType.WRITE)
    public void prepareHandshake(AgentCenter node) {
    	//this.newAgentTypes = new ArrayList<AgentType>();
    	this.newNode = node;
    	this.handshakeInProgress = true;
    }
    
    @Lock(LockType.WRITE)
    public void rollbackHandshake() {
    	communicator.removeNode(newNode);
    	clearHandshake();
    }
    
    @Lock(LockType.WRITE)
    public void commitHandshake() {
    	appManager.getAllCenters().add(newNode);
    	agentManager.addAgentsType(newNode.getAlias(), newAgentTypes);   	
    	communicator.notifyNodes();
    	clearHandshake();
    }
    
    @Lock(LockType.WRITE)
    public void clearHandshake() {
    	this.newAgentTypes.clear();
    	this.newNode = null;
    	this.handshakeInProgress = false;
    }
    
    @Override
    @Lock(LockType.WRITE)
    public boolean nodeRegister(AgentCenter node) {
    	if(appManager.aliasExists(node.getAlias())) {
			Log.out(this, "Alias exist:" + node.getAlias());
			return false;
		}
    	
    	if(appManager.isMaster()) {
    		prepareHandshake(node);
    		newAgentTypes = communicator.retrieveAgentCenterClasses(node);
    		
    		if(newAgentTypes != null) {
    			commitHandshake();
    			return true;
    		} else {
    			rollbackHandshake();
    			return false;
    		}   		
    	}   	
    	return false;	
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
