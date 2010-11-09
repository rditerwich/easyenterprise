package easyenterprise.server.planning.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;

import easyenterprise.server.common.EasyEnterpriseEntity;

@Entity
public class Planning extends EasyEnterpriseEntity {

	public enum State { requested, granted } 
	
	private static final long serialVersionUID = 1L;

	@Column(nullable=false) 
	private Date startDate;

	@Column(nullable=false) 
	private Date endDate;

	@Column(nullable=false) 
	private State state = State.requested;
	
	public Date getStartDate() {
		return startDate;
	}
	
	public Planning setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public Planning setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}
	
	public State getState() {
		return state;
	}
	
	public Planning setState(State state) {
		this.state = state;
		return this;
	}
}
