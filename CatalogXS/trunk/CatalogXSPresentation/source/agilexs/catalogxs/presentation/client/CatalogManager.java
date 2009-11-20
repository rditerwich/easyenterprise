package agilexs.catalogxs.presentation.client;

import agilexs.catalogxs.presentation.client.RootPage.RootPageNavigator;
import agilexs.catalogxs.presentation.client.resources.CatalogResources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;

import eu.easyenterprise.gwt.framework.client.core.Application;
import eu.easyenterprise.gwt.framework.client.core.PagesPanel;
import eu.easyenterprise.gwt.framework.client.core.Startup;
import eu.easyenterprise.gwt.framework.client.core.StartupApplication;
import eu.easyenterprise.gwt.framework.client.navi.NavigationEvent;
import eu.easyenterprise.gwt.framework.client.navi.NavigationEventHandler;
import eu.easyenterprise.gwt.framework.client.navi.NavigationManager;
import eu.easyenterprise.gwt.framework.client.navi.TrailPanel;

public class CatalogManager implements com.google.gwt.core.client.EntryPoint, Startup, ResizeHandler, NavigationEventHandler {

	public void onModuleLoad() {
		StartupApplication.instance().start(this, CatalogResources.STATIC_TEXTS.loading());
	}

	public void init() {
		Application.getTopUserPanel().setUserName("User");
		Application.getTopUserPanel().addUrl(GWT.getHostPageBaseURL() + "../info/Logout.jsp", CatalogResources.STATIC_TEXTS.logout());
		final Image logo = CatalogResources.IMAGES_48.product_48().createImage();
		logo.setTitle("Catalog Manager");
		Application.getTopPanel().setLogo(logo);
		HorizontalPanel middle = new HorizontalPanel();
		TrailPanel newTrail = new TrailPanel();
		middle.add(newTrail);
		// middle.add(crecheInfo);
		middle.setWidth("100%");
		// middle.setCellHorizontalAlignment(crecheInfo, HorizontalPanel.ALIGN_RIGHT);

		Application.getTopPanel().setWidget(middle);
		FeedbackErrorHandler.init();
		Window.addResizeHandler(this);
		NavigationManager.addNavigationEventHandler(this);
		setInitReady();
	}

	public void prepare() {
		// Application.setContentPanel(ContentPanel.instance());
	}

	public void setInitReady() {
		final RootPageNavigator root = new RootPageNavigator();
		NavigationManager.registerAsStart(root);
		History.fireCurrentHistoryState();
		DeferredCommand.addCommand(new Command() {
			public void execute() {
				onResize(null);
			}
		});
	}

	private int width = -1;

	private int height = -1;

	public void onResize(ResizeEvent resizeEvent) {
		if (resizeEvent != null) {
			width = resizeEvent.getWidth();
			height = resizeEvent.getHeight() - PagesPanel.instance().getAbsoluteTop();
		} else {
			width = Window.getClientWidth();
			height = Window.getClientHeight() - PagesPanel.instance().getAbsoluteTop();
		}
		PagesPanel.instance().getActiveWidget().setPixelSize(width - 42, height - 35);
	}

	public void onNavigation(NavigationEvent naviEvent) {
		PagesPanel.instance().getActiveWidget().setPixelSize(width - 42, height - 35);
	}

}
