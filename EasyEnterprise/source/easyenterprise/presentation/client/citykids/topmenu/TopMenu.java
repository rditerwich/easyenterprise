package easyenterprise.presentation.client.citykids.topmenu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Widget;

public class TopMenu extends Composite {

  interface TopMenuUiBinder extends UiBinder<Widget, TopMenu> {};
  private static TopMenuUiBinder uiBinder = GWT
      .create(TopMenuUiBinder.class);

//    @UiField
    FocusPanel bso;
  
    public TopMenu() {
      Resources.instance.css().ensureInjected();
      initWidget(uiBinder.createAndBindUi(this));
//      bso.addClickHandler(new ClickHandler() {
//        
//        @Override
//        public void onClick(ClickEvent event) {
//          // TODO Auto-generated method stub
//          Window.alert("Hello, AJAX");
//          
//        }
//      });
    }
    
//    @UiHandler("bso")
//    void handleClick(ClickEvent e) {
//    }
}
