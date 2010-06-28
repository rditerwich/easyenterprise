package easyenterprise.presentation.client.citykids;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

import easyenterprise.presentation.client.citykids.topmenu.TopMenuResources;

public class MainPanel extends Composite {

	interface MainPanelUiBinder extends UiBinder<Widget, MainPanel> {};
  private static MainPanelUiBinder uiBinder = GWT
      .create(MainPanelUiBinder.class);


  public MainPanel(String firstName) {
    initWidget(uiBinder.createAndBindUi(this));
  }

}
