package agilexs.catalogxsadmin.presentation.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import agilexs.catalogxsadmin.presentation.client.ItemPropertiesView.PGPRowView;
import agilexs.catalogxsadmin.presentation.client.Util.DeleteHandler;
import agilexs.catalogxsadmin.presentation.client.Util.ListPropertyTypeBinding;
import agilexs.catalogxsadmin.presentation.client.binding.BindingChangeStateEvent;
import agilexs.catalogxsadmin.presentation.client.binding.BindingConverter;
import agilexs.catalogxsadmin.presentation.client.binding.BindingEvent;
import agilexs.catalogxsadmin.presentation.client.binding.BindingListener;
import agilexs.catalogxsadmin.presentation.client.binding.CheckBoxBinding;
import agilexs.catalogxsadmin.presentation.client.binding.ListBoxBinding;
import agilexs.catalogxsadmin.presentation.client.binding.TextBoxBaseBinding;
import agilexs.catalogxsadmin.presentation.client.cache.CatalogCache;
import agilexs.catalogxsadmin.presentation.client.catalog.Item;
import agilexs.catalogxsadmin.presentation.client.catalog.Label;
import agilexs.catalogxsadmin.presentation.client.catalog.Language;
import agilexs.catalogxsadmin.presentation.client.catalog.Category;
import agilexs.catalogxsadmin.presentation.client.catalog.Property;
import agilexs.catalogxsadmin.presentation.client.catalog.PropertyType;
import agilexs.catalogxsadmin.presentation.client.catalog.PropertyValue;
import agilexs.catalogxsadmin.presentation.client.catalog.PropertyValueBinding;
import agilexs.catalogxsadmin.presentation.client.page.Presenter;
import agilexs.catalogxsadmin.presentation.client.util.BindingTuple;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

/**
 * Presenter that displays a table with all the properties own by the group shown.
 * The user can modify the properties and their default values.
 */
public class ItemPropertiesPresenter implements Presenter<ItemPropertiesView> {

  private static class Tuple extends BindingTuple<PropertyValue, PropertyValueBinding> {

    private static String curLang = null;

    public static void setLanguage(String lang) {
      curLang = lang;
    }

    @Override
    protected String getCurrentLanguage() {
      return curLang;
    }
    
    @Override
    protected PropertyValueBinding newBinding() {
      return new PropertyValueBinding();
    }
  }

  private final ItemPropertiesView view = new ItemPropertiesView();

  private Item currentItem;
  private String language = "en";
  private final List<Tuple> bindings = new ArrayList<Tuple>();
  private final BindingConverter<PropertyType, String> propertyTypeConverter;
  private int activeBindingSize = 0;

