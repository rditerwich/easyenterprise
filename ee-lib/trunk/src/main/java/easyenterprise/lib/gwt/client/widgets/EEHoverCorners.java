package easyenterprise.lib.gwt.client.widgets;

import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;

import easyenterprise.lib.gwt.client.Style;

public class EEHoverCorners extends FlowPanel {
	
	public static enum Styles implements Style { eeHoverCorners, eeHoverCorner, left, right }

	private FlowPanel left;
	private FlowPanel right;
	protected double height;
	
	public EEHoverCorners() {
		addStyleName(Styles.eeHoverCorners.toString());
		getElement().getStyle().setPosition(Position.RELATIVE);
		add(left = new FlowPanel() {{
			addStyleName(Styles.eeHoverCorner.toString());
			addStyleName(Styles.left.toString());
			getElement().getStyle().setPosition(Position.ABSOLUTE);
			getElement().getStyle().setLeft(0, Unit.PX);
			getElement().getStyle().setWidth(20, Unit.PCT);
			getElement().getStyle().setHeight(height, Unit.PX);
			
		}});
		add(right = new FlowPanel() {{
			addStyleName(Styles.eeHoverCorner.toString());
			addStyleName(Styles.right.toString());
			getElement().getStyle().setPosition(Position.ABSOLUTE);
			getElement().getStyle().setRight(0, Unit.PX);
			getElement().getStyle().setWidth(20, Unit.PCT);
			getElement().getStyle().setHeight(height, Unit.PX);
			getElement().getStyle().setProperty("textAlign", "right");
			add(right = new FlowPanel());
		}});
	}
	
	public FlowPanel getLeft() {
		return left;
	}
	
	public FlowPanel getRight() {
		return right;
	}
}
