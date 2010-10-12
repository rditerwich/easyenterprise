package easyenterprise.server.party;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import easyenterprise.server.EasyEnterpriseEntity;

import static javax.persistence.InheritanceType.SINGLE_TABLE;

@Entity
@Inheritance(strategy = SINGLE_TABLE) 
public class Relation extends EasyEnterpriseEntity {

	@ManyToOne
	@JoinColumn(nullable=false)
	private Party party;
	
	@OneToOne
	private Party relatedParty;
}
