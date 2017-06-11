package beans;

import javax.ejb.Local;

import org.json.JSONObject;

@Local
public interface AppManagerBeanLocal {

	void startUp();

	JSONObject load();

}
