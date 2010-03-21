package agilexs.catalogxsadmin.presentation.client;

import java.util.ArrayList;
import java.util.List;

import agilexs.catalogxsadmin.presentation.client.ItemValuesView.PGPRowView;
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
public class ItemValuesPresenter implements Presenter<ItemValuesView> {
  private final ItemValuesView view = new ItemValuesView();

  private final List<List<PropertyValueBinding>> bindings = new ArrayList<List<PropertyValueBinding>>();
  private final BindingConverter<PropertyType, String> propertyTypeConverter;
  private List<PropertyValue[]> curValues = new ArrayList<PropertyValue[]>();

  public ItemValuesPresenter() {
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

//  public Collection<PropertyValue> getPropertyValues() {
//    return curValues;
//  }

  @Override
  public ItemValuesView getView() {
    return view;
  }

  public void show(String name, final String language, List<PropertyValue[]> values) {
    curValues.clear();
    curValues.addAll(values);
    view.setName(name);
    final int bindingSize = bindings.size();

    int i = 0;
    for (PropertyValue[] pvLangs : curValues) {
      final PGPRowView rowView = view.setRow(i);
      
      if (bindingSize <= i) {
        final PropertyValueBinding pvdb = new PropertyValueBinding();
        final PropertyValueBinding pvb = new PropertyValueBinding();
        final List<PropertyValueBinding> bm = new ArrayList<PropertyValueBinding>(2);

        bm.add(pvdb);
        bm.add(pvb);
        bindings.add(bm);
        //name
        HasTextBinding.<List<Label>>bind(rowView.getName(), pvdb.property().labels(), new BindingConverter<List<Label>, String>() {
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
        HasTextBinding.<PropertyType>bind(rowView.getType(), pvdb.property().type(), propertyTypeConverter);
        Util.bindPropertyValue(pvLangs[0].getProperty().getType(), rowView.setDefaultValueWidget(pvLangs[0].getProperty().getType()), pvdb);
        Util.bindPropertyValue(pvLangs[0].getProperty().getType(), rowView.setValueWidget(pvLangs[0].getProperty().getType()), pvb);
      }
      for (PropertyValue pv : pvLangs) {
        if (Util.matchLang(null, pv.getLanguage())) {
          bindings.get(i).get(0).setData(pv);
        } else if (/*!Util.isEmpty(pv) &&*/ Util.matchLang(language, pv.getLanguage())) {
          bindings.get(i).get(1).setData(pv);
        }
      }
      i++;
    }
    view.resizeRows(curValues.size());
  }
}
