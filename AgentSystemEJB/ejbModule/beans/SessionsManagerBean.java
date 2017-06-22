package beans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.websocket.Session;

import org.json.JSONObject;

//@Startup
@Singleton
@LocalBean
public class SessionsManagerBean implements SessionsManagerBeanLocal {

	private List<Session> sessions;
	private int rand;

	public SessionsManagerBean() {
		// TODO Auto-generated constructor stub
	}

	@PostConstruct
	private void init() {
		//Log.out(this, "@PostConstruct");
		setSessions(new ArrayList<Session>());
	}

	@Override
	public List<Session> getSessions() {
		return sessions;
	}

	@Override
	public void setSessions(List<Session> sessions) {
		this.sessions = sessions;
	}

	@Override
	public void broadcast(String msg) {
		JSONObject response = new JSONObject();
		response.put("type", "message");
		response.put("data", msg);

		for (Session s : getSessions()) {
			try {
				s.getBasicRemote().sendText(response.toString(), true);
			} catch (IOException e) {
				e.printStackTrace();
			}
			//Log.out("Sending '" + msg + "' to : " + s.getId());
		}
	}

}
