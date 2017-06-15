package beans;

import java.util.ArrayList;
import java.util.HashMap;

import javax.ejb.Local;

import models.AID;
import models.AgentType;

@Local
public interface AgentManagerLocal {

	boolean addMyAgentType(AgentType type);

	boolean addAgentsType(String alias, ArrayList<AgentType> types);

	HashMap<String, ArrayList<AgentType>> getAgentTypes();

	boolean startAgent(AID id);

	boolean stopAgent(AID id);

}
