package easyenterprise.server.party;

import javax.persistence.Entity;

@Entity
public class Person extends Party {
	
	private String firstName;
	private String middleName;
	private String lastName;
}