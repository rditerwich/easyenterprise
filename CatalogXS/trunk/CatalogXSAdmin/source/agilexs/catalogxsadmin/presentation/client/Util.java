package agilexs.catalogxsadmin.presentation.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import agilexs.catalogxsadmin.presentation.client.binding.Binding;
import agilexs.catalogxsadmin.presentation.client.binding.BindingConverters;
import agilexs.catalogxsadmin.presentation.client.binding.CheckBoxBinding;
import agilexs.catalogxsadmin.presentation.client.binding.ListPropertyBinding;
import agilexs.catalogxsadmin.presentation.client.binding.TextBoxBaseBinding;
import agilexs.catalogxsadmin.presentation.client.cache.CatalogCache;
import agilexs.catalogxsadmin.presentation.client.catalog.EnumValue;
import agilexs.catalogxsadmin.presentation.client.catalog.Item;
import agilexs.catalogxsadmin.presentation.client.catalog.Label;
import agilexs.catalogxsadmin.presentation.client.catalog.Language;
import agilexs.catalogxsadmin.presentation.client.catalog.Product;
import agilexs.catalogxsadmin.presentation.client.catalog.ProductGroup;
import agilexs.catalogxsadmin.presentation.client.catalog.Property;
import agilexs.catalogxsadmin.presentation.client.catalog.PropertyType;
import agilexs.catalogxsadmin.presentation.client.catalog.PropertyValue;
import agilexs.catalogxsadmin.presentation.client.catalog.PropertyValueBinding;
import agilexs.catalogxsadmin.presentation.client.widget.EnumValuesEditWidget;
import agilexs.catalogxsadmin.presentation.client.widget.MediaWidget;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.Widget;

public class Util {

  public static interface AddHandler<T> {
    void onAdd(T data);
  }

  public static interface DeleteHandler<T> {
    void onDelete(T data);
  }

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

