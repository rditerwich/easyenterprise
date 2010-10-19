package easyenterprise.server.account.entity;

import static javax.persistence.CascadeType.ALL;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import easyenterprise.server.common.EasyEnterpriseEntity;

@Entity
public class Account extends EasyEnterpriseEntity {

	private static final long serialVersionUID = 1L;
	
	@OneToMany(cascade=ALL, mappedBy="account")
	private Set<User> users;
	
	public Set<User> getUsers() {
		return users;
	}
	
	public Account setUsers(Set<User> users) {
		this.users = users;
		return this;
	}
}
