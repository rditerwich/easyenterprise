package easyenterprise.presentation.client.citykids;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.UIObject;

public class MainPanel extends UIObject {

  private static MainPanelUiBinder uiBinder = GWT
      .create(MainPanelUiBinder.class);

  interface MainPanelUiBinder extends UiBinder<Element, MainPanel> {
  }

  @UiField
  SpanElement nameSpan;

  public MainPanel(String firstName) {
    setElement(uiBinder.createAndBindUi(this));
    nameSpan.setInnerText(firstName);
  }

}
