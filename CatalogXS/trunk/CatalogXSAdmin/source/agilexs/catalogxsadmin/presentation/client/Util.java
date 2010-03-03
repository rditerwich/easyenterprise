package agilexs.catalogxsadmin.presentation.client;

import java.util.List;

import agilexs.catalogxsadmin.presentation.client.catalog.CatalogView;
import agilexs.catalogxsadmin.presentation.client.catalog.Label;
import agilexs.catalogxsadmin.presentation.client.catalog.Property;
import agilexs.catalogxsadmin.presentation.client.catalog.PropertyValue;
import agilexs.catalogxsadmin.presentation.client.services.CatalogServiceAsync;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class Util {

  public static final String NAME = "Name";

  private static CatalogView catalogView;

  public static CatalogView getCatalogView(final AsyncCallback<CatalogView> async) {
    if (catalogView == null) {
      CatalogServiceAsync.findCatalogViewById(1L, new AsyncCallback<CatalogView>(){
        @Override
        public void onFailure(Throwable caught) {
          //TODO implement onFailure Util.getCatalogView
        }

        @Override
        public void onSuccess(CatalogView result) {
          catalogView = result;
          if (async != null) async.onSuccess(result);
        }});
    }
    return catalogView;
  }

  public static Property getPropertyByName(List<Property> properties, String name, String lang) {
    for (Property p : properties) {
      for (Label label : p.getLabels()) {
        if (name.equals(label.getLabel()) &&
             ((lang == null && label.getLanguage() == null) || 
              (lang != null && lang.equals(label.getLanguage())))) {
          return p;
        }
      }
    }
   return null;
  }

  public static PropertyValue getPropertyValueByName(List<PropertyValue> values, String name, String lang) {
    for (PropertyValue propertyValue : values) {
      if(getLabel(propertyValue, name, lang) != null) {
        return propertyValue;
      }
    }
    return null;
  }

  /**
   * Returns the property Label that matches the name and language given a list
   * of propertyValues.
   *
   * @param values
   * @param name
   * @param lang
   * @return
   */
  public static Label getLabel(List<PropertyValue> values, String name, String lang) {
    for (PropertyValue propertyValue : values) {
      final Label label = getLabel(propertyValue, name, lang);

      if (label != null) return label;
    }
    return null;
  }

  public static Label getLabel(PropertyValue value, String name, String lang) {
    if (value == null) return null;
    for (Label label : value.getProperty().getLabels()) {
      if (name.equals(label.getLabel()) &&
          ((lang == null && label.getLanguage() == null) || 
           (lang != null && lang.equals(label.getLanguage())))) {
        return label;
      }
    }
    return null;
  }
}
