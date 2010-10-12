package easyenterprise.server.party;

import java.util.Set;

import javax.persistence.Inheritance;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;

import easyenterprise.server.EasyEnterpriseEntity;

import static javax.persistence.InheritanceType.SINGLE_TABLE;
import static javax.persistence.CascadeType.ALL;

@MappedSuperclass
@Inheritance(strategy=SINGLE_TABLE)
public abstract class Party extends EasyEnterpriseEntity {

	@OneToMany(cascade=ALL, mappedBy="party")
	private Set<Relation> relations;
}
