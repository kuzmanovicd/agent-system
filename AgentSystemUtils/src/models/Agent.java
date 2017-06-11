package models;

import java.io.Serializable;

public interface Agent extends Serializable {

	void init(AID id);
	void stop();
	void handleMessage(ACLMessage msg);

}
