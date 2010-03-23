package agilexs.catalogxsadmin.presentation.client;

import agilexs.catalogxsadmin.presentation.client.page.Presenter;

public class SettingsPresenter implements Presenter<SettingsView> {

  private SettingsView view = new SettingsView();
  
  public SettingsPresenter() {
    
  }

  @Override
  public SettingsView getView() {
    return view;
  }

  public void show() {
  }
}
