package beans;

import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Stateless;

import logger.Log;
import models.AID;
import models.Agent;
import models.AgentType;

/**
 * Session Bean implementation class AgentManager
 */
@Singleton
@Startup
@LocalBean
public class AgentManager implements AgentManagerLocal {

	private ArrayList<AgentType> allAgentTypes;
	private ArrayList<AID> allRunningAgents;
	private HashMap<String, Agent> allRunningAgentsMyCenter;
	
    public AgentManager() {
        // TODO Auto-generated constructor stub
    }
    
    @PostConstruct
    public void init() {
    	Log.out(this, "@PostConstruct");
    	allAgentTypes = new ArrayList<AgentType>();
    	allRunningAgents = new ArrayList<AID>();
    	allRunningAgentsMyCenter = new HashMap<String, Agent>();
    }
    
    

}