  //private static final Label EMPTY_LABEL = new Label();

//  static {
//    EMPTY_LABEL.setLabel("UNKNOWN LABEL");
//  }

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
   * The newly created propertyValues are added to the current productGroup.
   *
   * @param pg
   * @param values
   * @return
   */
  public static List<PropertyValue[]> getProductGroupPropertyValues(
      List<Language> langs, ProductGroup pg, Item item) {
    final List<PropertyValue> values = item.getPropertyValues();
    final List<PropertyValue[]> pgValues = new ArrayList<PropertyValue[]>();
    final ArrayList<Language> allLangs = new ArrayList<Language>(langs);

    if (!langs.contains(null)) {
      allLangs.add(null);
    }
    for (Property property : pg.getProperties()) {
      if (property.getItem() == null) {
        property.setItem(pg);
      }
      final PropertyValue[] pgValue = new PropertyValue[allLangs.size()];

      pgValues.add(pgValue);
      for (int i = 0; i < allLangs.size(); i++) {
        final Language lLang = allLangs.get(i);
        final String lang = lLang != null ? lLang.getName() : null;

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
          found.setItem(item);
          item.getPropertyValues().add(found);
        }
        pgValue[i] = found;
/*
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
 */
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

  public static PropertyValue getPropertyValueByLangByName(List<PropertyValue[]> values, String name, String lang) {
    for (PropertyValue[] propertyValues : values) {
      for (PropertyValue propertyValue : propertyValues) {
        if (name.equals(getLabel(propertyValue, lang, true).getLabel())) {
          return propertyValue;
        }
      }
    }
    return null;
  }

  /**
   * Returns the PropertyValue with a Property with the name in the given
   * language or if no Property for that language for the null language.
   *
   * @param values List of PropertyValues to look through.
   * @param name Name of the Property
   * @param lang Language to select
   * @return
   */
  public static PropertyValue getPropertyValueByName(List<PropertyValue> values, String name, String lang) {
    PropertyValue dpv = null;
    PropertyValue pv = null;

    for (PropertyValue propertyValue : values) {
        if (name.equals(getLabel(propertyValue, lang, true).getLabel())) {
          if (propertyValue.getLanguage() == null) {
            dpv = propertyValue;
          } else if (lang != null && lang.equals(propertyValue.getLanguage())){
            pv = propertyValue;
          }
      }
    }
    return pv == null ? dpv : pv;
  }

  public static Label getLabel(PropertyValue value, String lang) {
    return value == null ? null : getLabel(value.getProperty().getLabels(), lang);
  }

  public static Label getLabel(PropertyValue value, String lang, boolean fallback) {
    return value == null ? null : getLabel(value.getProperty().getLabels(), lang, fallback);
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
    if (labels != null) {
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
    }
    final Label lbl = new Label();
    lbl.setLanguage(lang);

    return lbl;
  }

  public static List<Property> filterEmpty(List<Property> properties) {
    for (Property property : properties) {
      final List<Label> nl = new ArrayList<Label>();

      for (Label lbl : property.getLabels()) {
         if (lbl.getLabel() != null || !"".equals(lbl.getLabel()))  {
           nl.add(lbl);
         }
      }
      property.setLabels(nl);
    }
    return properties;
  }

  public static List<PropertyValue> filterEmpty(Collection<PropertyValue> values) {
    final List<PropertyValue> nv = new ArrayList<PropertyValue>();

    for (PropertyValue pv : values) {
      if (!isEmpty(pv)) {
        nv.add(pv);
      }
    }
    return nv;
  }

  /**
   * Returns true if all PropertyValue data fields all null.
   *
   * @param pv PropertyValue
   * @return
   */
  public static boolean isEmpty(PropertyValue pv) {
    return pv == null || (   
        (pv.getStringValue() == null || "".equals(pv.getStringValue()))
        && pv.getIntegerValue() == null
        && pv.getEnumValue() == null && pv.getRealValue() == null
        && pv.getBooleanValue() == null
        && (pv.getMoneyValue() == null || "".equals(pv.getMoneyValue()))
        && pv.getMoneyCurrency() == null && pv.getMediaValue() == null
        && pv.getMimeType() == null);
  }

  /**
   * Returns all ID's of the parents of the given ProductGroup.
   *
   * @param productGroup
   * @return
   */
  public static List<Long> findParents(ProductGroup productGroup) {
    final List<Long> parents = new ArrayList<Long>();

    findParents(productGroup, parents);
    return parents;
  }

  private static void findParents(Item productGroup, List<Long> parents) {
    if (productGroup != null && productGroup.getParents() != null) {
      for (Item parent : productGroup.getParents()) {
        if (!parents.contains(parent.getId())) {
          findParents(parent, parents);
          parents.add(parent.getId());
        }
      }
    }
  }

  public static Binding bindPropertyValue(PropertyType pt, Widget w, PropertyValueBinding pvb) {
    Binding value = null;
    switch (pt) {
    case Enum:
      value = ((EnumValuesEditWidget)w).bind(pvb);
      break;
    case FormattedText:
      value = TextBoxBaseBinding.bind((TextBoxBase)w, pvb.stringValue(), BindingConverters.STRING_CONVERTER);
      break;
    case Media:
      value = ((MediaWidget)w).bind(pvb);
      break;
    case String:
      value = TextBoxBaseBinding.bind((TextBoxBase)w, pvb.stringValue(), BindingConverters.STRING_CONVERTER);
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

  /**
   * Returns a string with all the generic Product properties appended.
   *
   * @param product
   * @param language
   * @return
   */
  public static String productToString(Product product, String language) {
    final List<PropertyValue[]> pvs = Util.getProductGroupPropertyValues(CatalogCache.get().getActiveCatalog().getLanguages(), CatalogCache.get().getProductGroupProduct(), product);
    final StringBuffer s = new StringBuffer();

    for (PropertyValue[] pvhlangs : pvs) {
      PropertyValue dpv = null;
      PropertyValue lpv = null;
      for (PropertyValue propv : pvhlangs) {
        if (language != null && language.equals(propv.getLanguage())) {
          lpv = propv;
        } else if (propv.getLanguage() == null) {
          dpv = propv;
        }
      }
      final String v = Util.propertyValueToString(lpv != null && !Util.isEmpty(lpv) ? lpv : dpv, language);

      s.append(v);
      if (!"".equals(v)) {
        s.append(" ");
      }
    }
    return s.toString();
  }

  /**
   * Converts a propertyValue to String.
   *
   * @param pv
   * @param language
   * @return
   */
  public static String propertyValueToString(PropertyValue pv, String language) {
    if (pv == null) return "";
    Object value = null;

    switch (pv.getProperty().getType()) {
    case Enum:
      for (EnumValue enumValue : pv.getProperty().getEnumValues()) {
        if (enumValue.getId().equals(pv.getEnumValue())) {
          value = getLabel(pv.getProperty().getEnumValues().get(
              pv.getEnumValue()).getLabels(), language, true);
          break;
        }
      }
      break;
    case FormattedText:
      value = pv.getStringValue();
      break;
    case Media:
      value = "";//pv.getStringValue();
      break;
    case String:
      value = pv.getStringValue();
      break;
    case Boolean:
      value = pv.getBooleanValue();
      break;
    case Real:
      value = pv.getRealValue();
      break;
    case Money:
      value = Util.formatMoney(pv.getMoneyValue());
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
      value = pv.getIntegerValue();
    default:
      value = pv.getStringValue();
    }
    return Util.stringValueOf(value);
  }
  /**
   * Formats money to a euro format.
   *
   * @param money
   * @return
   */
  public static String formatMoney(Double money) {
    return money == null ? "" : NumberFormat.getCurrencyFormat("EUR").format(money / 100);
  }

  /**
   * Converts the objectValue to a String and if objectValue == null return an
   * empty String.
   *
   * @param objectValue
   * @return
   */
  public static String stringValueOf(Object objectValue) {
    return objectValue == null ? "" : String.valueOf(objectValue);
  }
}
