package agilexs.catalogxsadmin.presentation.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import agilexs.catalogxsadmin.presentation.client.binding.Binding;
import agilexs.catalogxsadmin.presentation.client.binding.BindingConverters;
import agilexs.catalogxsadmin.presentation.client.binding.CheckBoxBinding;
import agilexs.catalogxsadmin.presentation.client.binding.ListPropertyBinding;
import agilexs.catalogxsadmin.presentation.client.binding.TextBoxBaseBinding;
import agilexs.catalogxsadmin.presentation.client.catalog.Label;
import agilexs.catalogxsadmin.presentation.client.catalog.ProductGroup;
import agilexs.catalogxsadmin.presentation.client.catalog.Property;
import agilexs.catalogxsadmin.presentation.client.catalog.PropertyType;
import agilexs.catalogxsadmin.presentation.client.catalog.PropertyValue;
import agilexs.catalogxsadmin.presentation.client.catalog.PropertyValueBinding;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.Widget;

public class Util {

  public static class ListPropertyTypeBinding extends ListPropertyBinding<PropertyType> {
    private static final List<PropertyType> PROPERTY_TYPE_LIST = new ArrayList<PropertyType>();

    static {
      for (PropertyType i : PropertyType.values()) {
        PROPERTY_TYPE_LIST.add(i);
      }
    }

    public PropertyType get(int index) {
      return PROPERTY_TYPE_LIST.get(index);
    }

    @Override
    protected List<PropertyType> doGetData() {
      return PROPERTY_TYPE_LIST;
    }

    @Override
    protected void doSetData(List<PropertyType> data) {
      //Nothing to do, list doesn't change.
    }

    /**
     * Initializes the data set, call this method AFTER the instance is bound.
     */
    public void set() {
      setData(PROPERTY_TYPE_LIST);  
    }
  }
  
  public static final String NAME = "Name";

  private static final Label EMPTY_LABEL = new Label();

  static {
    EMPTY_LABEL.setLabel("UNKNOWN LABEL");
  }

  /**
   * Returns true if the matching languages are both null or if they have the
   * same value.
   *
   * @param lang
   * @param pvLang
   * @return
   */
  public static boolean matchLang(String lang, String pvLang) {
    return (lang == null && pvLang == null) ||
        (lang != null && lang.equals(pvLang)); 
  }

