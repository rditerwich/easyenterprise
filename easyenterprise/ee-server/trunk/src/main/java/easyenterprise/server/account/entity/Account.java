package easyenterprise.server.account.entity;

import static javax.persistence.CascadeType.ALL;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import easyenterprise.server.common.EasyEnterpriseEntity;

@Entity
public class Account extends EasyEnterpriseEntity {

	@OneToMany(cascade=ALL, mappedBy="account")
	private Set users;
}
