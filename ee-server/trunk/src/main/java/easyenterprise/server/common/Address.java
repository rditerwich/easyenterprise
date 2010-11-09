package easyenterprise.server.common;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Address implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Column(nullable=false) 
	private String street1 = "";
	
	@Column(nullable=false) 
  private String street2 = "";
	
	@Column(nullable=false) 
  private String city = "";
	
	@Column(nullable=false) 
  private String state = "";
	
	@Column(nullable=false) 
  private String postalCode = "";
	
	@Column(nullable=false) 
  private String country = "";
  
  public String getStreet1() {
		return street1;
	}
  
  public Address setStreet1(String street1) {
		this.street1 = street1;
		return this;
	}
  
  public String getStreet2() {
		return street2;
	}
  
  public Address setStreet2(String street2) {
		this.street2 = street2;
		return this;
	}
  
  public String getCity() {
		return city;
	}
  
  public Address setCity(String city) {
		this.city = city;
		return this;
	}
  
  public String getState() {
		return state;
	}
  
  public Address setState(String state) {
		this.state = state;
		return this;
	}
  
  public String getPostalCode() {
		return postalCode;
	}
  
  public Address setPostalCode(String postalCode) {
		this.postalCode = postalCode;
		return this;
	}
  
  public String getCountry() {
		return country;
	}
  
  public Address setCountry(String country) {
		this.country = country;
		return this;
	}
}

