package easyenterprise.server.common;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.Lob;

@Embeddable
public class Media extends EasyEnterpriseEntity {

	private static final long serialVersionUID = 1L;
	
	@Column(nullable=false) 
	private String mimeType = "";

	@Lob
	@Basic(fetch=FetchType.LAZY)
	@Column(nullable=false) 
	private byte[] data;
	
	public String getMimeType() {
		return mimeType;
	}
	
	public Media setMimeType(String mimeType) {
		this.mimeType = mimeType;
		return this;
	}
	
	public byte[] getData() {
		return data;
	}
	
	public Media setData(byte[] data) {
		this.data = data;
		return this;
	}
}
