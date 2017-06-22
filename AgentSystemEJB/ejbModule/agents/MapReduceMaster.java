package agents;

import java.io.File;

import mdb.AgentServicesBeanLocal;
import models.ACLMessage;
import models.AID;
import models.Performative;
import utils.Log;

public class MapReduceMaster extends BaseAgent {

	private int sumWords;
	private AgentServicesBeanLocal services = AgentHelper.getAgentServices();
	private int slaves = 0;
	private AID respondTo;

	@Override
	public synchronized void handleMessage(ACLMessage msg) {

		//log(msg);

		ACLMessage msgBack = new ACLMessage();
		msgBack.setSender(getAID());

		if (msg.getPerformative() == Performative.REQUEST) {
			sumWords = 0;
			analyze(msg.getContent());
			respondTo = msg.getReplyTo();
		} else if (msg.getPerformative() == Performative.ACCEPT_PROPOSAL) {
			sumWords += Integer.parseInt(msg.getContent());
			slaves--;
			AgentHelper.getAgentManager().stopAgent(msg.getSender().getName());
			Log.out(this, "sumwords = " + sumWords);
			Log.out(this, "slaves left = " + slaves);

			if (slaves < 1) {
				msgBack.setContent("Broj reci je: " + sumWords);
				msgBack.getReceivers().add(respondTo);
				msgBack.setPerformative(Performative.INFORM);
				services.sendMessageToAgent(msgBack);
			}

		}

	}

	private void analyze(String dir) {
		File folder = new File(dir);
		File[] listOfFiles = folder.listFiles();

		for (File file : listOfFiles) {
			if (file.isFile() && file.getName().contains(".txt")) {
				slaves++;
			}
		}

		for (File file : listOfFiles) {
			if (file.isFile() && file.getName().contains(".txt")) {

				String absPath = file.getAbsolutePath();

				Log.out(this, "Kreiram novog slejva");
				ACLMessage acl = new ACLMessage();
				AID id = AgentHelper.getAgentManager().startAgent(MapReduceSlave.class.getName(), absPath);
				acl.setSender(this.getAID());
				acl.setReplyTo(this.getAID());
				acl.getReceivers().add(id);
				acl.setPerformative(Performative.CALL_FOR_PROPOSAL);
				acl.setContent(absPath);
				services.sendMessageToAgentNoResponse(acl);

			}
		}
	}

}
