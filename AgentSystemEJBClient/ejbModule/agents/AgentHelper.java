package agents;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import mdb.AgentServicesBeanLocal;
import utils.AppConst;
import utils.Log;

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

}
