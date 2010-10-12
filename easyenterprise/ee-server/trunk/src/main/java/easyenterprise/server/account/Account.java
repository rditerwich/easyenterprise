package easyenterprise.server.account;

import static javax.persistence.CascadeType.ALL;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

@Entity
public class Account {

	@OneToMany(cascade=ALL, mappedBy="account")
	private Set<User> users;
}
