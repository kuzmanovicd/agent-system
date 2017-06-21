package agents;

import models.ACLMessage;
import models.Performative;
import utils.Log;

public class PingAgent extends BaseAgent {

	@Override
	public void handleMessage(ACLMessage msg) {
		ACLMessage msgBack = new ACLMessage();

		if (msg.getPerformative() == Performative.INFORM) {
			Log.out(this, msg.getContent());

			if (msg.getReplyTo() != null) {
				Long sent = Long.parseLong(msg.getReplyWith());
				Long received = System.currentTimeMillis();
				Log.out(this, "Message travel:" + (received - sent) + "ms");
				msgBack.setContent("Ping!");
				msgBack.getReceivers().add(msg.getReplyTo());
				sendMessage(msgBack);
			}
		}

	}

}
