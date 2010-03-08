package agilexs.catalogxsadmin.presentation.client;

import java.util.ArrayList;
import java.util.List;

import agilexs.catalogxsadmin.presentation.client.ProductGroupPropertiesView.PGPRowView;
import agilexs.catalogxsadmin.presentation.client.Util.ListPropertyTypeBinding;
import agilexs.catalogxsadmin.presentation.client.binding.BindingChangeStateEvent;
import agilexs.catalogxsadmin.presentation.client.binding.BindingConverter;
import agilexs.catalogxsadmin.presentation.client.binding.BindingEvent;
import agilexs.catalogxsadmin.presentation.client.binding.BindingListener;
import agilexs.catalogxsadmin.presentation.client.binding.CheckBoxBinding;
import agilexs.catalogxsadmin.presentation.client.binding.HasTextBinding;
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
    private final PropertyValueBinding defaultBinding = new PropertyValueBinding();
    private final PropertyValueBinding binding = new PropertyValueBinding();
    
    public PropertyValueBinding getBinding() {
      return binding;
    }
    
    public PropertyValueBinding getDefaultBinding() {
      return defaultBinding;
    }

    public void refresh() {
      getBinding().setData(getBinding().getData());
    }
  }

  private final ProductGroupPropertiesView view = new ProductGroupPropertiesView();

  private String language = null;
  private final List<Tuple> bindings = new ArrayList<Tuple>();
  private final BindingConverter<List<Label>, String> labelBindingConverter;
  private final BindingConverter<List<Label>, String> defaultLabelBindingConverter;
  private final BindingConverter<PropertyType, String> propertyTypeConverter;

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
    labelBindingConverter = new BindingConverter<List<Label>, String>() {
      @Override
      public List<Label> convertFrom(String data) {
        return null;
      }

      @Override
      public String convertTo(List<Label> data) {
        return Util.getLabel(data, language).getLabel();
      }};
    defaultLabelBindingConverter = new BindingConverter<List<Label>, String>() {
      @Override
      public List<Label> convertFrom(String data) {
        return null;
      }
      
      @Override
      public String convertTo(List<Label> data) {
        return Util.getLabel(data, null).getLabel();
      }};
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

  @Override
  public ProductGroupPropertiesView getView() {
    return view;
  }

  public void setLanguage(String language) {
    this.language = language;
    for (Tuple pb : bindings) {
      pb.refresh();
    }
  }

  /**
   * shows the property list.
   * @param values
   */
  public void show(String language, List<PropertyValue> values) {
    this.language = language;
    view.gridReset();
    final int bindingSize = bindings.size();
    int i = 0;
    for (PropertyValue pv : values) {
      if (pv.getLanguage() == null){
        final PGPRowView rowView = view.setRow(i);

        if (bindingSize <= i) {
          createRow(rowView);
        }
        bindings.get(i).getDefaultBinding().setData(pv);
        Util.bindPropertyValue(pv, rowView.setDefaultValueWidget(pv.getProperty().getType()), bindings.get(i).getDefaultBinding());
        bindings.get(i).getDefaultBinding().setData(pv);
        //rowView.setDefaultValueWidget(pv.getProperty().getType());
        for (PropertyValue pvd : values) {
          if (pvd.getProperty().getId() == pv.getProperty().getId() &&
              pvd.getLanguage() != null && pvd.getLanguage().equals(language)
          ) {
            bindings.get(i).getBinding().setData(pvd);
            Util.bindPropertyValue(pv, rowView.setValueWidget(pvd.getProperty().getType()), bindings.get(i).getBinding());
            bindings.get(i).getBinding().setData(pvd);
            //rowView.setValueWidget(pvd.getProperty().getType());
            break;
          }
        }
        i++;
      }
    }
    if (bindings.size() > 0) {
      //lpb.setData(propertyTypeList);
    }
  }

  private Tuple createRow(final PGPRowView rowView) {
    final Tuple pb = new Tuple();

    bindings.add(pb);
    HasTextBinding.<List<Label>>bind(rowView.getDefaultName(), pb.getDefaultBinding().property().labels(), defaultLabelBindingConverter);
    final ListPropertyTypeBinding lpb = new ListPropertyTypeBinding();

    ListBoxBinding.bind(rowView.getType(), lpb, pb.getDefaultBinding().property().type(), propertyTypeConverter);
    lpb.set();
    pb.getDefaultBinding().property().type().addBindingListener(
        new BindingListener() {
          @Override
          public void onBindingChangeEvent(BindingEvent event) {
            if (event instanceof BindingChangeStateEvent) {
              Util.bindPropertyValue((PropertyValue) pb.getDefaultBinding().getData(),
                  rowView.setDefaultValueWidget(lpb.get(rowView.getType().getSelectedIndex())), pb.getDefaultBinding());
              Util.bindPropertyValue((PropertyValue) pb.getBinding().getData(),
                  rowView.setValueWidget(lpb.get(rowView.getType().getSelectedIndex())), pb.getBinding());
              //Next line doesn't work because data not set at this point. Bug?
              //rowView.setValueWidget((PropertyType) pb.property().type().getData());
            }
          }
        });
    CheckBoxBinding.bind(rowView.getPGOnly(), pb.getDefaultBinding().property().productGroupProperty());
    HasTextBinding.<List<Label>>bind(rowView.getName(), pb.getBinding().property().labels(), labelBindingConverter);
    return pb;
  }
}
