package agents;

import models.ACLMessage;
import models.Performative;
import utils.Log;

public class PongAgent extends BaseAgent {

	@Override
	public void handleMessage(ACLMessage msg) {
		ACLMessage msgBack = new ACLMessage();

		if (msg.getPerformative() == Performative.INFORM) {
			Log.out(this, msg.getContent());

			if (msg.getReplyTo() != null) {
				msgBack.setReplyWith(System.currentTimeMillis() + "");
				msgBack.setSender(getAID());
				msgBack.setPerformative(Performative.INFORM);
				msgBack.setContent("Pong!");
				msgBack.setReplyTo(this.getAID());
				msgBack.getReceivers().add(msg.getReplyTo());
				sendMessage(msgBack);
				//services.sendMessageToAgent(msgBack);
			}
		}

	}

}
