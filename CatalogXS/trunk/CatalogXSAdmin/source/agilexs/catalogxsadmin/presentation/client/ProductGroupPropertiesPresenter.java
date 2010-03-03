package agilexs.catalogxsadmin.presentation.client;

import java.util.List;

import agilexs.catalogxsadmin.presentation.client.catalog.Property;
import agilexs.catalogxsadmin.presentation.client.catalog.PropertyBinding;
import agilexs.catalogxsadmin.presentation.client.page.Presenter;

public class ProductGroupPropertiesPresenter implements Presenter<ProductGroupPropertiesView> {

  private final ProductGroupPropertiesView view = new ProductGroupPropertiesView();

  public ProductGroupPropertiesPresenter() {
  }

  @Override
  public ProductGroupPropertiesView getView() {
    return view;
  }
  
  
  public void show(String productGroupName, List<Property> properties) {
    view.setName(productGroupName);
    
    for (int i = 0; i < properties.size(); i++) {
      final PropertyBinding pb = view.addRow(i);
    }
  }
}
