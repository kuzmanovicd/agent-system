package agents;

import models.ACLMessage;
import models.AID;
import models.Agent;

public abstract class BaseAgent implements Agent {
	
	private static final long serialVersionUID = 1L;
	
	protected AID AID;
	
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
	public abstract void handleMessage(ACLMessage msg);

	public AID getAID() {
		return AID;
	}

	public void setAID(AID AID) {
		this.AID = AID;
	}
	
	@Override
	public String toString() {
		return "Agent [id=" + AID.getName() + "]";
	}
	
	
}
