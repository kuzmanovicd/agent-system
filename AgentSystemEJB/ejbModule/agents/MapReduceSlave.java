package agents;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

import mdb.AgentServicesBeanLocal;
import models.ACLMessage;
import models.Performative;
import utils.Log;

public class MapReduceSlave extends BaseAgent {

	private AgentServicesBeanLocal services = AgentHelper.getAgentServices();

	@Override
	public void handleMessage(ACLMessage msg) {
		ACLMessage msgBack = new ACLMessage();
		log(msg);

		if (msg.getPerformative() == Performative.CALL_FOR_PROPOSAL) {
			Random rand = new Random();
			msgBack.setSender(this.getAID());
			msgBack.setReplyTo(this.getAID());
			msgBack.getReceivers().add(msg.getReplyTo());

			msgBack.setContent(countWords(msg.getContent()) + "");

			msgBack.setPerformative(Performative.ACCEPT_PROPOSAL);
			services.sendMessageToAgent(msgBack);
		}

	}

	private int countWords(String path) {

		boolean diag = true;

		File file = new File(path);
		try (Scanner sc = new Scanner(new FileInputStream(file))) {
			int count = 0;
			while (sc.hasNext()) {
				sc.next();
				count++;
			}

			if (diag == true)
				Log.out(">>> AgentMapReduceSlave: Broj reci u fajlu: " + path + " je: " + count);

			return count;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return -1;

	}

}
