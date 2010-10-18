package easyenterprise.server.party.entity;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.InheritanceType.SINGLE_TABLE;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.OneToMany;

import easyenterprise.server.common.EasyEnterpriseEntity;

@Entity
@Inheritance(strategy=SINGLE_TABLE)
public abstract class Party<This extends Party<This>> extends EasyEnterpriseEntity {

	private static final long serialVersionUID = 1L;
	
	@OneToMany(cascade=ALL, mappedBy="party")
	private Set<Relation> relations;
	
	public Set<Relation> getRelations() {
		if (relations == null) {
			relations = new HashSet<Relation>();
		}
		return relations;
	}
	
	public This setRelations(Set<Relation> relations) {
		this.relations = relations;
		return getThis();
	}
	
	public This addRelations(Set<Relation> relations) {
		this.relations = relations;
		return getThis();
	}
	
	@SuppressWarnings("unchecked")
	protected This getThis() {
		return (This) this;
	}
}
