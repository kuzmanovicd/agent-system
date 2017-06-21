package models;

import java.io.Serializable;

public interface Agent extends Serializable {

	void handleMessage(ACLMessage msg);

}
