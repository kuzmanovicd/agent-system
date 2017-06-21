package beans;

import java.util.ArrayList;
import java.util.HashMap;

import javax.ejb.Local;

import models.ACLMessage;
import models.AID;
import models.AgentCenter;
import models.AgentType;

@Local
public interface CommunicatorLocal {

	void removeNode(AgentCenter node);

	void notifyNodes();

	ArrayList<AgentCenter> nodeRegister();

	HashMap<String, ArrayList<AgentType>> pushAndGetAgentTypes(ArrayList<AgentType> types);

	void nodeDelete();

	boolean heartbeat(AgentCenter node);

	void notifyAllNodesForAgents(HashMap<String, AID> allAgents);

	void sendMessageToNode(ACLMessage message, AgentCenter destination);

	HashMap<String, AID> getRunningAgents();

}