  /**
   * Returns a list of all PropertyValues based on all properties in a 
   * ProductGroup. If no propertyValue is present, a new one is created.
   *   
   * @param pg
   * @param values
   * @return
   */
  public static List<PropertyValue> getProductGroupPropertyValues(
      List<String> langs, ProductGroup pg, List<PropertyValue> values) {
    final List<PropertyValue> pgValues = new ArrayList<PropertyValue>();
    final ArrayList<String> allLangs = new ArrayList<String>(langs);

    if (!langs.contains(null)) {
      allLangs.add(null);
    }
    for (String lang : allLangs) {
      for (Property property : pg.getProperties()) {
        if (property.getItem() == null) {
          property.setItem(pg);
        }
        PropertyValue found = null;
        for (PropertyValue value : values) {
          if (value.getProperty().getId().equals(property.getId()) &&
              ((lang == null && value.getLanguage() ==  null) ||
              lang != null && lang.equals(value.getLanguage()))) {
            found = value;
            break;
          }
        }
        if (found == null) {
          found = new PropertyValue();
          found.setProperty(property);
          found.setLanguage(lang);
        }
        pgValues.add(found);
        boolean foundLabel = false;
        for (Label pl : property.getLabels()) {
          if ((lang == null && pl.getLanguage() == null) ||
              (lang !=null && lang.equals(pl.getLanguage()))) {
            foundLabel = true;
            break;
          }
        }
        if (!foundLabel) {
          final Label l = new Label();
          l.setLanguage(lang);
          property.getLabels().add(l);
        }
      }
    }
    return pgValues;
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
      if (name.equals(getLabel(propertyValue, lang, true).getLabel())) {
        return propertyValue;
      }
    }
    return null;
  }

  public static Label getLabel(PropertyValue value, String lang) {
    return Util.getLabel(value.getProperty().getLabels(), lang);
  }

  public static Label getLabel(PropertyValue value, String lang, boolean fallback) {
    return Util.getLabel(value.getProperty().getLabels(), lang, fallback);
  }

  public static Label getLabel(List<Label> labels, String lang) {
    return getLabel(labels, lang, false);
  }

  /**
   * Returns the label matching the language. If lang is not null, but the list
   * of Labels contains a label with language null this label is returns. If no
   * match could be found, an empty label is returned.
   *  
   * @param labels
   * @param lang
   * @return
   */
  public static Label getLabel(List<Label> labels, String lang, boolean fallback) {
    if (labels == null) return EMPTY_LABEL;
    for (Label label : labels) {
      if ((lang == null && label.getLanguage() == null) || 
          (lang != null && lang.equals(label.getLanguage()) &&
              (!fallback || (label.getLabel() != null && !"".equals(label.getLabel()))))) { 
        return label;
      }
    }
    if (fallback) {
      for (Label label : labels) {
        if (label.getLanguage() == null) { 
          return label;
        }
      }
    }
    return EMPTY_LABEL;
  }
  
  public static List<PropertyValue> filterEmpty(Collection<PropertyValue> values) {
    List<PropertyValue> nv = new ArrayList<PropertyValue>();
    for (PropertyValue pv : values) {
      if (!isEmpty(pv)) {
        nv.add(pv);
      }
    }
    return nv;
  }

  public static boolean isEmpty(PropertyValue pv) {
    return pv.getStringValue() == null && pv.getIntegerValue() == null
        && pv.getEnumValue() == null && pv.getRealValue() == null
        && pv.getBooleanValue() == null && pv.getMoneyValue() == null
        && pv.getMoneyCurrency() == null && pv.getMediaValue() == null
        && pv.getMimeType() == null;
  }

  public static Binding bindPropertyValue(PropertyType pt, Widget w, PropertyValueBinding pvb) {
    Binding value = null;
    switch (pt) {
    case Enum:
      //value = new TextBox();
      break;
    case FormattedText:
      value = TextBoxBaseBinding.bind((TextBoxBase)w, pvb.stringValue());
      break;
    case Media:
      value = TextBoxBaseBinding.bind((TextBoxBase)w, pvb.stringValue());
      break;
    case String:
      value = TextBoxBaseBinding.bind((TextBoxBase)w, pvb.stringValue());
      break;
    case Boolean:
      value = CheckBoxBinding.bind((CheckBox)w, pvb.booleanValue());
      break;
    case Real:
      value = TextBoxBaseBinding.bind((TextBoxBase)w, pvb.realValue(), BindingConverters.DOUBLE_CONVERTER);
      break;
    case Money:
      value = TextBoxBaseBinding.bind((TextBoxBase)w, pvb.moneyValue(), BindingConverters.DOUBLE_CONVERTER);
      break;
    case Acceleration:
    case AmountOfSubstance:
    case Angle:
    case Area:
    case ElectricCurrent:
    case Energy:
    case Frequency:
    case Integer:
    case Length:
    case LuminousIntensity:
    case Power:
    case Time:
    case Mass:
    case Temperature:
    case Velocity:
    case Voltage:
    case Volume:
    default:
      value = TextBoxBaseBinding.bind((TextBoxBase)w, pvb.integerValue(), BindingConverters.INTEGER_CONVERTER);
    }
    return value;
  }

  public static String formatMoney(Double money) {
    return money == null ? "" : NumberFormat.getCurrencyFormat("EUR").format(money / 100);
  }

  public static String stringValueOf(Object objectValue) {
    return objectValue == null ? "" : String.valueOf(objectValue);
  }
}
