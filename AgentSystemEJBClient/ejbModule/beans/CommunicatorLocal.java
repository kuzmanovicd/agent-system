package beans;

import java.util.ArrayList;

import javax.ejb.Local;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;

import models.AgentCenter;
import models.AgentType;

@Local
public interface CommunicatorLocal {

	void removeNode(AgentCenter node);

	void notifyNodes();

	ArrayList<AgentType> retrieveAgentCenterClasses(AgentCenter node);

	void registerNode(AgentCenter thisCenter, AgentCenter masterCenter, ResteasyClient client);

}
