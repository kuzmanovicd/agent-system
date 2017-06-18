package beans;

import java.util.ArrayList;

import javax.ejb.Local;

import models.AgentCenter;

@Local
public interface NodeManagerBeanLocal {

	ArrayList<AgentCenter> nodeRegister(AgentCenter node);

	void updateHost(ArrayList<AgentCenter> nodes);

	boolean deleteNode(String alias);

	Boolean nodeExists(String alias);

	AgentCenter getAgentCenter(String alias);

}
