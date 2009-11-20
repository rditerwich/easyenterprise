package agilexs.catalogxs.presentation.client.settings;

import agilexs.catalogxs.presentation.client.catalog.ProductRelation;
import agilexs.catalogxs.presentation.client.resources.CatalogResources;
import agilexs.catalogxs.presentation.client.services.DomainServiceAsync;
import eu.easyenterprise.gwt.framework.client.callback.SearchForTableASyncCallBack;
import eu.easyenterprise.gwt.framework.client.navi.ButtonNavigator;
import eu.easyenterprise.gwt.framework.client.navi.Navigable;
import eu.easyenterprise.gwt.framework.client.navi.NavigationData;
import eu.easyenterprise.gwt.framework.client.page.TwoColumnPage;
import eu.easyenterprise.gwt.framework.client.panels.toggleswitch.MasterDetailLinker;
import eu.easyenterprise.gwt.framework.client.table.BasicTableList;

public class RelationTypesPage extends TwoColumnPage {

	private BasicTableList<ProductRelation> table = new BasicTableList<ProductRelation>(new ProductRelationRenderer());

	private ProductRelationSwitchPanel groupEdit = new ProductRelationSwitchPanel();

	public RelationTypesPage() {
		super();
		setLeftBox(table);
		setRightPanel(groupEdit);
		new MasterDetailLinker<ProductRelation>(table, groupEdit);
	}

	@Override
	public void onShow(NavigationData data) {
		DomainServiceAsync.getProductRelations(new SearchForTableASyncCallBack<ProductRelation>(table));
	}

	public static class RelationTypesPageNavigator extends ButtonNavigator {

		public final static String TOKEN = "relationstype";

		public RelationTypesPageNavigator() {
			super(TOKEN, CatalogResources.STATIC_TEXTS.settingsRelationShipsTitle(), CatalogResources.STATIC_TEXTS.settingsRelationShipsTitle(), CatalogResources.IMAGES_24.connect_24());

		}

		@Override
		protected void addChildren() {
			// add(new OrganizationDetailsPageNavigator());
			// add(new PersonDetailsPageNavigator());
		}

		@Override
		protected Navigable createNavigable() {
			return new RelationTypesPage();
		}
	}
}
