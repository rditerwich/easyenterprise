package agilexs.catalogxsadmin.presentation.client.widget;

import agilexs.catalogxsadmin.presentation.client.binding.Binding;
import agilexs.catalogxsadmin.presentation.client.binding.BindingConverters;
import agilexs.catalogxsadmin.presentation.client.binding.HasTextBinding;
import agilexs.catalogxsadmin.presentation.client.binding.PropertyBinding;
import agilexs.catalogxsadmin.presentation.client.cache.CatalogCache;
import agilexs.catalogxsadmin.presentation.client.catalog.PropertyValue;
import agilexs.catalogxsadmin.presentation.client.catalog.PropertyValueBinding;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;

public class MediaWidget extends Composite {

  final FlowPanel panel = new FlowPanel();
  final Image image = new Image();
  final Anchor a = new Anchor();
  final UploadWidget up = new UploadWidget();
  final InputHidden itemId = new InputHidden();
  final InputHidden pvId = new InputHidden();
  final InputHidden propertyId = new InputHidden();
  private PropertyValueBinding pvb;

  private String id;
  private String pId;
  private String fileName;

  public MediaWidget() {
    initWidget(panel);
    panel.add(image);
    image.setVisible(false);
    image.setSize("20px", "20px");
    panel.add(a);
    a.setTarget("_blank");
    a.setVisible(false);
    panel.add(up);

    itemId.setName("itemId");
    up.addParam(itemId);
    pvId.setName("propertyValueId");
    up.addParam(pvId);
    propertyId.setName("propertyId");
    up.addParam(propertyId);
    up.addSubmitCompleteHandler(new SubmitCompleteHandler() {
      @Override public void onSubmitComplete(SubmitCompleteEvent event) {
        if (pvb != null) {
          CatalogCache.get().getPropertyValue((Long)pvb.id().getData(),
            new AsyncCallback<PropertyValue>() {
              @Override public void onFailure(Throwable caught) {
                // TODO Auto-generated method stub
              }

              @Override public void onSuccess(PropertyValue result) {
                if (result != null) {
                  pvb.setData(result);
                  StatusMessage.get().show(
                    "File '" + result.getStringValue() + "' uploaded.", 15);
                }
              }});
        }
      }
    });
  }

  public Binding bind(final PropertyValueBinding pvb) {
    this.pvb = pvb;
    final Binding b = HasTextBinding.<String>bind(new HasText() {
      //private String text;

      @Override public String getText() {
        return fileName;
      }

      @Override public void setText(String text) {
        fileName = text;
        show(id, (String)getData(pvb.mimeType(), ""), fileName);
        //show((Long)getData(pvb.id(), null), text, (String)getData(pvb.stringValue(), ""));
      }
    }, pvb.mimeType());
    HasTextBinding.<Long>bind(new HasText() {
//      private String id;

      @Override public String getText() {
        return id;
      }

      @Override public void setText(String text) {
        id = text;
        show(id, (String)getData(pvb.mimeType(), ""), fileName);
//        show(id,
//          (String)getData(pvb.mimeType(), ""), (String)getData(pvb.stringValue(), ""));
      }
    }, pvb.id(), BindingConverters.LONG_CONVERTER);
    HasTextBinding.<Long>bind(itemId, pvb.item().id(), BindingConverters.LONG_CONVERTER);
    HasTextBinding.<Long>bind(propertyId, pvb.property().id(), BindingConverters.LONG_CONVERTER);
    HasTextBinding.<Long>bind(pvId, pvb.id(), BindingConverters.LONG_CONVERTER);
    return b;
  }

  private void show(String idS, String text, String filename) {
    final Long id = Long.valueOf(idS == null || "".equals(idS.trim()) ? "0" : idS.trim());

    text = text.trim();
    filename = filename.trim();
    if (text == null || "".equals(text) || "\u00a0".equals(text) ) {
      image.setVisible(false);
      a.setVisible(false);
      image.setUrl("");
      image.setTitle("");
      a.setHref("");
      a.setText("");
    } else if (text.startsWith("image")) {
      image.setVisible(true);
      a.setVisible(false);
      image.setUrl(GWT.getModuleBaseURL() + "DownloadMed?pvId=" + id);
      image.setTitle(filename);
      a.setHref("");
      a.setText("");
    } else {
      a.setVisible(true);
      image.setVisible(false);
      image.setUrl("");
      image.setTitle("");
      a.setHref(GWT.getModuleBaseURL() + "DownloadMed?pvId=" + id);
      a.setText(filename);
    }
  }
  
  private <T> T getData(PropertyBinding<T> data, T dv) {
    return (T) (data == null || data.getData() == null ? dv : data.getData());
  }
}
