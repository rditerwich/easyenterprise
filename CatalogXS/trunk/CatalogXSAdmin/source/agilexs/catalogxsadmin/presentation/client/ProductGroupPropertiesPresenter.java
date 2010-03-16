package agilexs.catalogxsadmin.presentation.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import agilexs.catalogxsadmin.presentation.client.ProductGroupPropertiesView.PGPRowView;
import agilexs.catalogxsadmin.presentation.client.Util.ListPropertyTypeBinding;
import agilexs.catalogxsadmin.presentation.client.binding.BindingChangeStateEvent;
import agilexs.catalogxsadmin.presentation.client.binding.BindingConverter;
import agilexs.catalogxsadmin.presentation.client.binding.BindingEvent;
import agilexs.catalogxsadmin.presentation.client.binding.BindingListener;
import agilexs.catalogxsadmin.presentation.client.binding.CheckBoxBinding;
import agilexs.catalogxsadmin.presentation.client.binding.TextBoxBaseBinding;
import agilexs.catalogxsadmin.presentation.client.binding.ListBoxBinding;
import agilexs.catalogxsadmin.presentation.client.catalog.Label;
import agilexs.catalogxsadmin.presentation.client.catalog.Property;
import agilexs.catalogxsadmin.presentation.client.catalog.PropertyType;
import agilexs.catalogxsadmin.presentation.client.catalog.PropertyValue;
import agilexs.catalogxsadmin.presentation.client.catalog.PropertyValueBinding;
import agilexs.catalogxsadmin.presentation.client.page.Presenter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

/**
 * Presenter class for all properties on a specific ProductGroup. 
 */
public class ProductGroupPropertiesPresenter implements Presenter<ProductGroupPropertiesView> {

  private static class Tuple {
    private static String curLang = null;

    public static void setLanguage(String lang) {
      curLang = lang;
    }

    private final HashMap<String, PropertyValue> langMap = new HashMap<String, PropertyValue>(3);
    private final PropertyValueBinding defaultBinding = new PropertyValueBinding();
    private final PropertyValueBinding binding = new PropertyValueBinding();


    public PropertyValueBinding getBinding() {
      return binding;
    }
    
    public PropertyValueBinding getDefaultBinding() {
      return defaultBinding;
    }

    public Collection<PropertyValue> values() {
      return langMap.values();
    }

    public void refresh() {
      getBinding().setData(langMap.get(curLang));
    }
    
    public void setPropertyValue(PropertyValue pv) {
      langMap.put(pv.getLanguage(), pv);
    }
  }

  private final ProductGroupPropertiesView view = new ProductGroupPropertiesView();

  private String language = null;
  private final List<Tuple> bindings = new ArrayList<Tuple>();
  private final BindingConverter<PropertyType, String> propertyTypeConverter;
  private int activeBindingSize = 0;

