package agilexs.catalogxs.presentation.client.settings;

import java.util.ArrayList;
import java.util.List;

import agilexs.catalogxs.presentation.client.resources.CatalogResources;
import agilexs.catalogxs.presentation.client.settings.RelationTypesPage.RelationTypesPageNavigator;
import eu.easyenterprise.gwt.framework.client.navi.ButtonNavigator;
import eu.easyenterprise.gwt.framework.client.navi.Navigable;
import eu.easyenterprise.gwt.framework.client.navi.NavigationData;
import eu.easyenterprise.gwt.framework.client.page.ButtonNavigatorPage;
import eu.easyenterprise.gwt.framework.client.ui.LabeledPushButton;

public class SettingsPage extends ButtonNavigatorPage {

	public SettingsPage() {
		super(CatalogResources.IMAGES_24.product_24().createImage(), CatalogResources.STATIC_TEXTS.settingsTitle());

	}

	public static class SettingsPageNavigator extends ButtonNavigator {

		public final static String TOKEN = "settings";

		public SettingsPageNavigator() {
			super(TOKEN, CatalogResources.STATIC_TEXTS.settingsTitle(), CatalogResources.STATIC_TEXTS.settingsTitle(), CatalogResources.IMAGES_24.gear_24());

		}

		@Override
		protected void addChildren() {
			add(new RelationTypesPageNavigator());
		}

		@Override
		protected Navigable createNavigable() {
			return new SettingsPage();
		}
	}

	private List<LabeledPushButton> result = null;

	private NavigationData parent = null;

	public void onShow(NavigationData newParent) {
		parent = newParent;
		if (result == null) {
			result = new ArrayList<LabeledPushButton>();
			result.add(super.createButton(parent, RelationTypesPageNavigator.TOKEN, CatalogResources.STATIC_TEXTS.settingsRelationShipsTitle(), CatalogResources.STATIC_TEXTS.settingsRelationShipsTitle(), CatalogResources.IMAGES_48.connect_48()));
		}
		super.setButtons(result);
	}

}
