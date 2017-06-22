package agents;

import java.util.Random;

import mdb.AgentServicesBeanLocal;
import models.ACLMessage;
import models.Performative;

public class CNParticipantAgent extends BaseAgent {

	private AgentServicesBeanLocal services = AgentHelper.getAgentServices();

	@Override
	public void handleMessage(ACLMessage msg) {
		int flag = 1;
		log(msg);

		ACLMessage msgBack = new ACLMessage();
		msgBack.setSender(this.getAID());
		msgBack.getReceivers().add(msg.getReplyTo());
		msgBack.setReplyTo(this.getAID());

		if (msg.getPerformative() == Performative.CALL_FOR_PROPOSAL) {
			flag = 0;
			Random randomNum = new Random();
			int result = randomNum.nextInt(2);
			if (result == 0) {
				msgBack.setPerformative(Performative.REFUSE);
			} else {
				msgBack.setPerformative(Performative.PROPOSE);
			}
		} else if (msg.getPerformative() == Performative.ACCEPT_PROPOSAL) {
			flag = 0;
			msgBack.setPerformative(Performative.INFORM);
		} else if (msg.getPerformative() == Performative.REJECT_PROPOSAL) {
			flag = 0;
			msgBack.setPerformative(Performative.INFORM);
		} else if (msg.getPerformative() == Performative.INFORM) {
			flag = 1;
		} else if (msg.getPerformative() == Performative.NOT_UNDERSTOOD) {
			msgBack = null;
			flag = 1;
		} else {
			msgBack.setPerformative(Performative.NOT_UNDERSTOOD);
			msgBack.setContent("This Agent did not understand what was asked of it!");
		}

		if (flag == 0) {
			services.sendMessageToAgent(msgBack);
		}

	}

}
