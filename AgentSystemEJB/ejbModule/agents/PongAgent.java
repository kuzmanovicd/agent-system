package agents;

import mdb.AgentServicesBeanLocal;
import models.ACLMessage;
import models.Performative;
import utils.Log;

public class PongAgent extends BaseAgent {

	@Override
	public void handleMessage(ACLMessage msg) {
		AgentServicesBeanLocal services = AgentHelper.getAgentServices();

		ACLMessage msgBack = new ACLMessage();
		msgBack.setSender(getAID());
		msgBack.getReceivers().add(msg.getReplyTo());

		if (msg.getPerformative() == Performative.INFORM) {
			Log.out(this, msg.getContent());

			if (msg.getReplyTo() != null) {
				msgBack.setReplyWith(System.currentTimeMillis() + "");
				msgBack.setPerformative(Performative.INFORM);
				msgBack.setContent("Pongic!");
				msgBack.setReplyTo(this.getAID());
			}
		} else if (msg.getPerformative() == Performative.NOT_UNDERSTOOD) {
			Log.out("From " + msg.getSender().getName() + ": " + msg.getContent());
		} else {
			msgBack.setPerformative(Performative.NOT_UNDERSTOOD);
			msgBack.setContent("I don't fucking now what the fuck you want. ok..");
		}

		services.sendMessageToAgent(msgBack);
		//sendMessage(msgBack);

	}

}
