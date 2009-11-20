package agilexs.catalogxs.presentation.client.settings;

import agilexs.catalogxs.presentation.client.binding.HasTextBinding;
import agilexs.catalogxs.presentation.client.catalog.ProductRelation;
import agilexs.catalogxs.presentation.client.catalog.ProductRelationBinding;
import agilexs.catalogxs.presentation.client.resources.CatalogResources;
import agilexs.catalogxs.presentation.client.services.CatalogServiceAsync;

import com.google.gwt.user.client.ui.TextBox;

import eu.easyenterprise.gwt.framework.client.panels.InfoPanel;
import eu.easyenterprise.gwt.framework.client.panels.toggleswitch.AsyncCallbackAddForEdit;
import eu.easyenterprise.gwt.framework.client.panels.toggleswitch.BasicEditPanel;

public class ProductRelationEdit extends BasicEditPanel<ProductRelation> {

	private InfoPanel editFromPanel = new InfoPanel();

	private TextBox namelabel = new TextBox();

	public ProductRelationEdit() {
		super();
		initWidget(editFromPanel);
		editFromPanel.addRow(CatalogResources.STATIC_TEXTS.settingsRelationShipsName(), namelabel);
		HasTextBinding.bind(namelabel, binding.name());
	}

	@Override
	public void create() {
		CatalogServiceAsync.updateProductRelation(null, getData(), new AsyncCallbackAddForEdit<ProductRelation>(this));
	}

	@Override
	public void deleteCurrent() {
		CatalogServiceAsync.updateProductRelation(oldProductRelation, null, new AsyncCallbackAddForEdit<ProductRelation>(this));
	}

	@Override
	public ProductRelation getData() {
		return binding.getData();
	}

	@Override
	public boolean isValid() {
		return true;
	}

	private ProductRelation oldProductRelation = null;

	private ProductRelationBinding<ProductRelation> binding = new ProductRelationBinding<ProductRelation>();

	@Override
	public void update() {
		CatalogServiceAsync.updateProductRelation(oldProductRelation, getData(), new AsyncCallbackAddForEdit<ProductRelation>(this));
	}

	@Override
	public void setData(ProductRelation newData) {
		if (newData != null) {
			oldProductRelation = newData.clone(null);
		} else {
			oldProductRelation = null;
		}
		binding.setData(newData);

	}

}
