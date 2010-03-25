package agilexs.catalogxsadmin.presentation.client.binding;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HasValue;

public class HasValueBinding<D, W> extends WidgetBinding<D, W> implements ValueChangeHandler<W> {

  public static HasValueBinding bind(HasValue<String> hv, PropertyBinding<String> binding) {
      return bind(hv, binding, null);
  }

  public static <D, W> HasValueBinding bind(HasValue<W> hv, PropertyBinding<D> binding, BindingConverter<D, W> converter) {
      return new HasValueBinding<D,W>(hv, binding, converter).bind();
  }

  private HasValue<W> hasValue;

  public HasValueBinding(HasValue<W> hv, PropertyBinding<D> binding, BindingConverter<D, W> converter) {
      super(binding, converter);
      hasValue = hv;
  }

  @Override
  public void onValueChange(ValueChangeEvent<W> event) {
    dataChanged(this, getWidgetData());
  }

  @Override
  protected HasValueBinding<D, W> bind() {
      super.bind();
      hasValue.addValueChangeHandler(this);
      return this;
  }

  @Override
  protected W getWidgetData() {
      return hasValue.getValue();
  }

  @Override
  protected void setWidgetData(W data) {
      hasValue.setValue(data);
  }
}