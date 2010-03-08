package agilexs.catalogxsadmin.presentation.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface ResourceBundle extends ClientBundle {
  @Source("CatalogXS.png")
  ImageResource logo();

  @Source("bin_closed.png")
  ImageResource deleteImage();
}
