package agents;

import beans.NodeManagerBeanLocal;
import models.ACLMessage;
import models.AID;
import models.Agent;
import websocket.WSManagerLocal;

public abstract class BaseAgent implements Agent {

	private static final long serialVersionUID = 1L;

	protected AID AID;

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

	protected void log(ACLMessage msg) {
		WSManagerLocal ws = AgentHelper.getWSManager();
		NodeManagerBeanLocal node = AgentHelper.getNodeManager();
		if (ws != null) {
			String str = this.getAID().getName() + ": " + msg.getContent() + " from " + msg.getSender().getName();
			ws.broadcast(str);
			node.broadcast(str);
		}
	}
}
