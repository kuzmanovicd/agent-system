package beans;

import java.util.List;

import javax.ejb.Local;
import javax.websocket.Session;

@Local
public interface SessionsManagerBeanLocal {

	List<Session> getSessions();

	void setSessions(List<Session> sessions);

	void broadcast(String msg);

}
