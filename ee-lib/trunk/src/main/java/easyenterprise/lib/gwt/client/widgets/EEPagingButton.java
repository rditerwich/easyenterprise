package easyenterprise.lib.gwt.client.widgets;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.CustomButton;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

import easyenterprise.lib.gwt.client.PagedData;

public interface EEPagingButton {

	public class Previous extends Composite implements EEPagingButton, PagedData.Listener {
		private final PagedData<?, ?> data;
		public Previous(PagedData<?, ?> data_, Widget widget) {
			this.data = data_;
			initWidget(widget);
			widget.addDomHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					data.previousPage();
				}
			}, ClickEvent.getType());
		}
		public void dataChanged() {
			if (getWidget() instanceof CustomButton) {
				((CustomButton) getWidget()).setEnabled(!data.isFirstPage());
			}
		}
	}
	
	public class Next extends Composite implements EEPagingButton, PagedData.Listener {
		private final PagedData<?, ?> data;
		public Next(PagedData<?, ?> data_, Widget widget) {
			this.data = data_;
			initWidget(widget);
			widget.addDomHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					data.nextPage();
				}
			}, ClickEvent.getType());
		}
		public void dataChanged() {
			if (getWidget() instanceof CustomButton) {
				((CustomButton) getWidget()).setEnabled(!data.isLastPage());
			}
		}
	}
	
	public class PreviousNext extends HorizontalPanel {
		public PreviousNext(PagedData<?, ?> data, Widget previous, Widget next) {
			add(new Previous(data, previous));
			add(new Next(data, next));
		}
	}
}
