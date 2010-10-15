package easyenterprise.server.common;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class EasyEnterpriseEntity<This extends EasyEnterpriseEntity<This>> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
  @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private long version;
	private long tenant;
	
	public Long getId() {
		return id;
	}
	
	public long getVersion() {
		return version;
	}
	
	public long getTenant() {
		return tenant;
	}
	
	@SuppressWarnings("unchecked")
	protected This getThis() {
		return (This) this;
	}
}
