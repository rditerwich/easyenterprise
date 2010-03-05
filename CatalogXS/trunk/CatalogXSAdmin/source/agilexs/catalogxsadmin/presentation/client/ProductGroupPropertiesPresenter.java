package agilexs.catalogxsadmin.presentation.client;

import java.util.ArrayList;
import java.util.List;

import agilexs.catalogxsadmin.presentation.client.ProductGroupPropertiesView.PGPRowView;
import agilexs.catalogxsadmin.presentation.client.binding.BindingChangeStateEvent;
import agilexs.catalogxsadmin.presentation.client.binding.BindingConverter;
import agilexs.catalogxsadmin.presentation.client.binding.BindingEvent;
import agilexs.catalogxsadmin.presentation.client.binding.BindingListener;
import agilexs.catalogxsadmin.presentation.client.binding.CheckBoxBinding;
import agilexs.catalogxsadmin.presentation.client.binding.HasTextBinding;
import agilexs.catalogxsadmin.presentation.client.binding.ListBoxBinding;
import agilexs.catalogxsadmin.presentation.client.binding.ListPropertyBinding;
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
  
  private static final List<PropertyType> propertyTypeList = new ArrayList<PropertyType>();
  static {
    for (PropertyType i : PropertyType.values()) {
      propertyTypeList.add(i);
    }
  }

  private final ProductGroupPropertiesView view = new ProductGroupPropertiesView();

  private String language = null;
  private final List<PropertyValueBinding> bindings = new ArrayList<PropertyValueBinding>();
  private final BindingConverter<List<Label>, String> labelBindingConverter;
  private final BindingConverter<PropertyType, String> propertyTypeConverter;

  final ListPropertyBinding<PropertyType> lpb = new ListPropertyBinding<PropertyType>() {
    @Override
    protected List<PropertyType> doGetData() {
      return propertyTypeList;
    }
    @Override
    protected void doSetData(List<PropertyType> data) {
      //Nothing to do, list doesn't change.
    }
  };
  boolean lpbInit = false;

  public ProductGroupPropertiesPresenter() {
    view.getNewPropertyButton().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        final PGPRowView rowView = view.addRow();
        final PropertyValue pv = new PropertyValue();
        final Property p = new Property();
        p.setProductGroupProperty(Boolean.FALSE);
        pv.setProperty(p);
        createRow(rowView).setData(pv);
        lpb.setData(propertyTypeList);
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
    for (PropertyValueBinding pb : bindings) {
      pb.setData(true);
    }
  }

  public void show(List<PropertyValue> pv) {
    view.gridReset();
    final int bindingSize = bindings.size();

    for (int i = 0; i < pv.size(); i++) {
      final PGPRowView rowView = view.setRow(i);

      if (bindingSize <= i) {
        createRow(rowView);
      }
      bindings.get(i).setData(pv.get(i));
      rowView.setValueWidget(pv.get(i).getProperty().getType());
    }
    if (bindings.size() > 0) {
      //lpb.setData(propertyTypeList);
    }
  }

  private PropertyValueBinding createRow(final PGPRowView rowView) {
    final PropertyValueBinding pb = new PropertyValueBinding();

    bindings.add(pb);
    HasTextBinding.<List<Label>>bind(rowView.getName(), pb.property().labels(), labelBindingConverter);
    ListBoxBinding.bind(rowView.getType(), lpb, pb.property().type(), propertyTypeConverter);
//    if (!lpbInit) { lpb.setData(propertyTypeList); lpbInit = true; }
    pb.property().type().addBindingListener(
        new BindingListener() {
          @Override
          public void onBindingChangeEvent(BindingEvent event) {
            if (event instanceof BindingChangeStateEvent) {
              rowView.setValueWidget(propertyTypeList.get(rowView.getType().getSelectedIndex()));
              //Next line doesn't work because data not set at this point. Bug?
              //rowView.setValueWidget((PropertyType) pb.property().type().getData());
            }
          }
        });
    CheckBoxBinding.bind(rowView.getPGOnly(), pb.property().productGroupProperty());
    return pb;
  }
}
