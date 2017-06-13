package beans;

import java.util.ArrayList;

import javax.ejb.Local;

import models.AgentCenter;

@Local
public interface NodeManagerBeanLocal {

	ArrayList<AgentCenter> nodeRegister(AgentCenter node);

	void updateHost(ArrayList<AgentCenter> nodes);

	void unregister(AgentCenter node);

	boolean deleteNode(String alias);

}
