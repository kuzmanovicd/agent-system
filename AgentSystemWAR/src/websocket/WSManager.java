package websocket;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.enterprise.context.RequestScoped;
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

import beans.AppManagerBeanLocal;
import beans.SessionsManagerBeanLocal;
import utils.Log;

@ServerEndpoint("/websocket")
@Stateful
//@Startup
//@Singleton
@LocalBean
public class WSManager implements WSManagerLocal {
	
	//@Inject
	//private AppManagerBean appManager;
	
	@Inject
	private SessionsManagerBeanLocal sessionsManager;
	
	@PostConstruct
	private void init() {
		Log.out(this, "@PostConstruct");
		if(sessionsManager == null) {
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
		
		if(!session.isOpen()) {
			Log.out(this, "Sesija nije otvorena... WTF!???");
			try {
				session.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return;
		} 
		
		//do some shit here
	}
	
	@Override
	public void broadcast(String msg) {
		Log.out(this, "Broadcasting to " + sessionsManager.getSessions().size() + ": " + msg);
		//TODO
	}
	

	@OnClose
	public void close(Session session) {
		Log.out(this, "@OnClose");
		if(session.getUserProperties().containsKey("user")) {
			//TODO
		}
		sessionsManager.getSessions().remove(session);
		Log.out("Zatvorio: " + session.getId() + " u endpoint-u: " + this.hashCode());
	}
	
	@OnError
	public void error(Session session, Throwable t) {
		Log.out(this, "@OnError");
		if(session.getUserProperties().containsKey("user")) {
			//TODO
		}
		sessionsManager.getSessions().remove(session);
		Log.out("Greška u sessiji: " + session.getId() + " u endpoint-u: " + this.hashCode() + ", tekst: " + t.getMessage());
		t.printStackTrace();
	}

	/*
	@Override
	public void broadcastOnline(ArrayList<User> users) {
		Gson gson = new GsonBuilder().create();
		String str = gson.toJson(users);
		Log.out(this, "@broadcastOnline");
		
		JSONArray usersJson = new JSONArray(str);
		
		JSONObject json = new JSONObject();
		json.put("type", "online-users");
		json.put("data", usersJson);
		
		for(Session s : sessionsManager.getSessions()) {
			if(true) {
				try {
					Log.out(this, "@broadcastOnline - saljem");
					s.getBasicRemote().sendText(json.toString());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	
	
	public void sendOnlineUsers(Session session) {
		Gson gson = new GsonBuilder().create();
		
		String str = gson.toJson(userAppDelegate.getOnlineUsers());
		Log.out(this, str);
		JSONArray usersJson = new JSONArray(str);
		
		JSONObject json = new JSONObject();
		json.put("type", "online-users");
		json.put("data", usersJson);
		
		try {
			session.getBasicRemote().sendText(json.toString());
		} catch (IOException e) {
			Log.out(this, "sendOnlineUsers Exception");
		}
	}
	*/

	
}
