package agilexs.catalogxsadmin.presentation.client.widget;

import agilexs.catalogxsadmin.presentation.client.binding.Binding;
import agilexs.catalogxsadmin.presentation.client.binding.BindingConverters;
import agilexs.catalogxsadmin.presentation.client.binding.HasTextBinding;
import agilexs.catalogxsadmin.presentation.client.binding.PropertyBinding;
import agilexs.catalogxsadmin.presentation.client.cache.CatalogCache;
import agilexs.catalogxsadmin.presentation.client.catalog.Item;
import agilexs.catalogxsadmin.presentation.client.catalog.Property;
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
  final InputHidden language = new InputHidden();
  private PropertyValueBinding pvb;

  private String id;
  
  public MediaWidget() {
    this(true);
  }

  /**
   * Creates MediaWidget if showUploadButton is false no upload button will be
   * displayed, meaning the value can't be changed. Use when only view status
   * needed.
   *
   * @param showUploadButton
   */
  public MediaWidget(boolean showUploadButton) {
    initWidget(panel);
    panel.add(image);
    image.setVisible(false);
    image.setSize("70px", "70px");
    panel.add(a);
    a.setTarget("_blank");
    a.setVisible(false);
    if (showUploadButton) {
      panel.add(up);
  
      itemId.setName("itemId");
      up.addParam(itemId);
      pvId.setName("propertyValueId");
      up.addParam(pvId);
      propertyId.setName("propertyId");
      up.addParam(propertyId);
      language.setName("language");
      up.addParam(language);
      up.addSubmitCompleteHandler(new SubmitCompleteHandler() {
        @Override public void onSubmitComplete(SubmitCompleteEvent event) {
          if (pvb != null) {
            if (((PropertyValue) pvb.getData()).getId() == null) {
               final String idS = event.getResults();
  
               if (idS != null && !"".equals(idS.trim())) {
                  ((PropertyValue) pvb.getData()).setId(Long.valueOf(idS.trim())); 
               }
            }
            up.hide();
            CatalogCache.get().getPropertyValue((Long)pvb.id().getData(),
              new AsyncCallback<PropertyValue>() {
                @Override public void onFailure(Throwable caught) {
                  // TODO Auto-generated method stub
                }
  
                @Override public void onSuccess(PropertyValue result) {
                  if (result != null) {
                    if (result.getItem() == null) {
                      result.setItem((Item) pvb.item().getData());
                    }
                    if (result.getProperty() == null) {
                      result.setProperty((Property) pvb.property().getData());
                    }
                    pvb.setData(result);
                    StatusMessage.get().show(
                      "File '" + result.getStringValue() + "' uploaded.", 15);
                  }
                }});
          }
        }
      });
    }
  }

  /**
   * 
   * @param pvb
   * @return
   */
  public Binding bind(final PropertyValueBinding pvb) {
    this.pvb = pvb;
    final Binding b = HasTextBinding.<Long>bind(new HasText() {
      @Override public String getText() {
        return id;
      }

      @Override public void setText(String text) {
        id = text;
        show(id, (String)getData(pvb.mimeType(), ""),
          (String)getData(pvb.stringValue(), ""));
      }
    }, pvb.id(), BindingConverters.LONG_CONVERTER);
    HasTextBinding.<String>bind(new HasText() {
      private String mimeType;
      @Override public String getText() {
        return mimeType;
      }

      @Override public void setText(String text) {
        mimeType = text;
      }
    }, pvb.mimeType(), BindingConverters.STRING_CONVERTER);
    HasTextBinding.<String>bind(new HasText() {
      private String fileName;
      @Override public String getText() {
        return fileName;
      }

      @Override public void setText(String text) {
        fileName = text;
      }
    }, pvb.stringValue(), BindingConverters.STRING_CONVERTER);
    HasTextBinding.<Long>bind(itemId, pvb.item().id(), BindingConverters.LONG_CONVERTER);
    HasTextBinding.<Long>bind(propertyId, pvb.property().id(), BindingConverters.LONG_CONVERTER);
    HasTextBinding.<Long>bind(pvId, pvb.id(), BindingConverters.LONG_CONVERTER);
    HasTextBinding.bind(language, pvb.language(), BindingConverters.STRING_CONVERTER);
    return b;
  }

  /**
   * 
   * @param idS
   * @param text
   * @param filename
   */
  public void show(String idS, String text, String filename) {
    final Long id = Long.valueOf(idS == null || "".equals(idS.trim()) ? "0" : idS.trim());

    text = text.trim();
    if (filename != null) {
      filename = filename.trim();
    }
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
      image.setUrl(GWT.getModuleBaseURL() + "DownloadMedia?pvId=" + id);
      image.setTitle(filename);
      a.setHref("");
      a.setText("");
    } else {
      a.setVisible(true);
      image.setVisible(false);
      image.setUrl("");
      image.setTitle("");
      a.setHref(GWT.getModuleBaseURL() + "DownloadMedia?pvId=" + id);
      a.setText(filename);
    }
  }

  private <T> T getData(PropertyBinding<T> data, T dv) {
    return (T) (data == null || data.getData() == null ? dv : data.getData());
  }
}
