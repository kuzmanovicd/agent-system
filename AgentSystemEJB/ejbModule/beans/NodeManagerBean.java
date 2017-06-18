package beans;

import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.AccessTimeout;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import agents.AgentManager;
import models.AgentCenter;
import models.AgentType;
import proxy.NodeManagerProxy;
import utils.AppConst;
import utils.HTTP;
import utils.Log;

/**
 * Session Bean implementation class NodeManagerBean
 */
@Singleton
@Startup
@LocalBean
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@AccessTimeout(value = 5000)
@Lock(LockType.READ)
public class NodeManagerBean implements NodeManagerBeanLocal {

	@EJB
	AppManagerBean appManager;
	@EJB
	AgentManager agentManager;
	@EJB(beanName = AppConst.COMMUNICATOR_NAME)
	CommunicatorLocal communicator;

	private boolean isMaster = false;
	private AgentCenter thisCenter;
	private AgentCenter masterCenter;

	private HashMap<String, Integer> heartBeatCounter;

	private ResteasyClient client;
	private NodeManagerProxy masterRest;

	private ArrayList<AgentCenter> allCenters;

	private ArrayList<AgentType> newAgentTypes;
	private AgentCenter newNode;
	private boolean handshakeInProgress;

	public NodeManagerBean() {
		// TODO Auto-generated constructor stub
		allCenters = new ArrayList<AgentCenter>();
		client = new ResteasyClientBuilder().connectionPoolSize(10).build();
		heartBeatCounter = new HashMap<>();
	}

	@PostConstruct
	private void init() {
		thisCenter = appManager.getThisCenter();
		masterCenter = appManager.getMasterCenter();

		if (appManager.isMaster()) {
			Log.out(this, "@PostConstruct master");
			addCenter(appManager.getThisCenter());
			isMaster = true;
		} else {

			Log.out(this, "@PostConstruct slave");
			String url = HTTP.gen(appManager.getMasterCenter().getAddress(), AppConst.WAR_NAME, AppConst.REST_ROOT)
					+ "cluster";
			ResteasyWebTarget rtarget = client.target(url);
			masterRest = (NodeManagerProxy) rtarget.proxy(NodeManagerProxy.class);

			try {
				ArrayList<AgentCenter> nodes = masterRest.nodeRegister(appManager.getThisCenter());
				setAllCenters(nodes);
				Log.out(this, "Dobavio agent centre");

				HashMap<String, ArrayList<AgentType>> agents = masterRest.newClassesFromNodeInHandshake(
						appManager.getThisCenter().getAlias(), agentManager.getMyAgentTypes());
				if (agents != null) {
					agentManager.setAgentTypes(agents);
					Log.out(this, "Dobavio tipove agenata");
				} else {
					Log.out(this, "Neuspesno dobavio tipove agenata");
				}

			} catch (Exception e) {
				e.printStackTrace();
				Log.out(this, "Slave Exception");
			}

		}

	}

	@PreDestroy
	private void destroy() {
		Log.out(this, "@PreDestroy");
		if (!isMaster) {
			masterRest.deleteNode(thisCenter.getAlias());
		}
	}

	@Lock(LockType.WRITE)
	private ArrayList<AgentCenter> prepareHandshake(AgentCenter node) {
		Log.out(this, "prepareHandshake");
		this.newAgentTypes = null;
		this.newNode = node;
		this.handshakeInProgress = true;

		ArrayList<AgentCenter> newCenter = (ArrayList<AgentCenter>) getAllCenters().clone();
		newCenter.add(node);
		return newCenter;
	}

	// @Lock(LockType.WRITE)
	private void rollbackHandshake() {
		Log.out(this, "rollbackHandshake");
		communicator.removeNode(newNode);
		clearHandshake();
	}

	@Lock(LockType.WRITE)
	private void commitHandshake() {
		Log.out(this, "commitHandshake");
		addCenter(newNode);
		agentManager.addAgentsType(newNode.getAlias(), newAgentTypes);
		communicator.notifyNodes();
		clearHandshake();
		Log.out(this, "commitHandshake done");
	}

	@Lock(LockType.WRITE)
	private void clearHandshake() {
		Log.out(this, "clearHandshake");
		this.newAgentTypes = null;
		this.newNode = null;
		this.handshakeInProgress = false;
	}

	@Override
	@Lock(LockType.WRITE)
	public ArrayList<AgentCenter> nodeRegister(AgentCenter node) {
		Log.out(this, "nodeRegister");
		if (appManager.isMaster() && !nodeExists(node.getAlias())) {
			Log.out(this, "nodeRegister inside if");
			return prepareHandshake(node);
		}

		return null;
	}

	@Lock(LockType.WRITE)
	public HashMap<String, ArrayList<AgentType>> confirmHandshake(String node, ArrayList<AgentType> types) {

		if (handshakeInProgress) {
			Log.out(this, "confirmHandshake");
			newAgentTypes = types;
			commitHandshake();
			Log.out(this, "confirmHandshake done");
			return agentManager.getAgentTypes();
		}

		return null;
	}

	@Override
	public void updateHost(ArrayList<AgentCenter> nodes) {

	}

	@Override
	@Lock(LockType.WRITE)
	public boolean deleteNode(String alias) {
		Log.out(this, "DeleteNode");
		for (AgentCenter ac : allCenters) {
			if (ac.getAlias().equals(alias)) {
				allCenters.remove(ac);
				agentManager.getAgentTypes().remove(alias);
				communicator.notifyNodes();
				return true;
			}
		}
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
		for (AgentCenter h : allCenters) {
			if (h.getAlias().equals(alias)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public AgentCenter getAgentCenter(String alias) {
		for (AgentCenter ac : allCenters) {
			if (ac.getAlias().equals(alias)) {
				return ac;
			}
		}
		return null;
	}

	public void addCenter(AgentCenter node) {
		for (AgentCenter ac : getAllCenters()) {
			if (ac.equals(node)) {
				return;
			}
		}
		getAllCenters().add(node);
	}

	@Schedule(hour = "*", minute = "*", second = "*/5", info = "Check if slaves are online", persistent = false)
	public void heartBeat() {
		if (appManager.isMaster()) {
			for (AgentCenter node : getAllCenters()) {
				if (!node.equals(appManager.getMasterCenter())) {

					// rest
					String url = HTTP.gen(node.getAddress(), AppConst.WAR_NAME, AppConst.REST_ROOT) + "cluster";
					ResteasyWebTarget rtarget = client.target(url);
					NodeManagerProxy rest = (NodeManagerProxy) rtarget.proxy(NodeManagerProxy.class);

					try {
						rest.status();
						heartBeatCounter.put(node.getAlias(), 0);
					} catch (Exception e) {
						int counter = heartBeatCounter.get(node.getAlias());
						if (counter > 1) {
							deleteNode(node.getAlias());
						} else {
							counter++;
							heartBeatCounter.put(node.getAlias(), counter);
						}
						Log.out(this, "Node " + node.toString() + " is not responding... Trying again");
					}
				}

			}
		}
	}
}
