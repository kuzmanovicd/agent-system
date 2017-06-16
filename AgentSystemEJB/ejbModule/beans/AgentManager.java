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

import agents.BaseAgent;
import logger.Log;
import models.AID;
import models.Agent;
import models.AgentType;

/**
 * Session Bean implementation class AgentManager
 */
@Singleton
@Startup
@LocalBean
//@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
//@AccessTimeout(value = 5000)
//@Lock(LockType.READ)
public class AgentManager implements AgentManagerLocal {

	// String is alias of the Center where agent type is available
	//private HashMap<String, ArrayList<AgentType>> agentTypes;
	private HashMap<String, ArrayList<AgentType>> agentTypes;
	
	private ArrayList<AgentType> myAgentTypes;
	
	// String is alias of the Agent center where agent is running
	private ArrayList<AID> runningAgents;
	
	// String is AID of the Agent for easier retrival
	private HashMap<String, BaseAgent> myRunningAgents;
	
    public AgentManager() {
        // TODO Auto-generated constructor stub
    }
    
    @PostConstruct
    public void init() {
    	Log.out(this, "@PostConstruct");
    	agentTypes = new HashMap<>();
    	myAgentTypes = new ArrayList<AgentType>();
    	runningAgents = new ArrayList<AID>();
    	myRunningAgents = new HashMap<String, BaseAgent>();
    	
    	AgentType at1 = new AgentType("at1", "at1");
    	AgentType at2 = new AgentType("at2", "at2");
    	
    	myAgentTypes.add(at1);
    	myAgentTypes.add(at2);
    }

    @Override
	public HashMap<String, ArrayList<AgentType>> getAgentTypes() {
		return agentTypes;
	}

	@Override
	public boolean addAgentsType(String alias, ArrayList<AgentType> types) {
		agentTypes.put(alias, types);
		return false;
	}
	
	@Override
	public boolean addMyAgentType(AgentType type) {
		if(!myAgentTypes.contains(type)) {
			myAgentTypes.add(type);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean startAgent(AID id) {
		return true;
	}
	
	@Override
	public boolean stopAgent(AID id) {
		return true;
	}
	
	//getters and setters
	public ArrayList<AID> getRunningAgents() {
		return runningAgents;
	}

	public void setRunningAgents(ArrayList<AID> runningAgents) {
		this.runningAgents = runningAgents;
	}

	public HashMap<String, BaseAgent> getMyRunningAgents() {
		return myRunningAgents;
	}
	
	public ArrayList<AID> getMyRunningAgentsAID() {
		ArrayList<AID> agents = new ArrayList<AID>();
		for(BaseAgent a : myRunningAgents.values()) {
			agents.add(a.getMyAID());
		}
		return agents;
	}

	public void setMyRunningAgents(HashMap<String, BaseAgent> myRunningAgents) {
		this.myRunningAgents = myRunningAgents;
	}

	public ArrayList<AgentType> getMyAgentTypes() {
		return myAgentTypes;
	}

	public void setMyAgentTypes(ArrayList<AgentType> myAgentTypes) {
		this.myAgentTypes = myAgentTypes;
	}

	public void setAgentTypes(HashMap<String, ArrayList<AgentType>> agentTypes) {
		this.agentTypes = agentTypes;
	}

	
	
    
	
    
    
    

}
