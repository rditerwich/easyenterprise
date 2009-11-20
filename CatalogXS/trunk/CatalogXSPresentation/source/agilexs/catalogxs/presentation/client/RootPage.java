package agilexs.catalogxs.presentation.client;

import java.util.ArrayList;
import java.util.List;

import agilexs.catalogxs.presentation.client.resources.CatalogResources;
import agilexs.catalogxs.presentation.client.settings.SettingsPage.SettingsPageNavigator;
import eu.easyenterprise.gwt.framework.client.navi.ButtonNavigator;
import eu.easyenterprise.gwt.framework.client.navi.Navigable;
import eu.easyenterprise.gwt.framework.client.navi.NavigationData;
import eu.easyenterprise.gwt.framework.client.page.ButtonNavigatorPage;
import eu.easyenterprise.gwt.framework.client.ui.LabeledPushButton;

public class RootPage extends ButtonNavigatorPage {

	public RootPage() {
		super(CatalogResources.IMAGES_24.product_24().createImage(), CatalogResources.STATIC_TEXTS.settingsTitle());
	}

	public final static String TOKEN = "settings";

	public static class RootPageNavigator extends ButtonNavigator {

		public RootPageNavigator() {
			super(TOKEN, CatalogResources.STATIC_TEXTS.startScreenSmall(), CatalogResources.STATIC_TEXTS.startScreenLarge(), CatalogResources.IMAGES_24.product_24());

		}

		@Override
		protected void addChildren() {
			add(new SettingsPageNavigator());
		}

		@Override
		protected Navigable createNavigable() {
			return new RootPage();
		}
	}

	private List<LabeledPushButton> result = null;

	private NavigationData parent = null;

	public void onShow(NavigationData newParent) {
		parent = newParent;
		if (result == null) {
			result = new ArrayList<LabeledPushButton>();
			result.add(super.createButton(parent, SettingsPageNavigator.TOKEN, CatalogResources.STATIC_TEXTS.settingsTitle(), CatalogResources.STATIC_TEXTS.settingsTitle(), CatalogResources.IMAGES_48.gear_48()));
		}
		super.setButtons(result);
	}

}
