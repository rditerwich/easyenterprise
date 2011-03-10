package easyenterprise.lib.gwt.client.widgets;

import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

import easyenterprise.lib.gwt.client.Style;

public class EEHoverCorners extends FlowPanel {
	
	public static enum Styles implements Style { eeHoverCorners, eeHoverCorner, left, right }

	private FlowPanel leftParent;
	private Widget left;
	private FlowPanel rightParent;
	private Widget right;
	
	public EEHoverCorners() {
		this(20);
		
	}
	public EEHoverCorners(final int height) {
		addStyleName(Styles.eeHoverCorners.toString());
		getElement().getStyle().setPosition(Position.RELATIVE);
		add(leftParent = new FlowPanel() {{
			addStyleName(Styles.eeHoverCorner.toString());
			addStyleName(Styles.left.toString());
			getElement().getStyle().setPosition(Position.ABSOLUTE);
			getElement().getStyle().setLeft(0, Unit.PX);
			getElement().getStyle().setWidth(20, Unit.PCT);
			getElement().getStyle().setHeight(height, Unit.PX);
//			getElement().getStyle().setBackgroundColor("yellow");
			add(left = new FlowPanel());
			addDomHandler(new MouseMoveHandler() {
				public void onMouseMove(MouseMoveEvent event) {
					left.setVisible(true);
				}
			}, MouseMoveEvent.getType());
			addDomHandler(new MouseOutHandler() {
				public void onMouseOut(MouseOutEvent event) {
					left.setVisible(false);
				}
			}, MouseOutEvent.getType());
		}});
		add(rightParent = new FlowPanel() {{
			addStyleName(Styles.eeHoverCorner.toString());
			addStyleName(Styles.right.toString());
			getElement().getStyle().setPosition(Position.ABSOLUTE);
			getElement().getStyle().setRight(0, Unit.PX);
			getElement().getStyle().setWidth(20, Unit.PCT);
			getElement().getStyle().setHeight(height, Unit.PX);
			getElement().getStyle().setProperty("textAlign", "right");
//			getElement().getStyle().setBackgroundColor("yellow");
			add(right = new FlowPanel());
			addDomHandler(new MouseMoveHandler() {
				public void onMouseMove(MouseMoveEvent event) {
					right.setVisible(true);
				}
			}, MouseMoveEvent.getType());
			addDomHandler(new MouseOutHandler() {
				public void onMouseOut(MouseOutEvent event) {
					right.setVisible(false);
				}
			}, MouseOutEvent.getType());
		}});
	}
	
	public Widget getLeft() {
		return left;
	}
	
	public void setLeft(Widget left) {
		if (this.left != null) {
			this.left.removeFromParent();
		}
		this.left = left;
		if (left != null) {
			left.addDomHandler(mouseMoveHander, MouseMoveEvent.getType());
			left.addDomHandler(mouseOutHander, MouseOutEvent.getType());
			left.setVisible(false);
			leftParent.add(left);
		}
	}
	
	public Widget getRight() {
		return right;
	}
	
	public void setRight(Widget right) {
		if (this.right != null) {
			this.right.removeFromParent();
		}
		this.right = right;
		if (right != null) {
			right.addDomHandler(mouseMoveHander, MouseMoveEvent.getType());
			right.addDomHandler(mouseOutHander, MouseOutEvent.getType());
			rightParent.add(right);
			right.setVisible(false);
		}
	}

	private MouseMoveHandler mouseMoveHander = new MouseMoveHandler() {
		public void onMouseMove(MouseMoveEvent event) {
			if (event.getSource() == left) {
				left.setVisible(true);
			}
			if (event.getSource() == right) {
				right.setVisible(true);
			}
		}
	};
	
	private MouseOutHandler mouseOutHander = new MouseOutHandler() {
		public void onMouseOut(MouseOutEvent event) {
			if (event.getSource() == left) {
				left.setVisible(false);
			}
			if (event.getSource() == right) {
				right.setVisible(false);
			}
		}
	};
}
