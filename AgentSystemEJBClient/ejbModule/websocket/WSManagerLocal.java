package websocket;

import java.util.Collection;

import javax.ejb.Local;

import models.AID;

@Local
public interface WSManagerLocal {
	public void broadcast(String msg);

	void broadcastRunning(Collection<AID> collection);
}
