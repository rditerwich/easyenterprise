package easyenterprise.gwtplayground.client;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SourcesClickEvents;
import com.google.gwt.user.client.ui.Widget;

public class MenuRenderer {

	private static final int DIMMED_OPACITY = 30;
	private static final int FADE_INCR = 5;
	private static final int FADE_DELAY = 10;
	private static final int DELAY = 2000;
	private static final int COLLAPSE_TIME = 500;
	private static final int COLLAPSE_DELAY = 15;
	
	protected Map<Widget, Timer> timers;
	
	public Panel createMenuPanel(SubMenu submenu) {
		HorizontalPanel panel = new HorizontalPanel();
		panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		panel.add(new Image("arrow-right.gif"));
		return panel;
	}


	public Widget createItemWidget(final MenuItem menuItem, Widget widget) {
		if (!(widget instanceof SourcesClickEvents)) {
			FocusPanel focusPanel = new FocusPanel(widget);
			focusPanel.setTitle("This is a focus panel");
			DOM.setStyleAttribute(focusPanel.getElement(), "cursor", "pointer");
			widget = focusPanel;
		}
		((SourcesClickEvents) widget).addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				menuItem.select();
			}
		});
		widget.setWidth("100%");
		widget.setHeight("100%");
		DOM.setStyleAttribute(widget.getElement(), "overflow", "hidden");
		return widget;
	}

	public void displayed(final SubMenu subMenu) {
		
		removeTimer(subMenu.menuPanel);
		for (MenuItem item : subMenu.children) {
			setOpacity(item.widget, 100);
			item.widget.setWidth("100%");
		}
	}
	
	public void selected(final SubMenu subMenu, final MenuItem selectedItem) {
		
		// stop existing animations
		removeTimer(subMenu.menuPanel);
		resetTimers();
		
		// set initial opacity
		for (MenuItem item : subMenu.children) {
			if (item == selectedItem) {
				setOpacity(item.widget, 100);
			} else {
				setOpacity(item.widget, DIMMED_OPACITY);
			}
		}
		
		// make sure selected item is fully visible
		selectedItem.widget.setWidth("100%");
		
		if (!subMenu.isCollapsable()) {
			return;
		}
		
		// create timer for initial delay
		addTimer(subMenu.menuPanel, DELAY, false, new DelayTimer() {
			public void run() {
				
				// create time for fading effect
				addTimer(subMenu.menuPanel, FADE_DELAY, true, new Timer() {
					int opacity = DIMMED_OPACITY;
					public void run() {
						opacity -= FADE_INCR;
						for (MenuItem item : subMenu.children) {
							if (item != selectedItem) {
								setOpacity(item.widget, opacity);
							}
						}
						if (opacity <= 0) {
							
							// calculate collapse increments
							final int iterations = COLLAPSE_TIME / COLLAPSE_DELAY;
							final int[] widths = new int[subMenu.children.size()];
							final int[] incr = new int[subMenu.children.size()];
							int index = 0;
							for (MenuItem item : subMenu.children) {
								widths[index] = item.widget.getOffsetWidth() * 100;
								incr[index] = Math.max(1, widths[index] / iterations); 
								index++;
							}
							
							// create timer for collapsing
							addTimer(subMenu.menuPanel, COLLAPSE_DELAY, true, new Timer() {
								int count = iterations;
								public void run() {
									int index = 0;
									for (MenuItem item : subMenu.children) {
										if (item != selectedItem) {
											widths[index] -= incr[index];
											if (count == 0) {
												item.widget.setWidth("0");
											} else {
												item.widget.setWidth("" + (int) Math.max(0, widths[index] / 100));
											}
										}
										index++;
									}
									if (count-- == 0) {
										removeTimer(subMenu.menuPanel);
									}
								}
							});
						}
					}
				});
			}
		});
	}
	
	protected void setOpacity(Widget widget, int opacity) {
		if (opacity < 0) {
			opacity = 0;
		}
		DOM.setStyleAttribute(widget.getElement(), "opacity", "" + (opacity / 100.0));
		DOM.setStyleAttribute(widget.getElement(), "-moz-opacity", " + (opacity / 100.0) + ");
		DOM.setStyleAttribute(widget.getElement(), "filter", "alpha(opacity=" + opacity + ")");
	}
	
	protected void setWidth(Widget widget, int width) {
		widget.setWidth("" + Math.max(0, width));
	}
	
	protected void addTimer(Widget widget, int interval, boolean repeating, Timer timer) {
		if (timers == null) {
			timers = new HashMap<Widget, Timer>();
		}
		Timer oldTimer = timers.put(widget, timer);
		if (oldTimer != null) {
			oldTimer.cancel();
		}
		if (repeating) {
			timer.scheduleRepeating(interval);
		} else {
			timer.schedule(interval);
		}			
	}
	
	protected void resetTimers() {
		if (timers != null) {
			for (Timer timer : timers.values()) {
				if (timer instanceof DelayTimer) {
					timer.schedule(DELAY);
				}
			}
		}	
	}
	
	protected void removeTimer(Widget widget) {
		if (timers != null) {
			Timer timer = timers.remove(widget);
			if (timer != null) {
				timer.cancel();
			}
		}
	}
	
	private static abstract class DelayTimer extends Timer {
	}
}
