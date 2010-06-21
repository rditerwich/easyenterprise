package easyenterprise.presentation.client.citykids;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

import easyenterprise.presentation.client.citykids.resources.CityKidsResources;

public class MainPanel extends Composite {

	interface MainPanelUiBinder extends UiBinder<Widget, MainPanel> {};
  private static MainPanelUiBinder uiBinder = GWT
      .create(MainPanelUiBinder.class);


  @UiField
  HTMLPanel panel;
  
  @UiField
  ToggleButton bsoButton;
  
  public ImageResource buitenSchoolseOpvangButtonDisabled() {
	  return Resources.INSTANCE.buitenSchoolseOpvangButtonDisabled();
  }
  
//  @UiField
  DivElement topButtons;

  public MainPanel(String firstName) {
    initWidget(uiBinder.createAndBindUi(this));
  
    bsoButton.getUpHoveringFace().setImage(new Image(CityKidsResources.INSTANCE.buitenSchoolseOpvangButtonHover()));
    bsoButton.getUpFace().setImage(new Image(CityKidsResources.INSTANCE.buitenSchoolseOpvangButtonSelected()));
    
//    nameSpan.setInnerText(firstName);
//
//    HorizontalPanel panel = new HorizontalPanel();
    ToggleButton button = new ToggleButton(
    		new Image(CityKidsResources.INSTANCE.buitenSchoolseOpvangButton()),
    		new Image(CityKidsResources.INSTANCE.buitenSchoolseOpvangButtonSelected()));
    
//    panel.add(button);
//    button = new ToggleButton(
//    		new Image(CityKidsResources.INSTANCE.KinderDagVerblijfButton()),
//    		new Image(CityKidsResources.INSTANCE.KinderDagVerblijfButtonSelected()));
//    panel.add(button);
//    panel.add(new Label(CityKidsResources.INSTANCE.KinderDagVerblijfButton().getURL()));
//    topButtons.appendChild(panel.getElement());
//    topButtons.appendChild(new PushButton(new Image(CityKidsResources.INSTANCE.KinderDagVerblijfButton())).getElement());
  }

}
