package beans;

import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
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
public class AgentManager implements AgentManagerLocal {

	// String is alias of the Center where agent type is available
	private ArrayList<AgentType> agentTypes;
	
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
    	agentTypes = new ArrayList<AgentType>();
    	myAgentTypes = new ArrayList<AgentType>();
    	runningAgents = new ArrayList<AID>();
    	myRunningAgents = new HashMap<String, BaseAgent>();
    }

	public ArrayList<AgentType> getAgentTypes() {
		return agentTypes;
	}

	@Override
	public boolean addAgentType(AgentType type) {
		if(!agentTypes.contains(type)) {
			agentTypes.add(type);
			return true;
		}
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

	public void setAgentTypes(ArrayList<AgentType> agentTypes) {
		this.agentTypes = agentTypes;
	}
    
	
    
    
    

}
