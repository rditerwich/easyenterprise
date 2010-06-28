package easyenterprise.presentation.client;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

import easyenterprise.presentation.client.citykids.MainPanel;

public class EntryPoint implements com.google.gwt.core.client.EntryPoint {

  @Override
  public void onModuleLoad() {
    // Make a new button that does something when you click it.
    final ToggleButton toggleButton = new ToggleButton("Up", "Down");
    toggleButton.addClickListener(new ClickListener() {
      public void onClick(Widget sender) {
        if (toggleButton.isDown()) {
          Window.alert("I have been toggled down");
        } else {
          Window.alert("I have been toggled up");
        }
      }
    });

    // In a real application, you would have to have css styles defined for
    // gwt-ToggleButton-up,gwt-ToggleButton-up-hovering,gwt-ToggleButton-up-disabled,
    // gwt-ToggleButton-down,.gwt-ToggleButton-down-hovering,.gwt-ToggleButton-down-disabled

    // Add the ToggleButton to the root panel.
    RootPanel.get().add(new MainPanel("iiii"));
    
//    MainPanel mainPanel = new MainPanel("iiii");
//    Document.get().getBody().appendChild(mainPanel.getElement());
  }

}
