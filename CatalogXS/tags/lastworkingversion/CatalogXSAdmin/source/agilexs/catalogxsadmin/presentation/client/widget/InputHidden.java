package agilexs.catalogxsadmin.presentation.client.widget;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.TextBoxBase;

public class InputHidden extends TextBoxBase {

  public InputHidden() {
    super(Document.get().createHiddenInputElement());
  }
}