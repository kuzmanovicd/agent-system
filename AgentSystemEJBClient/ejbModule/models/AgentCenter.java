package models;

import java.io.Serializable;

public class AgentCenter implements Serializable {

	private String address;
	private String alias;
	private int port;

	public AgentCenter() {
		super();
		this.address = "";
		this.alias = "";
	}

	public AgentCenter(String address, String alias) {
		super();
		this.address = address;
		this.alias = alias;
	}

	public AgentCenter(String address, String alias, int tcp_port) {
		super();
		this.address = address;
		this.alias = alias;
		this.port = tcp_port;
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

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public String toString() {
		return alias + " - " + address;
	}

	public Boolean equals(AgentCenter host) {
		if (!getAddress().equals(host.getAddress()) || !getAlias().equals(host.getAlias())) {
			return false;
		}
		return true;
	}
}
