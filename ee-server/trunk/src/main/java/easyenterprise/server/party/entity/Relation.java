package easyenterprise.server.party.entity;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import easyenterprise.server.common.EasyEnterpriseEntity;

import static javax.persistence.InheritanceType.SINGLE_TABLE;

@Entity
@Inheritance(strategy = SINGLE_TABLE) 
public class Relation extends EasyEnterpriseEntity {

	private static final long serialVersionUID = 1L;

  @ManyToOne
	@JoinColumn(nullable=false)
	@SuppressWarnings("rawtypes")
	private Party party;
	
	@OneToOne
	@SuppressWarnings("rawtypes")
	private Party relatedParty;
	
	@SuppressWarnings("unchecked")
  public <T extends Party<T>> T getParty() {
		return (T) party;
	}
	
	public Relation setParty(Party<?> party) {
		this.party = party;
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Party<T>> T getRelatedParty() {
		return (T) relatedParty;
	}
	
	public Relation setRelatedParty(Party<?> party) {
		this.relatedParty = party;
		return this;
	}
	
}
