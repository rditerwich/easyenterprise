package easyenterprise.server.party.entity;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;

import easyenterprise.server.common.Address;

@Entity
public class AddressInfo extends PartyInfo {
	
	private static final long serialVersionUID = 1L;
	
	@Column(nullable=false) 
	@Embedded
	private Address address;

	public Address getAddress() {
		if (address == null) {
			address = new Address();
		}
		return address;
	}
	
}
