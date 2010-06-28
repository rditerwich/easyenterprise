package easyenterprise.presentation.client.citykids.topmenu;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ToggleButton;

public class TopMenu extends Composite {

    public TopMenu() {

      Panel panel = new HorizontalPanel();

      TopMenuResources res = TopMenuResources.instance;
      final ToggleButton button = createButton(res.buitenSchoolseOpvangButton(), res.buitenSchoolseOpvangButtonSelected(), res.buitenSchoolseOpvangButtonHover(), res.buitenSchoolseOpvangButtonDisabled());
      final ToggleButton button2 = createButton(res.KinderDagVerblijfButton(), res.KinderDagVerblijfButtonSelected(), res.KinderDagVerblijfButtonHover(), res.KinderDagVerblijfButtonDisabled());
      panel.add(button);
      panel.add(button2);
      initWidget(panel);
    }

    private static ToggleButton createButton(ImageResource up, ImageResource down, ImageResource hover, ImageResource disabled) {
      final ToggleButton button = new ToggleButton();
      button.getUpFace().setImage(new Image(up));
      button.getDownFace().setImage(new Image(down));
      button.getUpHoveringFace().setImage(new Image(hover));
      button.addClickHandler(new ClickHandler() {
        
        @Override
        public void onClick(ClickEvent event) {
          button.setFocus(false);
        }
      });
      return button;
    }
}
