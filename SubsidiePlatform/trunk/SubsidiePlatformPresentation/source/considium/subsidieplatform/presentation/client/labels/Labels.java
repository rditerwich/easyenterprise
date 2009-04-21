package considium.subsidieplatform.presentation.client.labels;

import com.google.gwt.core.client.GWT;

import considium.subsidieplatform.presentation.client.PresentationLabels;

public interface Labels extends PresentationLabels {
	
	Labels instance = (Labels) GWT.create(Labels.class);

	String login();
}