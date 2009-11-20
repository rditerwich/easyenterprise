package agilexs.catalogxs.presentation.client.settings;

import java.util.ArrayList;
import java.util.List;

import agilexs.catalogxs.presentation.client.catalog.ProductRelation;
import agilexs.catalogxs.presentation.client.resources.CatalogResources;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;

import eu.easyenterprise.gwt.framework.client.navi.NavigationData;
import eu.easyenterprise.gwt.framework.client.table.BasicTableListRenderer;
import eu.easyenterprise.gwt.framework.client.table.SortableLabel;

public class ProductRelationRenderer implements BasicTableListRenderer<ProductRelation> {

	@Override
	public int compare(ProductRelation newRow, ProductRelation oldRow, int column) {
		return 0;
	}

	@Override
	public NavigationData createNavigationData(ProductRelation data) {
		return null;
	}

	@Override
	public List<Widget> createRow(ProductRelation newRow, boolean selected) {
		final ArrayList<Widget> result = new ArrayList<Widget>();
		result.add(new Label(newRow.getName()));
		return result;
	}

	@Override
	public int getColumnCount() {
		return 0;
	}

	@Override
	public HorizontalAlignmentConstant getColumnHorizontalAlignment(int column) {
		return null;
	}

	@Override
	public String getColumnWidth(int column) {
		return null;
	}

	@Override
	public List<Widget> getHeaders() {
		final ArrayList<Widget> result = new ArrayList<Widget>();
		result.add(new SortableLabel(CatalogResources.STATIC_TEXTS.settingsRelationShipsName()));
		return result;
	}

	@Override
	public String getId(ProductRelation newRow) {
		return null;
	}

}
