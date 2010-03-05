package agilexs.catalogxsadmin.presentation.client;

import java.util.ArrayList;
import java.util.List;

import agilexs.catalogxsadmin.presentation.client.ProductGroupValuesView.PGPRowView;
import agilexs.catalogxsadmin.presentation.client.binding.BindingConverter;
import agilexs.catalogxsadmin.presentation.client.binding.HasTextBinding;
import agilexs.catalogxsadmin.presentation.client.catalog.Label;
import agilexs.catalogxsadmin.presentation.client.catalog.PropertyType;
import agilexs.catalogxsadmin.presentation.client.catalog.PropertyValue;
import agilexs.catalogxsadmin.presentation.client.catalog.PropertyValueBinding;
import agilexs.catalogxsadmin.presentation.client.page.Presenter;

public class ProductGroupValuesPresenter implements Presenter<ProductGroupValuesView> {
  private final ProductGroupValuesView view = new ProductGroupValuesView();

  private String language = null;
  private final List<PropertyValueBinding> bindings = new ArrayList<PropertyValueBinding>();
  private final BindingConverter<List<Label>, String> labelBindingConverter;
  private final BindingConverter<PropertyType, String> propertyTypeConverter;

  public ProductGroupValuesPresenter() {
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
  public ProductGroupValuesView getView() {
    return view;
  }

  //also set data to update labels to show new lanugage version
  public void setLanguage(String language) {
    this.language = language;
    for (PropertyValueBinding pb : bindings) {
      pb.setData(true);
    }
  }

  public void show(String name, List<PropertyValue> pv) {
    view.setName(name);
    view.resizeRows(pv.size());
    final int bindingSize = bindings.size();

    for (int i = 0; i < pv.size(); i++) {
      final PGPRowView rowView = view.setRow(i);

      if (bindingSize <= i) {
        final PropertyValueBinding pb = new PropertyValueBinding();
        bindings.add(pb);
        //name
        HasTextBinding.<List<Label>>bind(rowView.getName(), pb.property().labels(), labelBindingConverter);
        HasTextBinding.<PropertyType>bind(rowView.getType(), pb.property().type(), propertyTypeConverter);
        //HasTextBinding.<PropertyValue>bind(rowView.get)
      }
      bindings.get(i).setData(pv.get(i));
      rowView.setValueWidget(pv.get(i).getProperty().getType());
    }
  }
}
