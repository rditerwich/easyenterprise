package easyenterprise.lib.gwt.client.widgets;

import static easyenterprise.lib.gwt.client.StyleUtil.addStyle;
import static easyenterprise.lib.gwt.client.StyleUtil.setStyle;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.layout.client.Layout.AnimationCallback;
import com.google.gwt.layout.client.Layout.Layer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

import easyenterprise.lib.gwt.client.Style;

public abstract class MasterDetail extends Composite implements RequiresResize, ProvidesResize {
	private static final int BLUR_SPREAD = 3;
	private static int SHADOWSIZE = 2*BLUR_SPREAD;

	public enum Styles implements Style {
		MasterDetail, 
		MasterDetailHeaderContainer, MasterDetailHeaderWrapper, MasterDetailHeader, 
		MasterDetailFooterContainer, MasterDetailFooterWrapper, MasterDetailFooter, 
		MasterDetailMasterContainer, MasterDetailMasterWrapper, MasterDetailMaster, MasterDetailTable, 
		MasterDetailDetailContainer, MasterDetailDetailWrapper, MasterDetailDetail,
		MasterDetailSelection, MasterDetailSelectionPanel, MasterDetailSelectionPanelContent,  MasterDetailFillPanel, MasterDetailErasePanel;
	
		public String toString() {
			return "ee-" + super.toString();
		};
	}
	
	private final LayoutPanel mainPanel;
	private DockLayoutPanel dockPanel;
	private LayoutPanel headerContainer;
	private HasWidgets headerParent;
	private Widget header;
	
	private LayoutPanel footerContainer;
	private HasWidgets footerParent;
	private Widget footer;
	
	private LayoutPanel masterContainer;
	private HasWidgets masterParent;
	private Widget master;
	private HTMLTable masterTable;
	private int currentRow = -1;
	
	private LayoutPanel detailContainer;
	private HasWidgets detailParent;
	private Widget detail;
	private boolean detailOpen;

	private LayoutPanel erasePanel;
	private LayoutPanel selectionLine;
	
	private ValueChangeHandler<Integer> rowChangedHandler;;
	private ClickHandler tableClickHandler = new ClickHandler() {
		public void onClick(ClickEvent event) {
			Cell cell = masterTable.getCellForEvent(event);
			if (cell != null) {
				openDetail(cell.getRowIndex());
				event.stopPropagation();
				if (rowChangedHandler != null) {
					rowChangedHandler.onValueChange(new ValueChangeEvent<Integer>(currentRow){});
				}
			}
		}
	};

