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
import javax.ejb.Stateful;
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
//@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
//@AccessTimeout(value = 5000)
public class NodeManagerBean implements NodeManagerBeanLocal {

	@EJB AppManagerBean appManager;
	@EJB NodeManagerBean nodeManager;
	@EJB AgentManager agentManager;
	@EJB(beanName = AppConst.COMMUNICATOR_NAME)
	CommunicatorLocal communicator;
	
	private ArrayList<AgentCenter> allCenters;
	
	private ArrayList<AgentType> newAgentTypes;
	private AgentCenter newNode;
	private boolean handshakeInProgress;
	
    public NodeManagerBean() {
        // TODO Auto-generated constructor stub
    	allCenters = new ArrayList<AgentCenter>();
    }
    
    @PostConstruct
    public void init() {
    	//allCenters
    	if(appManager.isMaster()) {
    		Log.out(this, "@PostConstruct master");
    		allCenters.add(appManager.getThisCenter());
    	} else {
    		Log.out(this, "@PostConstruct slave");
    	}
    	
    }
    
    //@Lock(LockType.WRITE)
    private void prepareHandshake(AgentCenter node) {
    	Log.out(this, "prepareHandshake");
    	this.newAgentTypes = null;
    	this.newNode = node;
    	this.handshakeInProgress = true;
    }
    
    //@Lock(LockType.WRITE)
    private void rollbackHandshake() {
    	Log.out(this, "rollbackHandshake");
    	communicator.removeNode(newNode);
    	clearHandshake();
    }
    
    //@Lock(LockType.WRITE)
    private void commitHandshake() {
    	Log.out(this, "commitHandshake");
    	getAllCenters().add(newNode);
    	agentManager.addAgentsType(newNode.getAlias(), newAgentTypes);   	
    	communicator.notifyNodes();
    	clearHandshake();
    }
    
    @Lock(LockType.WRITE)
    private void clearHandshake() {
    	Log.out(this, "clearHandshake");
    	this.newAgentTypes = null;
    	this.newNode = null;
    	this.handshakeInProgress = false;
    }
    
    @Override
    //@Lock(LockType.WRITE)
    public ArrayList<AgentCenter> nodeRegister(AgentCenter node) {
    	Log.out(this, "nodeRegister");
    	if(nodeManager.nodeExists(node.getAlias())) {
			Log.out(this, "Alias exist:" + node.getAlias());
			return null;
		}
    	
    	Log.out(this, node.toString());
    	
    	if(appManager.isMaster()) {
    		prepareHandshake(node);
    		
    		/* delete retrieveAgentCenterClasses
    		newAgentTypes = communicator.retrieveAgentCenterClasses(node);
    		if(newAgentTypes != null) {
    			commitHandshake();
    			return true;
    		} else {
    			rollbackHandshake();
    			return false;
    		}
    		*/
    	}
    	return nodeManager.getAllCenters();
    }
    
    public boolean confirmHandshake(String node, ArrayList<AgentType> types) {
    	if(appManager.isMaster() && newNode.getAlias().equals(node)) {
    		newAgentTypes = types;
    		commitHandshake();
    		return true;
    	}
    	
    	return false;
    }
    
    @Override
    public void updateHost(ArrayList<AgentCenter> nodes) {
    	getAllCenters().clear();
    	getAllCenters().addAll(nodes);
    }
    
    @Override
    public void unregister(AgentCenter node) {
    	for(AgentCenter n : getAllCenters()) {
    		if(n.equals(node)) {
    			getAllCenters().remove(n);
    			break;
    		}
    	}
    }
    
    @Override
    public boolean deleteNode(String alias) {
    	return false;
    }

	public ArrayList<AgentCenter> getAllCenters() {
		return allCenters;
	}

	public void setAllCenters(ArrayList<AgentCenter> allCenters) {
		this.allCenters = allCenters;
	}
	
	@Override
	public Boolean nodeExists(String alias) {
		for(AgentCenter h : allCenters) {
			if(h.getAlias().equals(alias)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public AgentCenter getAgentCenter(String alias) {
		for(AgentCenter ac : allCenters) {
			if(ac.getAlias().equals(alias)) {
				return ac;
			}
		}
		return null;
	}
}
