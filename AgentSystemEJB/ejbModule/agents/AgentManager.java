package agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

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

import org.reflections.Reflections;

import beans.AppManagerBean;
import models.AID;
import models.AgentType;
import utils.Log;

/**
 * Session Bean implementation class AgentManager
 */
@Singleton
@Startup
@LocalBean
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@AccessTimeout(value = 5000)
@Lock(LockType.READ)
public class AgentManager implements AgentManagerLocal {

	// private HashMap<String, Class> agentClasses;

	@EJB
	AppManagerBean appManager;
	// String is alias of the Center where agent type is available
	// private HashMap<String, ArrayList<AgentType>> agentTypes;
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
	private void init() {
		Log.out(this, "@PostConstruct");
		agentTypes = new HashMap<>();
		myAgentTypes = new ArrayList<AgentType>();
		runningAgents = new ArrayList<AID>();
		myRunningAgents = new HashMap<String, BaseAgent>();

		generateAgentClasses();

		if (appManager.isMaster()) {
			agentTypes.put(appManager.getThisCenter().getAlias(), myAgentTypes);
		}
	}

	public void generateAgentClasses() {
		Log.out(this, "generateAgentClasses");

		try {
			Reflections reflections = new Reflections("agents");
			Set<Class<? extends BaseAgent>> classes = reflections.getSubTypesOf(BaseAgent.class);
			for (Class c : classes) {
				AgentType type = new AgentType(c.getName(), c.getName());
				Log.out(this, type.toString());
				myAgentTypes.add(type);
			}
		} catch (Exception e) {
			Log.out(this, "AJNE KLAJNE BELAJEN");
		}
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
		if (!myAgentTypes.contains(type)) {
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

	// getters and setters
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
		for (BaseAgent a : myRunningAgents.values()) {
			agents.add(a.getAID());
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
