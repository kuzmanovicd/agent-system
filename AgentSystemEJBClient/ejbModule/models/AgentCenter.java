package models;

import java.io.Serializable;

public class AgentCenter implements Serializable {
	
	private String address;
	private String alias;
	
	public AgentCenter() {
		super();
		this.address = this.alias = "";
	}
	
	public AgentCenter(String address, String alias) {
		super();
		this.address = address;
		this.alias = alias;
	}
	
	public AgentCenter(String ip, int port, String alias) {
		super();
		this.address = ip + ":" + port;
		this.alias = alias;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	@Override
	public String toString() {
		return alias + " - " + address;
	}
	
	public Boolean equals(AgentCenter host) {
		if(!getAddress().equals(host.getAddress()) || !getAlias().equals(host.getAlias())) {
			return false;
		}		
		return true;
	}
}
