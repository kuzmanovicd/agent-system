package beans;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.json.JSONObject;

import logger.Log;
import models.AgentCenter;
import utils.AppConst;
import utils.HTTP;

/**
 * Session Bean implementation class AppManagerBean
 */
@Stateless
@LocalBean
public class AppManagerBean implements AppManagerBeanLocal {

	
	private AgentCenter thisCenter;
    private AgentCenter masterCenter;
    private ArrayList<AgentCenter> allCenters;
    private ResteasyClient client;
    
    public AppManagerBean() {
        
    }
    
    @Override
	public void startUp() {
		
		JSONObject json = load();
		
		JSONObject host = json.getJSONObject("this_host");
		JSONObject master = json.getJSONObject("master_host");
		
		
		this.thisCenter = new AgentCenter(host.getString("address"), host.getString("alias"));
		this.masterCenter = new AgentCenter(master.getString("address"), master.getString("alias"));
		
		if(json.getBoolean("master")) {
			Log.out(this, "Ovaj host je master " + this.thisCenter.toString());
			allCenters.add(this.thisCenter);
		} else {
			Log.out(this, "Host je slave... Pokusaj konektovanja na master host...");
			
			String uri = HTTP.gen(this.masterCenter.getAddress(), AppConst.WAR_NAME, AppConst.REST_ROOT) + "cluster/register";
			Response response = HTTP.post(getClient(), uri, this.thisCenter);
			
			ArrayList<AgentCenter> hosts = response.readEntity(new GenericType<ArrayList<AgentCenter>>() {});
			response.close();
			
			allCenters = hosts;
			Log.out(this, "Ukupan broj hostova: " + hosts.size());
			for(AgentCenter h : hosts) {
				Log.out(this, h.toString());
			}
			
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

	public ArrayList<AgentCenter> getAllCenters() {
		return allCenters;
	}

	public void setAllCenters(ArrayList<AgentCenter> allCenters) {
		this.allCenters = allCenters;
	}

	public ResteasyClient getClient() {
		return client;
	}

	public void setClient(ResteasyClient client) {
		this.client = client;
	}
	
	

}
