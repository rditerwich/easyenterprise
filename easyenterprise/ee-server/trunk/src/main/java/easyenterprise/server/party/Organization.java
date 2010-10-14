package easyenterprise.server.party;

import javax.persistence.Entity;

@Entity
public class Organization extends Party {
	
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}