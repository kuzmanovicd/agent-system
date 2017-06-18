package models;

import java.io.Serializable;

public class AID implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private AgentCenter node;
	private String name;
	private AgentType type;
	
	public AID() {
		super();
	}
	
	public AID(AgentCenter node, String name, AgentType type) {
		super();
		this.node = node;
		this.name = name;
		this.type = type;
	}

	public AgentCenter getHost() {
		return node;
	}

	public void setHost(AgentCenter host) {
		this.node = host;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public AgentType getType() {
		return type;
	}

	public void setType(AgentType type) {
		this.type = type;
	}
	
	@Override
	public boolean equals(Object o) {
		//implement
		return true;
	}
	
	@Override
	public String toString(){
		//return host.getAlias()+":"+type.getName()+name;
		return "";
	}
	
	
}
