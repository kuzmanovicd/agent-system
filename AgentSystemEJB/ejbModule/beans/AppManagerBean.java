package beans;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.AccessTimeout;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Lock;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Stateless;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import javax.ejb.LockType;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.json.JSONObject;

import logger.Log;
import models.AgentCenter;
import utils.AppConst;
import utils.HTTP;

/**
 * Session Bean implementation class AppManagerBean
 */
@Singleton
@Startup
@LocalBean
@Lock(LockType.READ)
public class AppManagerBean implements AppManagerBeanLocal {

	//@EJB NodeManagerBean nodeManager;
	@EJB(beanName = AppConst.COMMUNICATOR_NAME)
	CommunicatorLocal communicator;
	
	private AgentCenter thisCenter;
    private AgentCenter masterCenter;
    //private ArrayList<AgentCenter> allCenters;
    private ResteasyClient client;
    //private boolean isInitialized;
    
    public AppManagerBean() {
        
    }
    
    @PostConstruct
    public void init() {
    	Log.out(this, "@PostConstruct");
    	//allCenters = new ArrayList<AgentCenter>();
    	client = new ResteasyClientBuilder().connectionPoolSize(50).build();
    	
    	startUp();
    }
    
    @PreDestroy
    public void destroy() {
    	Log.out(this, "@PreDestroy");
    	if(!isMaster()) {
    		String uri = HTTP.gen(this.masterCenter.getAddress(), AppConst.WAR_NAME, AppConst.REST_ROOT) + "cluster/unregister";
    		HTTP.post(getClient(), uri, this.thisCenter).close();
    	}
    	
    }
    
    @Override
	public void startUp() {
		
		JSONObject json = load();
		
		JSONObject host = json.getJSONObject("this_host");
		JSONObject master = json.getJSONObject("master_host");
		
		
		this.thisCenter = new AgentCenter(host.getString("address"), host.getString("alias"));
		this.masterCenter = new AgentCenter(master.getString("address"), master.getString("alias"));
		
		if(json.getBoolean("master")) {
			Log.out(this, "Ovaj Agent Centar je master " + this.thisCenter.toString());
			//nodeManager.getAllCenters().add(this.thisCenter);
		} else {
			Log.out(this, "Agent Centar je slave... Pokusaj konektovanja na master centar...");
			communicator.registerNode(this.thisCenter, this.masterCenter);
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