  public ProductGroupPropertiesPresenter() {
    view.getNewPropertyButton().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        final PGPRowView rowView = view.addRow();
        final PropertyValue pv = new PropertyValue();
        final Property p = new Property();
        p.setProductGroupProperty(Boolean.FALSE);
        pv.setProperty(p);
        createRow(rowView).getDefaultBinding().setData(pv);
        createRow(rowView).getBinding().setData(pv);
        rowView.setValueWidget(PropertyType.String);
      }
    });
    propertyTypeConverter = new BindingConverter<PropertyType, String>() {
      @Override
      public PropertyType convertFrom(String data) {
        return PropertyType.valueOf(data);
      }
      @Override
      public String convertTo(PropertyType data) {
        return data.toString();
      }
    };
  }

  public List<PropertyValue> getPropertyValues() {
    final List<PropertyValue> values = new ArrayList<PropertyValue>();
    for (int i = 0; i < activeBindingSize; i++) {
      values.addAll(bindings.get(i).values());
    }
    return values;
  }

  public List<Property> getProperties() {
    final List<Property> properties = new ArrayList<Property>();
    for (int i = 0; i < activeBindingSize; i++) {
      PropertyValue pv = (PropertyValue) bindings.get(i).getDefaultBinding().getData();

      properties.add(pv.getProperty());
    }
    return properties;
  }

  @Override
  public ProductGroupPropertiesView getView() {
    return view;
  }

  /**
   * shows the property list.
   * @param values
   */
  public void show(String language, List<PropertyValue> values) {
    this.language = language;
    Tuple.setLanguage(language);
    view.resetTable();
    final int bindingSize = bindings.size();
    int i = 0;
    activeBindingSize = 0;
    for (PropertyValue pv : values) {
      if (pv.getLanguage() == null){
        final PGPRowView rowView = view.setRow(i);

        if (bindingSize <= i) {
          createRow(rowView);
        }
        bindings.get(i).getDefaultBinding().setData(pv);
        Util.bindPropertyValue(pv.getProperty().getType(), rowView.setDefaultValueWidget(pv.getProperty().getType()), bindings.get(i).getDefaultBinding());
        bindings.get(i).getDefaultBinding().setData(pv);
        //rowView.setDefaultValueWidget(pv.getProperty().getType());
        for (PropertyValue pvd : values) {
          if (pvd.getProperty().getId() == pv.getProperty().getId() &&
              pvd.getLanguage() != null) {
            bindings.get(i).setPropertyValue(pvd);
            if (pvd.getLanguage().equals(language)) {
              bindings.get(i).refresh();
              Util.bindPropertyValue(pv.getProperty().getType(), rowView.setValueWidget(pvd.getProperty().getType()), bindings.get(i).getBinding());
              bindings.get(i).refresh();
            }
          }
        }
        i++;
      }
    }
    activeBindingSize = i;
  }

  private Tuple createRow(final PGPRowView rowView) {
    final Tuple pb = new Tuple();

    bindings.add(pb);
    TextBoxBaseBinding.<List<Label>>bind(rowView.getDefaultName(), pb.getDefaultBinding().property().labels(), new BindingConverter<List<Label>, String>() {
      private List<Label> labels;

      @Override
      public List<Label> convertFrom(String data) {
        for (Label label : labels) {
          if (Util.matchLang(null, label.getLanguage())) {
            label.setLabel(data);
          }
        }
        return labels;
      }
      
      @Override
      public String convertTo(List<Label> data) {
        labels = data;
        return Util.getLabel(data, null).getLabel();
      }});
    final ListPropertyTypeBinding lpb = new ListPropertyTypeBinding();

    ListBoxBinding.bind(rowView.getType(), lpb, pb.getDefaultBinding().property().type(), propertyTypeConverter);
    lpb.set();
    pb.getDefaultBinding().property().type().addBindingListener(
        new BindingListener() {
          @Override
          public void onBindingChangeEvent(BindingEvent event) {
            if (event instanceof BindingChangeStateEvent) {
              Util.bindPropertyValue(((PropertyValue) pb.getDefaultBinding().getData()).getProperty().getType(),
                  rowView.setDefaultValueWidget(lpb.get(rowView.getType().getSelectedIndex())), pb.getDefaultBinding());
              Util.bindPropertyValue(((PropertyValue) pb.getBinding().getData()).getProperty().getType(),
                  rowView.setValueWidget(lpb.get(rowView.getType().getSelectedIndex())), pb.getBinding());
              //Next line doesn't work because data not set at this point. Bug?
              //rowView.setValueWidget((PropertyType) pb.property().type().getData());
            }
          }
        });
    CheckBoxBinding.bind(rowView.getPGOnly(), pb.getDefaultBinding().property().productGroupProperty());
    TextBoxBaseBinding.<List<Label>>bind(rowView.getName(), pb.getBinding().property().labels(), new BindingConverter<List<Label>, String>() {
      private List<Label> labels;

      @Override
      public List<Label> convertFrom(String data) {
        for (Label label : labels) {
          if (Util.matchLang(language, label.getLanguage())) {
            label.setLabel(data);
          }
        }
        return labels;
      }

      @Override
      public String convertTo(List<Label> data) {
        labels = data;
        return Util.getLabel(data, language).getLabel();
      }});
    return pb;
  }
}
