package easyenterprise.presentation.client.citykids.topmenu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface TopMenuResources extends ClientBundle {

	public static final TopMenuResources instance =  GWT.create(TopMenuResources.class);

	@Source("TopMenu.css")
    TopMenuCss css();
  
	@Source("BuitenSchoolseOpvangButton.png")
	public ImageResource buitenSchoolseOpvangButton();

	@Source("BuitenSchoolseOpvangButtonHover.png")
	public ImageResource buitenSchoolseOpvangButtonHover();
	
	@Source("BuitenSchoolseOpvangButtonSelected.png")
	public ImageResource buitenSchoolseOpvangButtonSelected();
	
	@Source("BuitenSchoolseOpvangButtonDisabled.png")
	public ImageResource buitenSchoolseOpvangButtonDisabled();
	
	@Source("KinderDagVerblijfButton.png")
	public ImageResource kinderDagVerblijfButton();
	
	@Source("KinderDagVerblijfButtonHover.png")
	public ImageResource kinderDagVerblijfButtonHover();
	
	@Source("KinderDagVerblijfButtonSelected.png")
	public ImageResource kinderDagVerblijfButtonSelected();
	
	@Source("KinderDagVerblijfButtonDisabled.png")
	public ImageResource kinderDagVerblijfButtonDisabled();
	
	@Source("GastouderBureauButton.png")
	public ImageResource gastouderBureauButton();
	
	@Source("GastouderBureauButtonHover.png")
	public ImageResource gastouderBureauButtonHover();
	
	@Source("GastouderBureauButtonSelected.png")
	public ImageResource gastouderBureauButtonSelected();
	
	@Source("GastouderBureauButtonDisabled.png")
	public ImageResource gastouderBureauButtonDisabled();
	
	@Source("ScholenButton.png")
	public ImageResource scholenButton();
	
	@Source("ScholenButtonHover.png")
	public ImageResource scholenButtonHover();
	
	@Source("ScholenButtonSelected.png")
	public ImageResource scholenButtonSelected();
	
	@Source("ScholenButtonDisabled.png")
	public ImageResource scholenButtonDisabled();

	@Source("VerenigingButton.png")
	public ImageResource verenigingButton();
	
	@Source("VerenigingButtonHover.png")
	public ImageResource verenigingButtonHover();
	
	@Source("VerenigingButtonSelected.png")
	public ImageResource verenigingButtonSelected();
	
	@Source("VerenigingButtonDisabled.png")
	public ImageResource verenigingButtonDisabled();
	
	@Source("StichtingKindEnVrijeTijdButton.png")
	public ImageResource stichtingKindEnVrijeTijdButton();
	
	@Source("StichtingKindEnVrijeTijdButtonHover.png")
	public ImageResource stichtingKindEnVrijeTijdButtonHover();
	
	@Source("StichtingKindEnVrijeTijdButtonSelected.png")
	public ImageResource stichtingKindEnVrijeTijdButtonSelected();
	
	@Source("StichtingKindEnVrijeTijdButtonDisabled.png")
	public ImageResource stichtingKindEnVrijeTijdButtonDisabled();
	
	@Source("ExterneDeskundigenButton.png")
	public ImageResource externeDeskundigenButton();
	
	@Source("ExterneDeskundigenButtonHover.png")
	public ImageResource externeDeskundigenButtonHover();
	
	@Source("ExterneDeskundigenButtonSelected.png")
	public ImageResource externeDeskundigenButtonSelected();
	
	@Source("ExterneDeskundigenButtonDisabled.png")
	public ImageResource externeDeskundigenButtonDisabled();
}
