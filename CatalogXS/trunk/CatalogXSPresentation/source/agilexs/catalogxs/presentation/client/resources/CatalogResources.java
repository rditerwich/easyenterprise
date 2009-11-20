package agilexs.catalogxs.presentation.client.resources;

import agilexs.catalogxs.presentation.client.resources.png.x24.Images24;
import agilexs.catalogxs.presentation.client.resources.png.x48.Images48;

import com.google.gwt.core.client.GWT;

public class CatalogResources {

	public static final Images24 IMAGES_24 = (Images24) GWT.create(Images24.class);

	public static final Images48 IMAGES_48 = (Images48) GWT.create(Images48.class);

	public static final GeneralText STATIC_TEXTS = (GeneralText) GWT.create(GeneralText.class);

}
