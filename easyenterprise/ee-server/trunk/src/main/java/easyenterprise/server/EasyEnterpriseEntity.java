package easyenterprise.server;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class EasyEnterpriseEntity {

	private long id;
	private long version;
	private long tenant;
}
