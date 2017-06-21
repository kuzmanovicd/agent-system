package agents;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import mdb.AgentServicesBeanLocal;
import utils.Log;

public class AgentHelper {

	public static AgentServicesBeanLocal getAgentServices() {
		try {
			Context cntx = new InitialContext();
			String name = "java:global/AgentSystemEAR/AgentSystemEJB/AgentServicesBean!" + AgentServicesBeanLocal.class.getName();
			AgentServicesBeanLocal a = (AgentServicesBeanLocal) cntx.lookup(name);
			return a;

		} catch (NamingException e) {
			Log.out("AgentHelper - catchhh " + e.getMessage());
			return null;
		}
	}

}
