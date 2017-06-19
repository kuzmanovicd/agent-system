package beans;

import java.util.ArrayList;
import java.util.HashMap;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import agents.AgentManager;
import models.AID;
import models.AgentCenter;
import models.AgentType;
import proxy.AgentProxy;
import proxy.NodeManagerProxy;
import utils.AppConst;
import utils.HTTP;
import utils.Log;

/**
 * Session Bean implementation class RestCommunicator
 */
@Stateless(name = AppConst.COMMUNICATOR_NAME)
@LocalBean
public class RestCommunicator implements CommunicatorLocal {

	@EJB
	AgentManager agentManager;
	@EJB
	AppManagerBean appManager;
	@EJB
	NodeManagerBean nodeManager;
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
		for (AgentCenter node : nodeManager.getAllCenters()) {
			if (node.equals(appManager.getThisCenter())) {
				continue;
			}

			String url = HTTP.gen(node.getAddress(), AppConst.WAR_NAME, AppConst.REST_ROOT) + "/cluster/node/update";
			HTTP.post(restClient, url, nodeManager.getAllCenters()).close();

			url = HTTP.gen(node.getAddress(), AppConst.WAR_NAME, AppConst.REST_ROOT) + "/agents/classes";
			HTTP.post(restClient, url, agentManager.getAgentTypes()).close();

			url = HTTP.gen(node.getAddress(), AppConst.WAR_NAME, AppConst.REST_ROOT) + "/agents/running";
			HTTP.post(restClient, url, agentManager.getRunningAgents());

		}
		Log.out(this, "notifyNodes done");
	}

	@Override
	public void notifyAllNodesForAgents(HashMap<String, AID> allAgents) {
		Log.out(this, "notifyAllNodesForAgents");
		for (AgentCenter node : nodeManager.getAllCenters()) {
			if (node.equals(appManager.getThisCenter())) {
				continue;
			}

			String url = HTTP.gen(node.getAddress(), AppConst.WAR_NAME, AppConst.REST_ROOT) + "agents";
			ResteasyWebTarget rtarget = restClient.target(url);
			AgentProxy rest = (AgentProxy) rtarget.proxy(AgentProxy.class);
			rest.updateAllRunningAgents(allAgents);
		}
		Log.out(this, "notifyAllNodesForAgents done");
	}

	@Override
	public ArrayList<AgentCenter> nodeRegister() {
		String url = HTTP.gen(appManager.getMasterCenter().getAddress(), AppConst.WAR_NAME, AppConst.REST_ROOT) + "cluster";
		ResteasyWebTarget rtarget = restClient.target(url);
		NodeManagerProxy rest = (NodeManagerProxy) rtarget.proxy(NodeManagerProxy.class);

		return rest.nodeRegister(appManager.getThisCenter());
	}

	@Override
	public void nodeDelete() {
		String url = HTTP.gen(appManager.getMasterCenter().getAddress(), AppConst.WAR_NAME, AppConst.REST_ROOT) + "cluster";
		ResteasyWebTarget rtarget = restClient.target(url);
		NodeManagerProxy rest = (NodeManagerProxy) rtarget.proxy(NodeManagerProxy.class);
		rest.deleteNode(appManager.getThisCenter().getAlias());
	}

	@Override
	public HashMap<String, ArrayList<AgentType>> pushAndGetAgentTypes(ArrayList<AgentType> types) {
		String url = HTTP.gen(appManager.getMasterCenter().getAddress(), AppConst.WAR_NAME, AppConst.REST_ROOT) + "cluster";
		ResteasyWebTarget rtarget = restClient.target(url);
		NodeManagerProxy rest = (NodeManagerProxy) rtarget.proxy(NodeManagerProxy.class);
		return rest.newClassesFromNodeInHandshake(appManager.getThisCenter().getAlias(), types);
	}

	@Override
	public boolean heartbeat(AgentCenter node) {
		String url = HTTP.gen(node.getAddress(), AppConst.WAR_NAME, AppConst.REST_ROOT) + "cluster";
		ResteasyWebTarget rtarget = restClient.target(url);
		NodeManagerProxy rest = (NodeManagerProxy) rtarget.proxy(NodeManagerProxy.class);

		try {
			rest.status();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
