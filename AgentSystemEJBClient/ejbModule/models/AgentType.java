package models;

import java.io.Serializable;

public class AgentType implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String module;
	
	public AgentType() {
		super();
	}
	
	public AgentType(String name, String module) {
		super();
		this.name = name;
		this.module = module;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof AgentType) {
			return equals2((AgentType)o);
		}
		return false;
	}
	
	public boolean equals2(AgentType type) {
		if(this.name.equals(type.getName()) && this.module.equals(type.getModule())) {
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "AgentType [name = " + name + "; module = " + module + "]";
	}
	
	
}
