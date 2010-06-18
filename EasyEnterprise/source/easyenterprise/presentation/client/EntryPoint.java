package easyenterprise.presentation.client;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.RootPanel;

import easyenterprise.presentation.client.citykids.MainPanel;

public class EntryPoint implements com.google.gwt.core.client.EntryPoint {

  @Override
  public void onModuleLoad() {
    MainPanel mainPanel = new MainPanel("HIIIIIIIIIIIII");
    Document.get().getBody().appendChild(mainPanel.getElement());
  }

}
