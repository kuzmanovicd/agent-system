package agents;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import beans.NodeManagerBeanLocal;
import beans.SessionsManagerBeanLocal;
import mdb.AgentServicesBeanLocal;
import utils.AppConst;
import utils.Log;
import websocket.WSManagerLocal;

public class AgentHelper {

	public static AgentServicesBeanLocal getAgentServices() {
		try {
			Context cntx = new InitialContext();
			String name = "java:global/" + AppConst.APP_NAME + "/" + AppConst.EJB_NAME + "/AgentServicesBean!" + AgentServicesBeanLocal.class.getName();
			AgentServicesBeanLocal a = (AgentServicesBeanLocal) cntx.lookup(name);
			return a;
		} catch (NamingException e) {
			Log.out("AgentHelper - catchhh " + e.getMessage());
			return null;
		}
	}

	public static AgentManagerLocal getAgentManager() {
		try {
			Context cntx = new InitialContext();
			String name = "java:global/" + AppConst.APP_NAME + "/" + AppConst.EJB_NAME + "/AgentManager!" + AgentManagerLocal.class.getName();
			AgentManagerLocal a = (AgentManagerLocal) cntx.lookup(name);
			return a;
		} catch (NamingException e) {
			Log.out("AgentHelper - catchhh " + e.getMessage());
			return null;
		}
	}

	public static NodeManagerBeanLocal getNodeManager() {
		try {
			Context cntx = new InitialContext();
			String name = "java:global/" + AppConst.APP_NAME + "/" + AppConst.EJB_NAME + "/NodeManagerBean!" + NodeManagerBeanLocal.class.getName();
			NodeManagerBeanLocal a = (NodeManagerBeanLocal) cntx.lookup(name);
			return a;
		} catch (NamingException e) {
			Log.out("AgentHelper - catchhh " + e.getMessage());
			return null;
		}
	}

	public static SessionsManagerBeanLocal getSessionManager() {
		try {
			Context cntx = new InitialContext();
			String name = "java:global/" + AppConst.APP_NAME + "/" + AppConst.EJB_NAME + "/SessionsManagerBean!" + SessionsManagerBeanLocal.class.getName();
			SessionsManagerBeanLocal a = (SessionsManagerBeanLocal) cntx.lookup(name);
			return a;
		} catch (Exception e) {
			Log.out("AgentHelper - catchhh " + e.getMessage());
			return null;
		}
	}

	public static WSManagerLocal getWSManager() {
		try {
			Context cntx = new InitialContext();
			String name = "java:global/" + AppConst.APP_NAME + "/" + AppConst.WAR_NAME + "/WSManager!" + WSManagerLocal.class.getName();
			WSManagerLocal a = (WSManagerLocal) cntx.lookup(name);
			return a;
		} catch (Exception e) {
			Log.out("AgentHelper - catchhh " + e.getMessage());
			return null;
		}
	}

}
