package easyenterprise.server.party.entity;

import static javax.persistence.InheritanceType.SINGLE_TABLE;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;

import easyenterprise.server.common.EasyEnterpriseEntity;

@Entity
@Inheritance(strategy=SINGLE_TABLE)
public abstract class PartyInfo extends EasyEnterpriseEntity {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("rawtypes")
	@JoinColumn(nullable=false) 
	private Party party;
	
	@Column(nullable=false) 
	private String label;
	
	public Party<?> getParty() {
		return party;
	}
	
	public String getLabel() {
		return label;
	}
	
	public PartyInfo setLabel(String label) {
		this.label = label;
		return this;
	}
	
	
}