	public MasterDetail(final int headerSize, final int footerSize, final int borderSize) {
		initWidget(mainPanel = new LayoutPanel() {{
			setStyle(this, Styles.MasterDetail);
			
			add(dockPanel = new DockLayoutPanel(Unit.PX) {{
				
				if (headerSize > 0) {
					headerContainer = new LayoutPanel();
					addNorth(headerContainer, headerSize);
					setStyle(headerContainer, Styles.MasterDetailHeaderContainer);
//					setWidgetTopBottom(headerContainer, 0, Unit.PX, 0, Unit.PX);
//					setWidgetLeftRight(headerContainer, 0, Unit.PX, 0, Unit.PX);
					headerParent = createHeaderWrapper(headerContainer);
					if (headerParent == null) {
						headerParent = headerContainer;
					} else {
						setStyle((UIObject) headerParent, Styles.MasterDetailHeaderWrapper);
					}
				}
				if (footerSize > 0) {
					footerContainer = new LayoutPanel();
					addNorth(footerContainer, footerSize);
					setStyle(footerContainer, Styles.MasterDetailFooterContainer);
//					setWidgetTopBottom(footerContainer, 0, Unit.PX, 0, Unit.PX);
//					setWidgetLeftRight(footerContainer, 0, Unit.PX, 0, Unit.PX);
					footerParent = createFooterWrapper(footerContainer);
					if (footerParent == null) {
						footerParent = footerContainer;
					} else {
						setStyle((UIObject) footerParent, Styles.MasterDetailFooterWrapper);
					}
				}
			
				masterContainer = new LayoutPanel();
				add(masterContainer);
				setStyle(masterContainer, Styles.MasterDetailMasterContainer);
//				setWidgetTopBottom(masterContainer, 0, Unit.PX, 0, Unit.PX);
//				setWidgetLeftRight(masterContainer, 0, Unit.PX, 0, Unit.PX);
				masterParent = createMasterWrapper(masterContainer);
				if (masterParent == null) {
					masterParent = masterContainer;
				} else {
					setStyle((UIObject) masterParent, Styles.MasterDetailMasterWrapper);
				}
			}});

			setWidgetTopBottom(dockPanel, borderSize, Unit.PX, 0, Unit.PX);
			setWidgetLeftRight(dockPanel, borderSize, Unit.PX, borderSize, Unit.PX);

			add(selectionLine = new LayoutPanel() {{
				addStyle(this, Styles.MasterDetailSelectionPanel);
				add(new FlowPanel() {{
					addStyle(this, Styles.MasterDetailSelectionPanelContent);
				}});
			}});

			// Make sure the selection panel does not overlap the master
			setWidgetLeftWidth(selectionLine, 100, Unit.PCT, 0, Unit.PX);
			
			// auto-close detail when clicked on background
			addDomHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					closeDetail(true);
				}
			}, ClickEvent.getType());
	
			// detail panel
			detailContainer = new LayoutPanel();
			add(detailContainer);
			setStyle(detailContainer, Styles.MasterDetailDetailContainer);
			setWidgetTopBottom(detailContainer, 0, Unit.PX, 0, Unit.PX);
			setWidgetLeftRight(detailContainer, 0, Unit.PX, 0, Unit.PX);
			detailParent = createDetailWrapper(detailContainer);
			if (detailParent == null) {
				detailParent = detailContainer;
			} else {
				setStyle((UIObject) detailParent, Styles.MasterDetailDetailWrapper);
			}
			setWidgetTopHeight(detailContainer, 0, Unit.PX, 0, Unit.PX);
				
			// make sure clicks on detail panel aren't causing the detail panel to close
			detailContainer.addDomHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					event.stopPropagation();
				}
			}, ClickEvent.getType());

			add(erasePanel = new LayoutPanel() {{
				setStyle(this, Styles.MasterDetailErasePanel);
			}});
	
			// make sure erase panel does not overlap the detail panel
			setWidgetLeftWidth(erasePanel, 100, Unit.PCT, 0, Unit.PX);
		}});
	}

	public Widget getHeader() {
		return header;
	}
	
	public void setHeader(Widget header) {
		if (this.header != null) {
			headerParent.remove(this.header);
		}
		this.header = header;
		if (header != null) {
			headerParent.add(header);
			setStyle(header, Styles.MasterDetailHeader);
		}	
	}
	
	public Widget getFooter() {
		return footer;
	}
	
	public void setFooter(Widget footer) {
		if (this.footer != null) {
			footerParent.remove(this.footer);
		}
		this.footer = footer;
		if (footer != null) {
			footerParent.add(footer);
			setStyle(footer, Styles.MasterDetailFooter);
		}	
	}
	
	@SuppressWarnings("unchecked")
	public <T extends HTMLTable> T getMasterTable() {
		return (T) masterTable;
	}
		
	public Widget getMaster() {
		return master;
	}
	
	public <T extends IsWidget & IsHTMLTable> void setMaster(T master) {
		doSetMaster(master.asWidget(), master.asHTMLTable());
	}

	public <T extends HTMLTable> void setMaster(T masterTable) {
		doSetMaster(masterTable, masterTable);
	}

	private void doSetMaster(Widget mainWidget, HTMLTable masterTable) {
		setMainWidget(mainWidget);
		if (this.masterTable != masterTable) {
			this.masterTable = masterTable;
			if (this.masterTable != null) {
				setStyle(this.masterTable, Styles.MasterDetailTable);
				this.masterTable.addClickHandler(tableClickHandler);
			}
		}
	}
	
	public Widget getMainWidget() {
		return master;
	}
	
	public void setMainWidget(Widget master) {
		if (this.master != master) {
			if (this.master != null) {
				masterParent.remove(this.master);
			}
			this.master = master;
			this.masterTable = null;
			if (master != null) {
				masterParent.add(this.master);
				setStyle(this.master, Styles.MasterDetailMaster);
			}
		}
	}
	
	public Widget getDetail() {
		return detail;
	}
	
	public void setDetail(Widget detail) {
		if (this.detail != detail) {
			if (this.detail != null) {
				detailParent.remove(this.detail);
			}
			this.detail = detail;
			if (detail != null) {
				detailParent.add(detail);
				setStyle(detail, Styles.MasterDetailDetail);
			}
		}
	}

	public boolean isDetailOpen() {
		return detailOpen;
	}

	public int getCurrentRow() {
		return currentRow;
	}
	
	public void setCurrentRow(int row) {
//		if (currentRow != row) {
			if (row < 0 || masterTable != null && row >= masterTable.getRowCount()) {
				closeDetail(false);
			} else if (isDetailOpen()) {
				openDetail(row);
			}
			currentRow = row;
//		}
	}
	
	public void setRowChangedHandler(ValueChangeHandler<Integer> handler) {
		this.rowChangedHandler = handler;
	}

	@Override
	public void onResize() {
		mainPanel.onResize();
//		System.out.println("onResize():");
//		System.out.println("  MainPanel size is " + mainPanel.getOffsetWidth() + "x" + mainPanel.getOffsetHeight());
//		System.out.println("  MainPanel client size is " + mainPanel.getElement().getClientWidth() + "x" + mainPanel.getElement().getClientHeight());
//		if (getMasterTable() != null) {
//			System.out.println("  MasterPanel size is " + getMasterTable().getOffsetWidth() + "x" + getMasterTable().getOffsetHeight());
//			System.out.println("  MasterPanel client size is " + getMasterTable().getElement().getClientWidth() + "x" + getMasterTable().getElement().getClientHeight());
//			
//		}
	}
	
	public void openDetail() {
		if (currentRow >= 0) {
			openDetail(currentRow);
		}
	}
	
	public void openDetail(final int row) {
		
		if (masterTable != null) {
			// Determine size/offset of selected table row.
			final int offsetTop = rowTopOffset(row);
			final int offsetLeft = masterContainer.getAbsoluteLeft();
			final int offsetHeight = rowHeight(row);
			
			// Set selection 
			
			// If the details is not open, position to get a nice starting position
			if (!detailOpen) {
	
				mainPanel.setWidgetTopHeight(detailContainer, offsetTop -1, Unit.PX, offsetHeight + 2, Unit.PX);
				mainPanel.setWidgetLeftWidth(detailContainer, 20, Unit.PCT, 0, Unit.PX);
				
				int detailPanelOffset = mainPanel.getElement().getOffsetWidth() / 5;
				mainPanel.setWidgetTopHeight(selectionLine, offsetTop - 1 - SHADOWSIZE, Unit.PX, offsetHeight + 2+ 2 *SHADOWSIZE, Unit.PX);
				mainPanel.setWidgetLeftWidth(selectionLine, detailPanelOffset, Unit.PX, 0, Unit.PX);
				
	//			mainPanel.setWidgetTopHeight(eraseLine, offsetTop + 2, Unit.PX, offsetHeight - 4, Unit.PX);
	//			mainPanel.setWidgetLeftWidth(eraseLine, 20, Unit.PCT, 3, Unit.PX);
				
				mainPanel.forceLayout();
			}
			detailOpen = true;
	
			// Update selection
//			int w = getDetail().getAbsoluteLeft() - (offsetLeft - 1) + 9;
			mainPanel.setWidgetTopHeight(selectionLine, offsetTop - 1 - SHADOWSIZE, Unit.PX, offsetHeight + 2+ 2 *SHADOWSIZE, Unit.PX);
			mainPanel.setWidgetLeftRight(selectionLine, offsetLeft - 1, Unit.PX, getDetail().getAbsoluteLeft() + 24, Unit.PX);
	
			// erase a bit of the detail panel:
	//		int left = getMasterTable().getAbsoluteLeft() + getMasterTable().getOffsetWidth() - 1;
//			int left = masterContainer.getAbsoluteLeft() + 1;
//			int width = masterTable.getAbsoluteLeft() - left ;
			mainPanel.setWidgetTopHeight(erasePanel, offsetTop + 2, Unit.PX, offsetHeight - 4, Unit.PX);
			mainPanel.setWidgetLeftWidth(erasePanel, 20, Unit.PCT, 3 + SHADOWSIZE, Unit.PX);
			
			mainPanel.setWidgetTopHeight(detailContainer, 0, Unit.PX, 100, Unit.PCT);
			mainPanel.setWidgetLeftWidth(detailContainer, 20, Unit.PCT, 80, Unit.PCT);
			
	
			mainPanel.animate(150, new AnimationCallback() {
				public void onLayout(Layer layer, double progress) {
				}
				public void onAnimationComplete() {
					// move style to new master row
					for (int i = 0; i < masterTable.getRowCount(); i++) {
						masterTable.getRowFormatter().removeStyleName(i, Styles.MasterDetailSelection.toString());
					}
					
					if (row < masterTable.getRowCount()) {
						masterTable.getRowFormatter().addStyleName(row, Styles.MasterDetailSelection.toString());
					}
	
					mainPanel.setWidgetTopHeight(erasePanel, offsetTop + 2, Unit.PX, offsetHeight - 4, Unit.PX);
	
					mainPanel.animate(10);
				}
			});
		}
		currentRow = row;
	}
	
	public void closeDetail(boolean animated) {
		if (!detailOpen) {
			return;
		}
		
		detailOpen = false;
		
		// animate
		// Determine size/offset of selected table row.
		int offsetTop = rowTopOffset(currentRow);
		int offsetLeft = masterContainer.getAbsoluteLeft();

		int offsetHeight = rowHeight(currentRow);
		int mainPanelHeight = mainPanel.getOffsetHeight();
		
		// This forcelayout should not have to be necessary.

		mainPanel.setWidgetTopHeight(selectionLine, offsetTop - 1, Unit.PX, offsetHeight + 2, Unit.PX);
		mainPanel.setWidgetLeftWidth(selectionLine, offsetLeft - 1, Unit.PX, 25, Unit.PCT);
		mainPanel.setWidgetTopHeight(erasePanel, offsetTop + 1, Unit.PX, offsetHeight - 2, Unit.PX);
		mainPanel.setWidgetLeftWidth(erasePanel, 20, Unit.PCT, 3, Unit.PX);
		mainPanel.setWidgetTopHeight(detailContainer, 0, Unit.PX, mainPanelHeight, Unit.PX);
		mainPanel.setWidgetLeftWidth(detailContainer, 20, Unit.PCT, 80, Unit.PCT);
		mainPanel.forceLayout();
		
		mainPanel.setWidgetTopHeight(detailContainer, offsetTop - 1, Unit.PX, offsetHeight + 2, Unit.PX);
		mainPanel.setWidgetLeftWidth(detailContainer, 20, Unit.PCT, 0, Unit.PCT);
		
		mainPanel.setWidgetTopHeight(selectionLine, offsetTop - 1, Unit.PX, offsetHeight + 2, Unit.PX);
		mainPanel.setWidgetLeftWidth(selectionLine, 20, Unit.PCT, 0, Unit.PCT);
			

		final int row = currentRow;
		mainPanel.animate(animated ? 150 : 0, new AnimationCallback() {
			public void onLayout(Layer layer, double progress) {
			}
			public void onAnimationComplete() {
				mainPanel.forceLayout();
				// Delay this until the animation is done, otherwise you see the erased line reappear.
				mainPanel.setWidgetLeftWidth(erasePanel, 20, Unit.PCT, 0, Unit.PX);

				// Remove style on current master.
				for (int i = 0; i < masterTable.getRowCount(); i++) {
					masterTable.getRowFormatter().removeStyleName(i, Styles.MasterDetailSelection.toString());
				}
			}
		});
		currentRow = -1;
	}

	private int rowHeight(final int row) {
		int offsetHeight;
		if (row < 0 || row >= masterTable.getRowCount()) {
			offsetHeight = 0;
		} else {
			offsetHeight = masterTable.getRowFormatter().getElement(row).getOffsetHeight();
		}
		return offsetHeight;
	}

	private int rowTopOffset(final int row) {
		int offsetTop;
		if (row < 0) {
			offsetTop = masterTable.getAbsoluteTop();
		} else if (row >= masterTable.getRowCount()) {
			offsetTop = masterTable.getAbsoluteTop() + masterTable.getOffsetHeight();
		} else {
			offsetTop = masterTable.getAbsoluteTop() - mainPanel.getAbsoluteTop() + masterTable.getRowFormatter().getElement(row).getOffsetTop();
		}
		return offsetTop;
	}
	
	protected HasWidgets createHeaderWrapper(LayoutPanel parent) {
		return null;
	}
	
	
	protected HasWidgets createFooterWrapper(LayoutPanel parent) {
		return null;
	}
	
	
	protected HasWidgets createMasterWrapper(LayoutPanel parent) {
		return null;
	}
	
	
	protected HasWidgets createDetailWrapper(LayoutPanel parent) {
		return null;
	}

	public static interface IsHTMLTable {
		HTMLTable asHTMLTable();
	}
}
