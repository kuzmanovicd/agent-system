package beans;

import javax.ejb.Local;

import models.AgentType;

@Local
public interface AgentManagerLocal {

	boolean addAgentType(AgentType type);

	boolean addMyAgentType(AgentType type);

}
