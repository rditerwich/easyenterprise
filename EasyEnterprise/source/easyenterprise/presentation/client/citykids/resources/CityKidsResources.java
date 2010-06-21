package easyenterprise.presentation.client.citykids.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface CityKidsResources extends ClientBundle {

	public static final CityKidsResources INSTANCE =  GWT.create(CityKidsResources.class);

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
