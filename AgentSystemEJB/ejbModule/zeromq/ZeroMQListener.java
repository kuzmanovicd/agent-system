package zeromq;

import java.util.ArrayList;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.json.JSONArray;
import org.json.JSONObject;
import org.zeromq.ZMQ;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import agents.AgentManager;
import beans.AppManagerBean;
import beans.NodeManagerBean;
import models.AgentCenter;
import utils.AppConst;
import utils.JSON;
import utils.Log;
import utils.MsgType;

/**
 * Session Bean implementation class ZeroMQListener
 */
@MessageDriven(activationConfig = {@ActivationConfigProperty(propertyName = "destination", propertyValue = "jms/queue/ZeroMQQueue"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")}, mappedName = "jms/queue/ZeroMQQueue")
public class ZeroMQListener implements MessageListener {

	@EJB
	AppManagerBean appManager;

	@EJB
	AgentManager agentManager;

	@EJB
	NodeManagerBean nodeManager;

	public ZeroMQListener() {

	}

	@Override
	public void onMessage(Message arg0) {
		if (AppConst.COMMUNICATOR_NAME.equals("rest")) {
			return;
		}

		try {
			listen();
		} catch (Exception e) {
			e.printStackTrace();
			Log.out(this, "EXCEPTION - " + e.getMessage());
		}

	}

	@Asynchronous
	public void listen() throws Exception {
		Log.out(this, "listen()");
		ZMQ.Context context = ZMQ.context(1);
		ZMQ.Socket responder = context.socket(ZMQ.REP);
		String url = "tcp://*:" + appManager.getThisCenter().getPort();
		//Log.out(this, "URL: " + url);
		responder.bind(url);

		while (!Thread.currentThread().isInterrupted()) {
			String request_str = responder.recvStr(0);
			JSONObject request = new JSONObject(request_str);
			Log.out(this, "ZeroMQ Primio poruku: " + request_str);
			responder.send(process(request));
		}

		responder.close();
		context.term();
	}

	private String process(JSONObject o) {
		try {
			Gson gson = new GsonBuilder().create();
			MsgType type = gson.fromJson(o.get("type").toString(), MsgType.class);

			Log.out(this, "Msg Type - " + type);
			if (type == MsgType.REGISTER_NODE) {
				AgentCenter node = (AgentCenter) JSON.parse(o.get("data").toString(), AgentCenter.class);
				ArrayList<AgentCenter> nodes = nodeManager.nodeRegister(node);
				JSONArray ret = new JSONArray(nodes);
				Log.out(this, ret.toString());
				return ret.toString();
			} else if (type == MsgType.DELETE_NODE) {
				AgentCenter node = (AgentCenter) JSON.parse(o.get("data").toString(), AgentCenter.class);
				nodeManager.nodeDelete(node.getAlias());
				return "deleted";
			}
		} catch (Exception e) {
			Log.out(this, "Exception - " + e.getMessage());

		}

		return null;
	}

}
