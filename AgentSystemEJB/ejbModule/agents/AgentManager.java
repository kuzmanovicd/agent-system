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
import beans.CommunicatorLocal;
import models.AID;
import models.AgentCenter;
import models.AgentType;
import utils.AppConst;
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

	@EJB(beanName = AppConst.COMMUNICATOR_NAME)
	CommunicatorLocal communicator;

	@EJB
	AppManagerBean appManager;
	// String is alias of the Center where agent type is available
	// private HashMap<String, ArrayList<AgentType>> agentTypes;
	private HashMap<String, ArrayList<AgentType>> agentTypes;

	private ArrayList<AgentType> myAgentTypes;

	private HashMap<String, AID> runningAgents;

	private HashMap<String, BaseAgent> myRunningAgents;

	public AgentManager() {
		// TODO Auto-generated constructor stub
	}

	@PostConstruct
	private void init() {
		Log.out(this, "@PostConstruct");
		agentTypes = new HashMap<>();
		myAgentTypes = new ArrayList<AgentType>();
		runningAgents = new HashMap<>();
		myRunningAgents = new HashMap<String, BaseAgent>();

		generateAgentClasses();

		if (appManager.isMaster()) {
			agentTypes.put(appManager.getThisCenter().getAlias(), myAgentTypes);
		}
	}

	@Lock(LockType.WRITE)
	public void generateAgentClasses() {
		Log.out(this, "generateAgentClasses");

		try {
			Reflections reflections = new Reflections("agents");
			Set<Class<? extends BaseAgent>> classes = reflections.getSubTypesOf(BaseAgent.class);
			for (Class c : classes) {
				AgentType type = new AgentType(c.getName(), c.getName(), c);
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
	@Lock(LockType.WRITE)
	public boolean addAgentsType(String alias, ArrayList<AgentType> types) {
		agentTypes.put(alias, types);
		return false;
	}

	@Override
	@Lock(LockType.WRITE)
	public boolean addMyAgentType(AgentType type) {
		if (!myAgentTypes.contains(type)) {
			myAgentTypes.add(type);
			return true;
		}
		return false;
	}

	@Override
	@Lock(LockType.WRITE)
	public AID startAgent(String agentType, String agentName) {

		if (runningAgents.containsKey(agentName)) {
			return null;
		}

		AgentType atype = getMyAgent(agentType);
		if (atype != null) {
			try {
				BaseAgent a = (BaseAgent) atype.getKlass().newInstance();
				AID aid = new AID(appManager.getThisCenter(), agentName, atype);
				a.setAID(aid);
				myRunningAgents.put(aid.getName(), a);
				runningAgents.put(aid.getName(), aid);
				communicator.notifyAllNodesForAgents(runningAgents);
				return aid;

			} catch (InstantiationException | IllegalAccessException e) {
				Log.out(this, "startAgent Exception");
			}
		}
		return null;
	}

	public AgentType getMyAgent(String agentName) {
		for (AgentType at : myAgentTypes) {
			if (at.getName().equals(agentName)) {
				return at;
			}
		}
		return null;
	}

	@Override
	public AID stopAgent(String agentName) {
		AID ret = runningAgents.remove(agentName);
		myRunningAgents.remove(agentName);
		if (ret != null) {
			communicator.notifyAllNodesForAgents(runningAgents);
		}
		return ret;
	}

	// getters and setters
	public HashMap<String, AID> getRunningAgents() {
		return runningAgents;
	}

	public void setRunningAgents(HashMap<String, AID> runningAgents) {
		this.runningAgents = runningAgents;
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

	public HashMap<String, BaseAgent> getMyRunningAgents() {
		return myRunningAgents;
	}

	public void setMyRunningAgents(HashMap<String, BaseAgent> myRunningAgents) {
		this.myRunningAgents = myRunningAgents;
	}

	private void smallTest() {
		AgentType at1 = new AgentType("test", "test", AgentType.class);
		AgentType at2 = new AgentType("test", "test", AgentType.class);

		if (!at1.equals(at2)) {
			Log.out(this, "SHIT SHIT SHIT");
		}

		AgentCenter ac1 = new AgentCenter("127.0.0.1:8426", "alias");
		AgentCenter ac2 = new AgentCenter("127.0.0.1:8426", "alias");

		if (!ac1.equals(ac2)) {
			Log.out(this, "2 SHIT SHIT SHIT");
		}

		AID id1 = new AID(ac1, "name", at1);
		AID id2 = new AID(ac2, "name", at2);
		AID id3 = new AID(ac2, "name2", at2);
		AID id4 = new AID(ac2, "name", at2);

		if (!id1.equals(id2)) {
			Log.out(this, "3 SHIT SHIT SHIT");
		}

		ArrayList<AID> list1 = new ArrayList<AID>();
		list1.add(id1);
		if (!list1.contains(id3))
			list1.add(id3);

		Log.out(this, "list1.size() " + list1.size());

		ArrayList<AID> list2 = new ArrayList<AID>();
		list2.add(id4);
		list2.add(id3);

		Log.out(this, "list2.size() " + list2.size());

		list1.addAll(list2);

		Log.out(this, "list1.size() " + list1.size());
	}
}
