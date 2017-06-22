package mdb;

import javax.ejb.Local;
import javax.jms.Message;

import models.ACLMessage;

@Local
public interface AgentServicesBeanLocal {

	boolean sendMessageToAgent(ACLMessage message);

	void reply(Message msg, Boolean success);

	boolean sendMessageToAgentNoResponse(ACLMessage message);

}
