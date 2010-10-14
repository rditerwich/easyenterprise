package easyenterprise.server;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class EasyEnterpriseEntity {

	@Id
  @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private long version;
	private long tenant;
	
	public Long getId() {
		return id;
	}
}
