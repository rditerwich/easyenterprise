package easyenterprise.lib.gwt.client.widgets;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Panel;

import easyenterprise.lib.gwt.client.Style;

/**
 * Button with repeatable background image, and optionally 2 images on left and right sides.
 */
public class EEButton extends Button {

	enum StylesEnum implements Style { eeButton, eeLeft, eeMiddle, eeRight } 

	protected Panel leftPanel;
	protected Panel middlePanel;
	protected Panel rightPanel;
	protected boolean selected;
	protected boolean hovering;
	private final String styleName;

	public EEButton(String text) {
		this(text, StylesEnum.eeButton.toString());
	}

	public EEButton(final String text, final String styleName) {
		super("<div><span><span>" + text + "</span></span></div>");
		this.styleName = styleName;
		addDomHandler(new MouseOverHandler() {
			public void onMouseOver(MouseOverEvent event) {
				hovering = true;
				setStyleName();
			}
		}, MouseOverEvent.getType());
		addDomHandler(new MouseOutHandler() {
			public void onMouseOut(MouseOutEvent event) {
				hovering = false;
				setStyleName();
			}
		}, MouseOutEvent.getType());
		setStyleName();
	}
	
	public boolean getSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		if (this.selected != selected) {
			this.selected = selected;
			setStyleName();
		}
	}
	
	protected void setStyleName() {
		String s = styleName;
//		if (selected) s += "-Selected";
//		if (hovering) s += "-Hover";
		setStylePrimaryName(s);
	}

	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return addDomHandler(handler, ClickEvent.getType());
	}
}
