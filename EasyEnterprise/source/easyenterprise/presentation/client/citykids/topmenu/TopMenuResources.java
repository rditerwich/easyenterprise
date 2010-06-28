package easyenterprise.presentation.client.citykids.topmenu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

public interface TopMenuResources extends ClientBundle {

	public static final TopMenuResources instance =  GWT.create(TopMenuResources.class);

	@Source("TopMenu.css")
  CssResource css();
  
	@Source("BuitenSchoolseOpvangButton.png")
	public ImageResource buitenSchoolseOpvangButton();

	@Source("BuitenSchoolseOpvangButtonHover.png")
	public ImageResource buitenSchoolseOpvangButtonHover();
	
	@Source("BuitenSchoolseOpvangButtonSelected.png")
	public ImageResource buitenSchoolseOpvangButtonSelected();
	
	@Source("BuitenSchoolseOpvangButtonDisabled.png")
	public ImageResource buitenSchoolseOpvangButtonDisabled();
	
	@Source("KinderDagVerblijfButton.png")
	public ImageResource KinderDagVerblijfButton();
	
	@Source("KinderDagVerblijfButtonHover.png")
	public ImageResource KinderDagVerblijfButtonHover();
	
	@Source("KinderDagVerblijfButtonSelected.png")
	public ImageResource KinderDagVerblijfButtonSelected();
	
	@Source("KinderDagVerblijfButtonDisabled.png")
	public ImageResource KinderDagVerblijfButtonDisabled();
	
}
