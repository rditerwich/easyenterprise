package easyenterprise.lib.gwt.client.widgets;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.layout.client.Layout.AnimationCallback;
import com.google.gwt.layout.client.Layout.Layer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import easyenterprise.lib.gwt.client.Style;
import easyenterprise.lib.gwt.client.StyleUtil;

public abstract class CopyOfMasterDetail extends Composite implements RequiresResize, ProvidesResize {

	private static final int MASTERPADDING = 24;
	private static final int BLUR_DISTANCE = 5;
	private static final int BLUR_SPREAD = 3;
	private static int SHADOWSIZE = 2*BLUR_SPREAD;
	public enum Styles implements Style {
		MasterDetail, MasterDetailHeader, MasterDetailMaster, MasterDetailTable, MasterDetailSelection, MasterDetailSelectionPanel, MasterDetailSelectionPanelContent, MasterDetailDetailPanel, MasterDetailFillPanel, MasterDetailErasePanel;
		
		public String toString() {
			return "ee-" + super.toString();
		};
	}
	
	
	private final LayoutPanel mainPanel;
	private LayoutPanel headerPanel;
	private LayoutPanel footerPanel;
	
	private Table masterTable;
	private LayoutPanel detailPanel;
	
	private boolean initialized;
	private boolean detailInitialized;

	private boolean detailOpen;

	private LayoutPanel erasePanel;
	private LayoutPanel selectionLine;

	private int currentRow;

	private final int headerSize;

	private final int footerSize;
	private DockLayoutPanel masterPanel;
	private Widget tableContainer;
	private ScrollPanel scrollPanel;

