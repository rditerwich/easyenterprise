package easyenterprise.server.party.entity;

import javax.persistence.Column;
import javax.persistence.Embedded;

import easyenterprise.server.common.Media;

public class MediaInfo extends PartyInfo {

	private static final long serialVersionUID = 1L;
	
	@Embedded
	@Column(nullable=false) 
	private Media media;
	
	public Media getMedia() {
		return media;
	}
	
	public MediaInfo setMedia(Media media) {
		this.media = media;
		return this;
	}
}
