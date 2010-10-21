package easyenterprise.server.party.entity;

import javax.persistence.Entity;

import com.google.common.base.Strings;

import easyenterprise.lib.util.StringUtil;

@Entity
public class Person extends Party<Person> {
	
	private static final long serialVersionUID = 1L;
	
	private String firstName;
	private String middleName;
	private String lastName;
	
	public String getFirstName() {
		return firstName;
	}
	public Person setFirstName(String firstName) {
		this.firstName = firstName;
		return this;
	}
	public String getMiddleName() {
		return middleName;
	}
	public Person setMiddleName(String middleName) {
		this.middleName = middleName;
		return this;
	}
	public String getLastName() {
		return lastName;
	}
	public Person setLastName(String lastName) {
		this.lastName = lastName;
		return this;
	}
	@Override
	public String toString() {
		return StringUtil.mkString(" ", firstName, middleName, lastName);
	}
}