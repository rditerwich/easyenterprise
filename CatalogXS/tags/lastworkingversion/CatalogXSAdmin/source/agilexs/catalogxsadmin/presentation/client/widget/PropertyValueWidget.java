package agilexs.catalogxsadmin.presentation.client.widget;

import agilexs.catalogxsadmin.presentation.client.Util;
import agilexs.catalogxsadmin.presentation.client.catalog.PropertyValue;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;

public class PropertyValueWidget extends Composite {

  public PropertyValueWidget(PropertyValue pv) {
    Widget value = null;
    switch (pv.getProperty().getType()) {
    case Enum:
      value = new InlineLabel();
      break;
    case FormattedText:
      value = new InlineLabel();
      break;
    case Media:
      value = new MediaWidget(false);
      ((MediaWidget) value).show(Util.stringValueOf(pv.getId()), pv.getMimeType(), pv.getStringValue());
      break;
    case String:
      value = new InlineLabel();
      ((InlineLabel) value).setText(Util.stringValueOf(pv.getStringValue()));
      break;
    case Boolean:
      value = new CheckBox();
      ((CheckBox)value).setEnabled(false);
      ((CheckBox)value).setValue(pv.getBooleanValue());
      break;
    case Money:
      value = new HTML();
      ((HTML) value).setHTML(Util.formatMoney(pv.getMoneyValue()));
      break;
    case Real:
      value = new InlineLabel();
      ((InlineLabel) value).setText(Util.stringValueOf(pv.getRealValue()));
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
    case Mass:
    case Temperature:
    case Time:
    case Velocity:
    case Voltage:
    case Volume:
    default:
      value = new InlineLabel();
      ((InlineLabel) value).setText(Util.stringValueOf(pv.getIntegerValue()));
    }   
    initWidget(value);
  }
}
