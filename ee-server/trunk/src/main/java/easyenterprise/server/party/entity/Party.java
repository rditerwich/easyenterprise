package easyenterprise.server.party.entity;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.InheritanceType.SINGLE_TABLE;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import easyenterprise.server.common.Address;
import easyenterprise.server.common.EasyEnterpriseEntity;

@Entity
@Inheritance(strategy=SINGLE_TABLE)
public abstract class Party<This extends Party<This>> extends EasyEnterpriseEntity {

	private static final long serialVersionUID = 1L;
	
	@OneToMany(cascade=ALL, mappedBy="party")
	private Set<Relation> relations;
	
	@OneToMany(cascade=ALL, mappedBy="party")
	@OrderBy("label")
	private Set<PartyInfo> infos;

	private AddressInfo invoiceAddress;
	private AddressInfo deliveryAddress;

	public Set<Relation> getRelations() {
		if (relations == null) {
			relations = new HashSet<Relation>();
		}
		return relations;
	}
	
	public Set<PartyInfo> getInfos() {
		if (infos == null) {
			infos = new HashSet<PartyInfo>();
		}
		return infos;
	}
	
	public <T extends PartyInfo> Set<T> getInfos(Class<T> type) {
		return getInfos(type, null);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends PartyInfo> Set<T> getInfos(Class<T> type, String label) {
		HashSet<T> result = new HashSet<T>();
		for (PartyInfo info : getInfos()) {
			if (type.equals(info.getClass()) && (label == null || label.equals(info.getLabel()))) {
				result.add((T) info);
			}
		}
		return result;
	}
	
	public This addInfo(PartyInfo info) {
		getInfos().add(info);
		return getThis();
	}

	public Address getDeliveryAddress() {
		if (deliveryAddress != null) {
			return deliveryAddress.getAddress();
		}
		for (AddressInfo info : getInfos(AddressInfo.class)) {
			return info.getAddress();
		}
		return null;
	}
	
	public Address getInvoiceAddress() {
		if (invoiceAddress != null) {
			return invoiceAddress.getAddress();
		}
		for (AddressInfo info : getInfos(AddressInfo.class)) {
			return info.getAddress();
		}
		return null;
	}
	
	public This addRelation(Relation relation) {
		getRelations().add(relation);
		return getThis();
	}
	
	@SuppressWarnings("unchecked")
	protected This getThis() {
		return (This) this;
	}
}
