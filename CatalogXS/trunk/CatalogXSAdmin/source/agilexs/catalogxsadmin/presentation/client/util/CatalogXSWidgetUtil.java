package agilexs.catalogxsadmin.presentation.client.util;

import agilexs.catalogxsadmin.presentation.client.catalog.PropertyType;
import agilexs.catalogxsadmin.presentation.client.widget.EnumValuesEditWidget;
import agilexs.catalogxsadmin.presentation.client.widget.MediaWidget;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * CatalogXS specific Widget utils
 */
public class CatalogXSWidgetUtil {

  /**
   * Returns the specific widget to display a specific propertyValue field.
   *
   * @param wrapper Parent to which the widget is attached.
   * @param type
   * @return
   */
  public static Widget setPropertyTypeWidget(SimplePanel wrapper, PropertyType type, boolean edit) {
    Widget value;
    switch (type) {
    case Enum:
      value = new EnumValuesEditWidget(edit);
      break;
    case FormattedText:
      value = new TextBox();
      break;
    case Media:
      value = new MediaWidget();
      break;
    case String:
      value = new TextArea();
      break;
    case Boolean:
      value = new CheckBox();
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
    case Money:
    case Power:
    case Real:
    case Mass:
    case Temperature:
    case Time:
    case Velocity:
    case Voltage:
    case Volume:
    default:
      value = new TextBox();
    }
    wrapper.setWidget(value);
    return value;
  }
}
