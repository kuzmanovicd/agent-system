package websocket;

import javax.ejb.Local;

@Local
public interface WSManagerLocal {
	public void broadcast(String msg);
}
