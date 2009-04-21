package considium.subsidieplatform.presentation.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ImageBundle;

public class Images {
	
	public static Bundle imageBundle = GWT.create(Bundle.class); 

	public static Image mboRaadLogo() {
		return imageBundle.mboRaadLogo().createImage();
	}
	
	public static interface Bundle extends ImageBundle {
		
		@Resource("considium/subsidieplatform/presentation/client/images/mboraad-logo.png")
		public AbstractImagePrototype mboRaadLogo();
		
	}
	
}
