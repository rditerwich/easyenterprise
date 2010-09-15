package agilexs.catalogxsadmin.presentation.client.page;

import com.google.gwt.user.client.ui.Widget;

public interface Presenter<T extends Widget & View> {
 T getView();
}
