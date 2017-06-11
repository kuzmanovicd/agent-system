package agents;

import models.ACLMessage;
import models.AID;
import models.Agent;

public abstract class BaseAgent implements Agent {
	
	private static final long serialVersionUID = 1L;
	
	protected AID myAID;
	
	@Override
	public void init(AID id) {
		onInit();
		
	}
	
	protected void onInit() {
		
	}
	
	@Override
	public void stop() {
		onTerminate();
		
	}
	
	protected void onTerminate() {
		
	}
	
	@Override
	public void handleMessage(ACLMessage msg) {
		
	}

	public AID getMyAID() {
		return myAID;
	}

	public void setMyAID(AID myAID) {
		this.myAID = myAID;
	}
	
	
}
