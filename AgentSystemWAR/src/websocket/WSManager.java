package websocket;

import java.io.IOException;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import agents.AgentManagerLocal;
import beans.SessionsManagerBeanLocal;
import models.ACLMessage;
import models.AID;
import utils.Log;

@ServerEndpoint("/websocket")
//@Stateful
@Startup
@Singleton
@LocalBean
public class WSManager implements WSManagerLocal {

	@Inject
	private SessionsManagerBeanLocal sessionsManager;

	@Inject
	private AgentManagerLocal agentManager;

	@PostConstruct
	private void init() {
		Log.out(this, "@PostConstruct");
		if (sessionsManager == null) {
			Log.out(this, "******************Houston, we have a problem!!!********************");
		}
	}

	@OnOpen
	public void onOpen(Session session) {
		Log.out(this, "@OnOpen");
		if (!sessionsManager.getSessions().contains(session)) {
			Log.out(this, "OTVORENA NOVA SESIJA");
			sessionsManager.getSessions().add(session);
			Log.out("Dodao sesiju: " + session.getId() + " u endpoint-u: " + this.hashCode() + ", ukupno sesija: " + sessionsManager.getSessions().size());
			//sendOnlineUsers(session);
		}
	}

	@OnMessage
	public void echoTextMessage(Session session, String msg, boolean last) {

		if (!session.isOpen()) {
			Log.out(this, "Sesija nije otvorena... WTF!???");
			try {
				session.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return;
		}

		JSONObject data = null;

		try {
			JSONObject message = new JSONObject(msg);
			String type;

			type = message.getString("type");
			data = message.getJSONObject("data");

			if (type.equals("create")) {
				agentManager.startAgent(data.getString("type"), data.getString("name"));
				//broadcastRunning(agentManager.getRunningAgents().values());
			} else if (type.equals("acl")) {
				Gson gson = new GsonBuilder().create();
				ACLMessage acl = gson.fromJson(data.toString(), ACLMessage.class);
				Log.out(this, acl.toString());
			} else {
				Log.out(this, "Uknown message type");
				Log.out(msg);
			}
		} catch (Exception e) {
			Log.out(this, "exception - " + e.getMessage());
			Log.out(data.toString());
		}

	}

	@OnClose
	public void close(Session session) {
		Log.out(this, "@OnClose");
		if (session.getUserProperties().containsKey("user")) {
			//TODO
		}
		sessionsManager.getSessions().remove(session);
		Log.out("Zatvorio: " + session.getId() + " u endpoint-u: " + this.hashCode());
	}

	@OnError
	public void error(Session session, Throwable t) {
		Log.out(this, "@OnError");
		if (session.getUserProperties().containsKey("user")) {
			//TODO
		}
		sessionsManager.getSessions().remove(session);
		Log.out("Greška u sessiji: " + session.getId() + " u endpoint-u: " + this.hashCode() + ", tekst: " + t.getMessage());
		t.printStackTrace();
	}

	@Override
	public void broadcast(String msg) {
		JSONObject response = new JSONObject();
		response.put("type", "message");
		response.put("data", msg);

		for (Session s : sessionsManager.getSessions()) {
			try {
				s.getBasicRemote().sendText(response.toString(), true);
			} catch (IOException e) {
				e.printStackTrace();
			}
			//Log.out("Sending '" + msg + "' to : " + s.getId());
		}
	}

	@Override
	public void broadcastRunning(Collection<AID> collection) {
		JSONArray runningJson = new JSONArray(collection);

		JSONObject json = new JSONObject();
		json.put("type", "running");
		json.put("data", runningJson);

		for (Session s : sessionsManager.getSessions()) {
			try {
				Log.out(this, "@broadcastRunning - saljem");
				s.getBasicRemote().sendText(json.toString().replaceAll("class a", "a"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
