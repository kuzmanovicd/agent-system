package beans;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.json.JSONObject;

import models.AgentCenter;
import proxy.NodeManagerProxy;
import utils.AppConst;
import utils.HTTP;
import utils.Log;

/**
 * Session Bean implementation class AppManagerBean
 */
@Singleton
//@Startup
@LocalBean
@Lock(LockType.READ)
public class AppManagerBean implements AppManagerBeanLocal {

	@EJB(beanName = AppConst.COMMUNICATOR_NAME)
	CommunicatorLocal communicator;

	private AgentCenter thisCenter;
	private AgentCenter masterCenter;
	//private ArrayList<AgentCenter> allCenters;
	private ResteasyClient client;
	private NodeManagerProxy masterRest;

	private int thisPort;
	private int masterPort;

	public AppManagerBean() {

	}

	@PostConstruct
	public void init() {
		//Log.out(this, "@PostConstruct");
		//allCenters = new ArrayList<AgentCenter>();
		client = new ResteasyClientBuilder().connectionPoolSize(10).build();
		startUp();
	}

	@PreDestroy
	public void destroy() {
		//Log.out(this, "@PreDestroy");
		if (!isMaster()) {

		}
	}

	@Override
	public void startUp() {
		JSONObject json = load();
		try {
			JSONObject host = json.getJSONObject("this_host");
			JSONObject master = json.getJSONObject("master_host");

			this.thisCenter = new AgentCenter(host.getString("address"), host.getString("alias"), host.getInt("port"));

			this.masterCenter = new AgentCenter(master.getString("address"), master.getString("alias"), master.getInt("port"));

			String url = HTTP.gen(masterCenter.getAddress(), AppConst.WAR_NAME, AppConst.REST_ROOT) + "cluster";
			ResteasyWebTarget rtarget = client.target(url);
			masterRest = (NodeManagerProxy) rtarget.proxy(NodeManagerProxy.class);
		} catch (Exception e) {
			Log.out(this, "exception startup - " + e.getMessage());
		}

	}

	@Override
	public JSONObject load() {
		Scanner in = null;
		JSONObject json = null;
		try {
			in = new Scanner(new FileReader(AppConst.HOST_FILE));
			String json_raw = "";
			while (in.hasNextLine()) {
				json_raw += in.nextLine();
			}
			json = new JSONObject(json_raw);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			in.close();
		}

		Log.out(this, json.toString());
		return json;
	}

	public AgentCenter getThisCenter() {
		return thisCenter;
	}

	public void setThisCenter(AgentCenter thisCenter) {
		this.thisCenter = thisCenter;
	}

	public AgentCenter getMasterCenter() {
		return masterCenter;
	}

	public void setMasterCenter(AgentCenter masterCenter) {
		this.masterCenter = masterCenter;
	}

	public ResteasyClient getClient() {
		return client;
	}

	public void setClient(ResteasyClient client) {
		this.client = client;
	}

	public Boolean isMaster() {
		return thisCenter.equals(masterCenter);
	}

}
