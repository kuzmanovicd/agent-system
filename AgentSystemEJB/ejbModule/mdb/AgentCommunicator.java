package mdb;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import agents.AgentManager;
import agents.BaseAgent;
import beans.AppManagerBean;
import beans.CommunicatorLocal;
import models.ACLMessage;
import models.AID;
import utils.AppConst;
import utils.Log;

/**
 * Message-Driven Bean implementation class for: AgentCommunicator
 */
@MessageDriven(activationConfig = {@ActivationConfigProperty(propertyName = "destination", propertyValue = "jms/queue/AgentQueue"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")}, mappedName = "jms/queue/AgentQueue")
public class AgentCommunicator implements MessageListener {

	@EJB(beanName = AppConst.COMMUNICATOR_NAME)
	CommunicatorLocal communicator;

	@EJB
	AgentManager agentManager;

	@EJB
	AgentServicesBean services;

	@EJB
	AppManagerBean appManager;

	public AgentCommunicator() {
		// TODO Auto-generated constructor stub
	}

	public void onMessage(Message message) {
		//Log.out(this, "onMessage");

		if (message instanceof ObjectMessage) {
			try {
				ACLMessage msg = (ACLMessage) ((ObjectMessage) message).getObject();
				//Log.out(msg.toString());
				if (msg.getReceivers() == null) {
					Log.out(this, "Reciever list doesn't exists.");
					services.reply(message, false);
					return;
				} else if (msg.getReceivers().size() < 1) {
					Log.out(this, "Reciever list is empty.");
					services.reply(message, false);
					return;
				}

				for (AID reciever : msg.getReceivers()) {

					if (reciever == null) {
						Log.out(this, "receiver null");
						Log.out(msg.toString());
						services.reply(message, false);
						return;
					}

					// check if that agent is running
					if (agentManager.getRunningAgents().containsKey(reciever.getName())) {
						//Log.out(this, reciever.toString());
						// if it's running then check if it's on this node
						if (reciever.getHost().equals(appManager.getThisCenter())) {
							BaseAgent a = agentManager.getMyRunningAgents().get(reciever.getName());

							if (a != null) {
								a.handleMessage(msg);
							} else {
								Log.out(this, "USAO GDE NIJE TREBAO AGENT JE NULL");
							}
						} else {
							//agent is on other node, notify that node with message
							//Log.out(this, "Different host - sending mesage to other node");
							communicator.sendMessageToNode(msg, reciever.getHost());
						}

					} else {
						Log.out(this, "ne postoji trazeni agent");
						services.reply(message, false);
						return;
					}
				}

				services.reply(message, true);
			} catch (JMSException e) {
				Log.out(this, "Exception");
			}
		} else {
			Log.out(this, "THIS SHOULD NEVER HAPPEN");
		}
		//Log.out(this, "onMessage done");
	}

}
