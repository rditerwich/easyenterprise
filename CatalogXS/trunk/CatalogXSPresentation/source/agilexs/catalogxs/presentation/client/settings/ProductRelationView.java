package agilexs.catalogxs.presentation.client.settings;

import agilexs.catalogxs.presentation.client.catalog.ProductRelation;
import agilexs.catalogxs.presentation.client.resources.CatalogResources;

import com.google.gwt.user.client.ui.Label;

import eu.easyenterprise.gwt.framework.client.panels.InfoPanel;
import eu.easyenterprise.gwt.framework.client.panels.toggleswitch.BasicViewPanel;

public class ProductRelationView extends BasicViewPanel<ProductRelation> {

	private InfoPanel editFromPanel = new InfoPanel();

	private Label namelabel = new Label();

	public ProductRelationView() {
		super();
		initWidget(editFromPanel);
		editFromPanel.addRow(CatalogResources.STATIC_TEXTS.settingsRelationShipsName(), namelabel);
	}

	@Override
	public void setViewState() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setData(ProductRelation newData) {

	}

}
