package agents;

import java.util.Random;

import mdb.AgentServicesBeanLocal;
import models.ACLMessage;
import models.Performative;

public class CNInitiatorAgent extends BaseAgent {

	private AgentServicesBeanLocal services = AgentHelper.getAgentServices();

	@Override
	public void handleMessage(ACLMessage msg) {

		int flag = 1;
		log(msg);

		ACLMessage msgBack = new ACLMessage();
		msgBack.setSender(this.getAID());
		msgBack.getReceivers().add(msg.getReplyTo());
		msgBack.setReplyTo(this.getAID());

		if (msg.getPerformative() == Performative.REFUSE) {
			flag = 0;
			msgBack.setPerformative(Performative.INFORM);
			msgBack.setContent("My Call For Proposal was refused!");
		} else if (msg.getPerformative() == Performative.PROPOSE) {
			flag = 0;
			Random randomNum = new Random();
			int result = randomNum.nextInt(2);

			if (result == 0) {
				msgBack.setPerformative(Performative.REJECT_PROPOSAL);
			} else {
				msgBack.setPerformative(Performative.ACCEPT_PROPOSAL);
			}

		} else if (msg.getPerformative() == Performative.NOT_UNDERSTOOD) {
			flag = 1;
		} else if (msg.getPerformative() == Performative.INFORM) {
			flag = 1;
		} else {
			msgBack.setPerformative(Performative.NOT_UNDERSTOOD);
			msgBack.setContent("This Agent did not understand what was asked of it!");
		}

		///
		if (flag == 0) {
			services.sendMessageToAgent(msgBack);
		}

	}

}
