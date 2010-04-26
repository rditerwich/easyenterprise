package agilexs.catalogxsadmin.presentation.client.util;

import java.util.Collection;
import java.util.HashMap;

import agilexs.catalogxsadmin.presentation.client.binding.Binding;

public abstract class BindingTuple<D, B extends Binding> {

  private final HashMap<String, D> langMap = new HashMap<String, D>(3);
  private final B defaultBinding = newBinding();
  private final B binding = newBinding();

  public B getBinding() {
    return binding;
  }

  public B getDefaultBinding() {
    return defaultBinding;
  }

  public Collection<D> values() {
    return langMap.values();
  }

  public void refresh() {
    if (langMap.get("") != null) {
      getDefaultBinding().setData(langMap.get(""));
    }
    getBinding().setData(langMap.get(getCurrentLanguage()));
  }

  public void setValue(D pv, String language) {
    if (language == null) {
      defaultBinding.setData(pv);
      langMap.put("", pv);
    } else {
      binding.setData(pv);
      langMap.put(language, pv);
    }
  }

  protected abstract String getCurrentLanguage();
  
  protected abstract B newBinding();
}
