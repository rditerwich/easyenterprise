package easyenterprise.presentation.client.citykids.topmenu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

public class TopMenu extends Composite {
	
	interface TopMenuUiBinder extends UiBinder<Widget, TopMenu> {}
	private static TopMenuUiBinder uiBinder = GWT.create(TopMenuUiBinder.class);

	@UiField
	ToggleButton buitenSchoolseOpvangButton;
	
	@UiField
	ToggleButton kinderDagVerblijfButton;
	
	@UiField
	ToggleButton gastouderBureauButton;
	
	@UiField
	ToggleButton scholenButton;
	
	@UiField
	ToggleButton verenigingButton;
	
	@UiField
	ToggleButton stichtingKindEnVrijeTijdButton;
	
	@UiField
	ToggleButton externeDeskundigenButton;
	
	@UiField
	DivElement contentPanel;
	
	ToggleButton[] allButtons;

	String[] buttonColors;
	
    public TopMenu() {
    	initWidget(uiBinder.createAndBindUi(this));
    	allButtons = new ToggleButton[] {
    		buitenSchoolseOpvangButton, kinderDagVerblijfButton, gastouderBureauButton,
    		scholenButton, verenigingButton, stichtingKindEnVrijeTijdButton,
    		externeDeskundigenButton
    	};
    	buttonColors = new String[] {
    		"0026FF", "FFF30F", "527F3F",
    		"57007F", "FF00DC", "E00000",
    		"FF6A00"
    	};
    	for (final ToggleButton button : allButtons) {
    		button.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					for (final ToggleButton other : allButtons) {
						if (button == other) {
							button.setFocus(false);
						} else {
							other.setDown(false);
						}
					}
					if (button.isDown()) {
						contentPanel.setAttribute("style", "border: 4px solid " + getColor(button) + ";");
					} else {
						contentPanel.setAttribute("style", "border: 4px solid black;");
					}
				}
			});
    	}
    }
    
    public String getColor(ToggleButton button) {
    	for (int i = 0; i < allButtons.length; i++) {
    		if (allButtons[i] == button) {
    			return buttonColors[i];
    		}
    	}
    	return "black";
    }

    private static ToggleButton createButton(ImageResource up, ImageResource down, ImageResource hover, ImageResource disabled) {
      final ToggleButton button = new ToggleButton();
      button.getUpFace().setImage(new Image(up));
      button.getDownFace().setImage(new Image(down));
//      button.getUpHoveringFace().setImage(new Image(hover));
      button.addClickHandler(new ClickHandler() {
        
        @Override
        public void onClick(ClickEvent event) {
          button.setFocus(false);
        }
      });
      return button;
    }
}
