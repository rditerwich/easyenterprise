package agilexs.catalogxsadmin.presentation.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import agilexs.catalogxsadmin.presentation.client.ProductGroupValuesView.PGPRowView;
import agilexs.catalogxsadmin.presentation.client.binding.BindingConverter;
import agilexs.catalogxsadmin.presentation.client.binding.HasTextBinding;
import agilexs.catalogxsadmin.presentation.client.catalog.Label;
import agilexs.catalogxsadmin.presentation.client.catalog.PropertyType;
import agilexs.catalogxsadmin.presentation.client.catalog.PropertyValue;
import agilexs.catalogxsadmin.presentation.client.catalog.PropertyValueBinding;
import agilexs.catalogxsadmin.presentation.client.page.Presenter;

/**
 * 
 */
public class ProductGroupValuesPresenter implements Presenter<ProductGroupValuesView> {
  private final ProductGroupValuesView view = new ProductGroupValuesView();

  private String language = null;
  private final List<PropertyValueBinding> bindings = new ArrayList<PropertyValueBinding>();
  private final BindingConverter<PropertyType, String> propertyTypeConverter;
  private List<PropertyValue> curValues = new ArrayList<PropertyValue>();

  private String name;

  public ProductGroupValuesPresenter() {
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

  public Collection<PropertyValue> getPropertyValues() {
    return curValues;
  }

  @Override
  public ProductGroupValuesView getView() {
    return view;
  }

  public void setValues(String name, List<PropertyValue> values) {
    curValues.clear();
    curValues.addAll(values);
    this.name = name;
  }

  public void show(String lang) {
    this.language = lang;
    view.setName(name);
    final int bindingSize = bindings.size();

    int i = 0;
    for (PropertyValue pv : curValues) {
      if (Util.matchLang(null, pv.getLanguage())) {
        final PGPRowView rowView = view.setRow(i);

        if (bindingSize <= i) {
          final PropertyValueBinding pb = new PropertyValueBinding();
          bindings.add(pb);
          //name
          HasTextBinding.<List<Label>>bind(rowView.getName(), pb.property().labels(), new BindingConverter<List<Label>, String>() {
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
              return Util.getLabel(data, language, true).getLabel();
            }});
          HasTextBinding.<PropertyType>bind(rowView.getType(), pb.property().type(), propertyTypeConverter);
          Util.bindPropertyValue(pv.getProperty().getType(), rowView.setValueWidget(pv.getProperty().getType()), pb);
        }
        bindings.get(i).setData(pv);
        i++;
      }
    }
    view.resizeRows(i);
    i=0;
    for (PropertyValue pv : curValues) {
      if (!Util.isEmpty(pv) && Util.matchLang(language, pv.getLanguage())) {
        //final PGPRowView rowView = view.setRow(i);
        bindings.get(i).setData(pv);
        //rowView.setValueWidget(pv.getProperty().getType());
      }
      i++;
    }
  }
}