	public CopyOfMasterDetail(int headerSize, int footerSize) {
		this.headerSize = headerSize;
		this.footerSize = footerSize;
		initWidget(scrollPanel = new ScrollPanel(mainPanel = new LayoutPanel() {{
			StyleUtil.addStyle(this, Styles.MasterDetail);
			
			// auto-close detail when clicked on background
			addDomHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					closeDetail(true);
				}
			}, ClickEvent.getType());
		}}));
	}
	
	public int getHeight() {
		return this.headerSize + this.footerSize + masterTable.getRowFormatter().getElement(masterTable.getRowCount()).getOffsetHeight();
	}

	public LayoutPanel getMasterHeader() {
		initializeMainPanel();
		
		return headerPanel;
	}
	
	public LayoutPanel getMasterFooter() {
		initializeMainPanel();

		return footerPanel; 
	}
	
	public int getHeaderSize() {
		return headerSize;
	}

	public int getFooterSize() {
		return footerSize;
	}
	
	public LayoutPanel getMainPanel() {
		return mainPanel;
	}
	
	public DockLayoutPanel getMasterPanel() {
		return masterPanel;
	}
	
	public Widget getTableContainer() {
		return tableContainer;
	}
	
	public Table getMasterTable() {
		initializeMainPanel();
		
		return masterTable;
	}
	
	protected LayoutPanel getDetail() {
		initializeDetailPanel();
		
		return detailPanel;
	}
	
	public boolean getDetailOpen() {
		return detailOpen;
	}
	
	public int getCurrentRow() {
		return currentRow;
	}
	
	@Override
	public void onResize() {
//		System.out.println("onResize():");
//		System.out.println("  Scrollpanel size is " + scrollPanel.getOffsetWidth() + "x" + scrollPanel.getOffsetHeight());
//		System.out.println("  MasterDetail size is " + getOffsetWidth() + "x" + getOffsetHeight());
//		System.out.println("  MainPanel size is " + getMainPanel().getOffsetWidth() + "x" + getMainPanel().getOffsetHeight());
//		System.out.println("  MasterPanel size is " + getMasterPanel().getOffsetWidth() + "x" + getMasterPanel().getOffsetHeight());
//		System.out.println("  Container size is " + getTableContainer().getOffsetWidth() + "x" + getTableContainer().getOffsetHeight());
//		System.out.println("  ContainerElt size is " + getTableContainer().getElement().getOffsetWidth() + "x" + getTableContainer().getElement().getOffsetHeight());
//		System.out.println("  ContainerCont size is " + getMasterPanel().getWidgetContainerElement(getTableContainer()).getOffsetWidth() + "x" + getMasterPanel().getWidgetContainerElement(getTableContainer()).getOffsetHeight());
//		System.out.println("  MasterTable size is " + getMasterTable().getOffsetWidth() + "x" + getMasterTable().getOffsetHeight());
//		System.out.println("  Header size is " + getMasterHeader().getOffsetWidth() + "x" + getMasterHeader().getOffsetHeight());
		if (getMasterTable() == null) {
			return;
		}
		
		// With is max of table and scrollable area. 
		int newWidth = Math.max(getMasterTable().getElement().getOffsetWidth() + 2*MASTERPADDING + getExtraTableWidth(), scrollPanel.getElement().getClientWidth()); 
		
		// Height is max of table and scollable area
		int newHeight = Math.max(getMasterTable().getOffsetHeight() + getHeaderSize() + getFooterSize() + getExtraTableHeight(), scrollPanel.getElement().getClientHeight());  
		System.out.println("  Setting m.size to " + newWidth + "x" + newHeight);
		mainPanel.setPixelSize(newWidth, newHeight);

		scrollPanel.onResize();
	}
	
	protected void masterChanged() {
		onResize();
	}

	protected void openDetail(final int row) {
		
		initializeDetailPanel();
		
		// Determine size/offset of selected table row.
		final int offsetTop = masterTable.getAbsoluteTop() - mainPanel.getAbsoluteTop() + masterTable.getRowFormatter().getElement(row).getOffsetTop();
		final int offsetLeft = tableContainer.getAbsoluteLeft();
		final int offsetHeight = masterTable.getRowFormatter().getElement(row).getOffsetHeight();
		
		// Set selection 
		
		// If the details is not open, position to get a nice starting position
		if (!detailOpen) {

			mainPanel.setWidgetTopHeight(detailPanel, offsetTop -1, Unit.PX, offsetHeight + 2, Unit.PX);
			mainPanel.setWidgetLeftWidth(detailPanel, 20, Unit.PCT, 0, Unit.PX);
			
			int detailPanelOffset = mainPanel.getElement().getOffsetWidth() / 5;
			mainPanel.setWidgetTopHeight(selectionLine, offsetTop - 1 - SHADOWSIZE, Unit.PX, offsetHeight + 2 + 2 *SHADOWSIZE, Unit.PX);
			mainPanel.setWidgetLeftWidth(selectionLine, detailPanelOffset, Unit.PX, 0, Unit.PX);
			
//			mainPanel.setWidgetTopHeight(eraseLine, offsetTop + 2, Unit.PX, offsetHeight - 4, Unit.PX);
//			mainPanel.setWidgetLeftWidth(eraseLine, 20, Unit.PCT, 3, Unit.PX);
			
			mainPanel.forceLayout();
		}
		detailOpen = true;

		// Update selection
		int w = getDetail().getAbsoluteLeft() - (offsetLeft - 1) + 9;
		mainPanel.setWidgetTopHeight(selectionLine, offsetTop - 1 - SHADOWSIZE, Unit.PX, offsetHeight + 2 + 2*SHADOWSIZE, Unit.PX);
		mainPanel.setWidgetLeftRight(selectionLine, offsetLeft - 1, Unit.PX, getDetail().getAbsoluteLeft() + 24, Unit.PX);

		// erase a bit of the detail panel:
//		int left = getMasterTable().getAbsoluteLeft() + getMasterTable().getOffsetWidth() - 1;
		int left = tableContainer.getAbsoluteLeft() + 1;
		int width = getMasterTable().getAbsoluteLeft() - left ;
		mainPanel.setWidgetTopHeight(erasePanel, offsetTop + 2, Unit.PX, offsetHeight - 4, Unit.PX);
		mainPanel.setWidgetLeftWidth(erasePanel, 20, Unit.PCT, 3+SHADOWSIZE, Unit.PX);
		
		mainPanel.setWidgetTopHeight(detailPanel, 0, Unit.PX, 100, Unit.PCT);
		mainPanel.setWidgetLeftWidth(detailPanel, 20, Unit.PCT, 80, Unit.PCT);
		

		final int oldRow = currentRow;
		mainPanel.animate(150, new AnimationCallback() {
			public void onLayout(Layer layer, double progress) {
			}
			public void onAnimationComplete() {
				// move style to new master row
				if (oldRow != -1) {
					masterTable.getRowFormatter().removeStyleName(oldRow, Styles.MasterDetailSelection.toString());
				}
				masterTable.getRowFormatter().addStyleName(row, Styles.MasterDetailSelection.toString());

				mainPanel.setWidgetTopHeight(erasePanel, offsetTop + 2, Unit.PX, offsetHeight - 4, Unit.PX);

				mainPanel.animate(10);
			}
		});
		
		currentRow = row;
	}
	
	protected void closeDetail(boolean animated) {
		if (!detailOpen) {
			return;
		}
		
		detailOpen = false;
		
		final int row = currentRow;
		// animate
		// Determine size/offset of selected table row.
		final int offsetTop = masterTable.getAbsoluteTop() - mainPanel.getAbsoluteTop() + masterTable.getRowFormatter().getElement(row).getOffsetTop();
		int offsetLeft = tableContainer.getAbsoluteLeft();

//		final int offsetTop = masterTable.getRowFormatter().getElement(currentRow).getOffsetTop() + headerSize;
		final int offsetHeight = masterTable.getRowFormatter().getElement(currentRow).getOffsetHeight();
//		int offsetLeft = masterTable.getRowFormatter().getElement(currentRow).getOffsetLeft();
		int mainPanelHeight = mainPanel.getOffsetHeight();
		
		// This forcelayout should not have to be necessary.

		mainPanel.setWidgetTopHeight(selectionLine, offsetTop - 1, Unit.PX, offsetHeight + 2, Unit.PX);
		mainPanel.setWidgetLeftWidth(selectionLine, offsetLeft - 1, Unit.PX, 25, Unit.PCT);
		mainPanel.setWidgetTopHeight(erasePanel, offsetTop + 1, Unit.PX, offsetHeight - 2, Unit.PX);
		mainPanel.setWidgetLeftWidth(erasePanel, 20, Unit.PCT, 3, Unit.PX);
		mainPanel.setWidgetTopHeight(detailPanel, 0, Unit.PX, mainPanelHeight, Unit.PX);
		mainPanel.setWidgetLeftWidth(detailPanel, 20, Unit.PCT, 80, Unit.PCT);
		mainPanel.forceLayout();
		
		mainPanel.setWidgetTopHeight(detailPanel, offsetTop - 1, Unit.PX, offsetHeight + 2, Unit.PX);
		mainPanel.setWidgetLeftWidth(detailPanel, 20, Unit.PCT, 0, Unit.PCT);
		
		mainPanel.setWidgetTopHeight(selectionLine, offsetTop - 1, Unit.PX, offsetHeight + 2, Unit.PX);
		mainPanel.setWidgetLeftWidth(selectionLine, 20, Unit.PCT, 0, Unit.PCT);
			

		mainPanel.animate(animated ? 150 : 0, new AnimationCallback() {
			public void onLayout(Layer layer, double progress) {
			}
			public void onAnimationComplete() {
				mainPanel.forceLayout();
				// Delay this until the animation is done, otherwise you see the erased line reappear.
				mainPanel.setWidgetLeftWidth(erasePanel, 20, Unit.PCT, 0, Unit.PX);

				// Remove style on current master.
				if (masterTable.getRowCount() > row && row >= 0) {
					masterTable.getRowFormatter().removeStyleName(row, Styles.MasterDetailSelection.toString());
				}
			}
		});
		currentRow = -1;

	}
	
	private void initializeMainPanel() {
		if (initialized) {
			return;
		}
		initialized = true;
		
		mainPanel.add(masterPanel = new DockLayoutPanel(Unit.PX) {{
			addNorth(headerPanel = new LayoutPanel(), headerSize);
			addSouth(footerPanel = new LayoutPanel(), footerSize);
			headerPanel.setStylePrimaryName(Styles.MasterDetailHeader.toString());
			tableContainer = tableCreated(masterTable = new Table());
			tableContainer.setStylePrimaryName(Styles.MasterDetailMaster.toString());
			masterTable.addStyleName(Styles.MasterDetailTable.toString());
			add(tableContainer);
		}});
		mainPanel.setWidgetLeftRight(masterPanel, MASTERPADDING, Unit.PX, MASTERPADDING, Unit.PX);

		
		mainPanel.add(selectionLine = new LayoutPanel() {{
			StyleUtil.addStyle(this, Styles.MasterDetailSelectionPanel);
			add(new FlowPanel() {{
				StyleUtil.addStyle(this, Styles.MasterDetailSelectionPanelContent);
			}});
		}});

		// Make sure the selection panel does not overlap the master
		mainPanel.setWidgetLeftWidth(selectionLine, 100, Unit.PCT, 0, Unit.PX);

		masterPanelCreated(masterPanel);
		
		onResize();
	}
	
	protected Widget tableCreated(Table table) {
		return table;
	}
	
	
	protected int getExtraTableWidth() {
		return 0;
	}

	protected int getExtraTableHeight() {
		return 0;
	}


	
	private void initializeDetailPanel() {
		if (detailInitialized) {
			return;
		}
		detailInitialized = true;

		mainPanel.add(detailPanel = new LayoutPanel() {{
			StyleUtil.addStyle(this, Styles.MasterDetailDetailPanel);
			
			// make sure clicks on detail panel aren't causeing the detail panel to close
			addDomHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					event.stopPropagation();
				}
			}, ClickEvent.getType());

		}});
		mainPanel.add(erasePanel = new LayoutPanel() {{
			StyleUtil.addStyle(this, Styles.MasterDetailErasePanel);
		}});

		// make sure erase panel does not overlap the detail panel
		mainPanel.setWidgetLeftWidth(erasePanel, 100, Unit.PCT, 0, Unit.PX);
		
		// fill in detail panel
		detailPanelCreated(detailPanel);
	}

	protected void masterPanelCreated(DockLayoutPanel masterPanel2) {
	}

	protected void detailPanelCreated(LayoutPanel detailPanel) {
	}
}
