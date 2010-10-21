package easyenterprise.server.party.entity;

import javax.persistence.Entity;

@Entity
public class Organization extends Party<Organization> {
	
	private static final long serialVersionUID = 1L;

	private String name;

	public String getName() {
		return name;
	}

	public Organization setName(String name) {
		this.name = name;
		return this;
	}
	
	@Override
	public String toString() {
		return name;
	}
}