  public ItemPropertiesPresenter(String lang) {
    language = lang;
    view.getNewPropertyButton().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        final PGPRowView rowView = view.addRow();
        final PropertyValue dpv = new PropertyValue();
        final PropertyValue pv = new PropertyValue();

        currentItem.getPropertyValues().add(dpv);
        currentItem.getPropertyValues().add(pv);
        pv.setLanguage(language);
        final Property p = new Property();

        p.setType(PropertyType.String);
        p.setCategoryProperty(Boolean.FALSE);
        currentItem.getProperties().add(p);
        p.setCategoryProperty(Boolean.FALSE);
        p.setLabels(new ArrayList<Label>(3));
        final Tuple tuple = createRow(rowView);

        dpv.setProperty(p);
        tuple.setValue(dpv, dpv.getLanguage());
        rowView.setDefaultValueWidget(PropertyType.String);
        pv.setProperty(p);
        tuple.setValue(pv, pv.getLanguage());
        rowView.setValueWidget(PropertyType.String);
        view.setTableVisible(true); //needed in case this is first property
      }
    });
    view.setDeleteHandler(new DeleteHandler<Integer>(){
      @Override
      public void onDelete(Integer index) {
        final Tuple b = bindings.get(index.intValue());
        final Property delProperty = ((PropertyValue) b.getBinding().getData()).getProperty();
        final Long propId = delProperty.getId();
        final ArrayList<PropertyValue> deletes  = new ArrayList<PropertyValue>(3);

        for (PropertyValue pv : currentItem.getPropertyValues()) {
          //if propId == null it's a new not yet stored property
          if ((propId == null && pv.getProperty() == delProperty) || 
              propId.equals(pv.getProperty().getId())) {
            deletes.add(pv);
          }
        }
        for (PropertyValue pv : deletes) {
          currentItem.getPropertyValues().remove(pv);
        }
        for (Property property : currentItem.getProperties()) {
          if ((propId == null && property == delProperty) ||
              propId.equals(property.getId())) {
            currentItem.getProperties().remove(property);
            break;
          }
        }
        show(CatalogCache.get().getActiveCatalog().getLanguages(), language, currentItem);
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
  public ItemPropertiesView getView() {
    return view;
  }

  /**
   * shows the property list.
   * @param values
   */
  public void show(List<Language> langs, String language, Item productGroup) {
    currentItem = productGroup;
    this.language = language;
    Tuple.setLanguage(language);
    final List<PropertyValue[]> values = Util.getCategoryPropertyValues(langs, (Category)productGroup, productGroup);
    final Entry<Long, String> name =
      CatalogCache.get().getCategoryName(productGroup.getId(), language);

    view.setName(name==null ? "" : name.getValue());
    view.resetTable();
    final int bindingSize = bindings.size();
    int i = 0;
    activeBindingSize = 0;
    for (PropertyValue[] pvl : values) {
      final PGPRowView rowView = view.setRow(i);

      if (bindingSize <= i) {
        createRow(rowView);
      }
      for (PropertyValue pv : pvl) {
        bindings.get(i).setValue(pv, pv.getLanguage());
        if (pv.getLanguage() == null){
          Util.bindPropertyValue(pv.getProperty().getType(), rowView.setDefaultValueWidget(pv.getProperty().getType()), bindings.get(i).getDefaultBinding());
          bindings.get(i).refresh();
        } else if (language.equals(pv.getLanguage())) {
//          bindings.get(i).refresh();
          Util.bindPropertyValue(pv.getProperty().getType(), rowView.setValueWidget(pv.getProperty().getType()), bindings.get(i).getBinding());
          bindings.get(i).refresh();
        }
      }
      i++;
    }
    view.setTableVisible(i > 0);
    activeBindingSize = i;
  }

  private Tuple createRow(final PGPRowView rowView) {
    final Tuple pb = new Tuple();

    bindings.add(pb);
    TextBoxBaseBinding.<List<Label>>bind(rowView.getDefaultName(), pb.getDefaultBinding().property().labels(), new BindingConverter<List<Label>, String>() {
      private List<Label> labels;

      @Override
      public List<Label> convertFrom(String data) {
        Label foundLabel = null;
        for (Label label : labels) {
          if (Util.matchLang(null, label.getLanguage())) {
            foundLabel = label;
          }
        }
        if (foundLabel == null) {
          foundLabel = new Label();
          labels.add(foundLabel);
        }
        foundLabel.setLabel(data);
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
              final PropertyType pt = (PropertyType) ((BindingChangeStateEvent) event).getSender().getData();

              Util.bindPropertyValue(pt, rowView.setDefaultValueWidget(lpb
                  .get(rowView.getType().getSelectedIndex())), pb
                  .getDefaultBinding());
              Util.bindPropertyValue(pt, rowView.setValueWidget(lpb.get(rowView
                  .getType().getSelectedIndex())), pb.getBinding());
              //Next line doesn't work because data not set at this point. Bug?
              //rowView.setValueWidget((PropertyType) pb.property().type().getData());
            }
          }
        });
    CheckBoxBinding.bind(rowView.getPGOnly(), pb.getDefaultBinding().property().categoryProperty());
    TextBoxBaseBinding.<List<Label>>bind(rowView.getName(), pb.getBinding().property().labels(), new BindingConverter<List<Label>, String>() {
      private List<Label> labels;

      @Override
      public List<Label> convertFrom(String data) {
        Label foundLabel = null;
        for (Label label : labels) {
          if (Util.matchLang(language, label.getLanguage())) {
            foundLabel = label;
          }
        }
        if (foundLabel == null) {
          foundLabel = new Label();
          labels.add(foundLabel);
        }
        foundLabel.setLabel(data);
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
