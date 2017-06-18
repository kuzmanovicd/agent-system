package beans;

import java.util.ArrayList;

import javax.ejb.Local;

import models.AgentCenter;

@Local
public interface NodeManagerBeanLocal {

	ArrayList<AgentCenter> nodeRegister(AgentCenter node);

	boolean nodeDelete(String alias);

	Boolean nodeExists(String alias);

	AgentCenter getAgentCenter(String alias);

}
