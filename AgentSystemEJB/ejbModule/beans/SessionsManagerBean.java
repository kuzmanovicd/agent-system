package beans;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.websocket.Session;

import utils.Log;

//@Startup
@Singleton
@LocalBean
public class SessionsManagerBean implements SessionsManagerBeanLocal {

	private List<Session> sessions; 
	
    public SessionsManagerBean() {
        // TODO Auto-generated constructor stub
    }
    
    @PostConstruct
    private void init() {
    	Log.out(this, "@PostConstruct");
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

}
