package mdb;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import agents.AgentManager;
import models.ACLMessage;
import utils.Log;

/**
 * Message-Driven Bean implementation class for: AgentCommunicator
 */
@MessageDriven(activationConfig = {@ActivationConfigProperty(propertyName = "destination", propertyValue = "jms/queue/AgentQueue"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")}, mappedName = "jms/queue/AgentQueue")
public class AgentCommunicator implements MessageListener {

	@EJB
	AgentManager agentManager;

	@EJB
	AgentServicesBean services;

	public AgentCommunicator() {
		// TODO Auto-generated constructor stub
	}

	public void onMessage(Message message) {
		Log.out(this, "onMessage");

		if (message instanceof ObjectMessage) {
			try {
				ACLMessage msg = (ACLMessage) ((ObjectMessage) message).getObject();

				services.reply(message, true);
			} catch (JMSException e) {
				Log.out(this, "Exception");
			}
		}
	}

}
