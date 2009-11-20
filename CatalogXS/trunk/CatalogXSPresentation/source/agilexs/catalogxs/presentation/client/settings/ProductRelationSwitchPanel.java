package agilexs.catalogxs.presentation.client.settings;

import agilexs.catalogxs.presentation.client.catalog.ProductRelation;
import agilexs.catalogxs.presentation.client.resources.CatalogResources;
import eu.easyenterprise.gwt.framework.client.panels.toggleswitch.SwitchPanel;

public class ProductRelationSwitchPanel extends SwitchPanel<ProductRelation> {

	public ProductRelationSwitchPanel() {
		super(CatalogResources.IMAGES_24.connect_24(), CatalogResources.STATIC_TEXTS.settingsRelationShipsTitle());
		super.setEditPanel(new ProductRelationEdit());
		super.setViewPanel(new ProductRelationView());
	}

	@Override
	public boolean mayCreate() {
		return true;
	}

	@Override
	public boolean mayDelete() {
		return true;
	}

	@Override
	public boolean mayEdit() {
		return true;
	}

}
