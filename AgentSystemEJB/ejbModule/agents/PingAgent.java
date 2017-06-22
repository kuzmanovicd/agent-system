package agents;

import mdb.AgentServicesBeanLocal;
import models.ACLMessage;
import models.Performative;
import utils.Log;

public class PingAgent extends BaseAgent {

	@Override
	public void handleMessage(ACLMessage msg) {
		AgentServicesBeanLocal services = AgentHelper.getAgentServices();

		log(msg);

		ACLMessage msgBack = new ACLMessage();
		msgBack.setSender(getAID());
		msgBack.getReceivers().add(msg.getReplyTo());

		if (msg.getPerformative() == Performative.INFORM) {
			Log.out(this, msg.getContent());

			if (msg.getReplyTo() != null) {
				Long sent = Long.parseLong(msg.getReplyWith());
				Long received = System.currentTimeMillis();
				Log.out(this, "Message travel:" + (received - sent) + "ms");
				msgBack.setContent("Ping!");
				msgBack.getReceivers().add(msg.getReplyTo());
			}
		} else if (msg.getPerformative() == Performative.NOT_UNDERSTOOD) {
			Log.out("From " + msg.getSender().getName() + ": " + msg.getContent());
		} else {
			msgBack.setPerformative(Performative.NOT_UNDERSTOOD);
			msgBack.setContent("I don't fucking now what the fuck you want. ok..");
			services.sendMessageToAgent(msgBack);
			//sendMessage(msgBack);
		}

	}

}
