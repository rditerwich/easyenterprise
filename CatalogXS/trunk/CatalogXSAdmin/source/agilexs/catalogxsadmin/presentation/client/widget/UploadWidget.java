package agilexs.catalogxsadmin.presentation.client.widget;

import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SubmitButton;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;

public class UploadWidget extends Composite {

  private final Button b = new Button("Upload");
  private final PopupPanel popup = new PopupPanel(true, true);
  private final FileUpload fileUpload = new FileUpload();
  private final FormPanel formPanel = new FormPanel();
  final FlowPanel fp = new FlowPanel();
  private final HashMap<String, InputElement> inputElements = new HashMap<String, InputElement>();
  private final SubmitButton submitButton = new SubmitButton("Upload file");

  public UploadWidget() {
    initWidget(b);
    b.addClickHandler(new ClickHandler() {
      @Override public void onClick(ClickEvent event) {
        popup.showRelativeTo(b);
      }
    });
    fileUpload.setName("fileupload"); // Without this uploads don't work.
    popup.add(formPanel);
    formPanel.add(fp);
    formPanel.setAction(GWT.getModuleBaseURL() + "UploadMedia"); 
    formPanel.setMethod(FormPanel.METHOD_POST);
    formPanel.setEncoding(FormPanel.ENCODING_MULTIPART);

    fp.add(new HTML("Browse a file and press upload file to confirm."));
    fp.add(fileUpload);
    final SimplePanel sbp = new SimplePanel();
    sbp.add(submitButton);
    fp.add(sbp);
  }
  
  public HandlerRegistration addSubmitCompleteHandler(
      SubmitCompleteHandler handler) {
    return formPanel.addSubmitCompleteHandler(handler);
  }

  public void addParam(InputHidden ih) {
    fp.add(ih);
  }

  public void setParam(String name, String value) {
    final InputElement ie = inputElements.containsKey(name) ? inputElements
            .get(name) : Document.get().createHiddenInputElement();

    ie.setValue(value);
    if (!inputElements.containsKey(name)) {
        inputElements.put(name, ie);
        ie.setName(name);
        formPanel.getElement().appendChild(ie);
    }
  }
}
