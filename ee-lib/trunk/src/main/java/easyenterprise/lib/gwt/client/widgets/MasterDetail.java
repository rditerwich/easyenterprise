package easyenterprise.lib.gwt.client.widgets;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.layout.client.Layout.AnimationCallback;
import com.google.gwt.layout.client.Layout.Layer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

import easyenterprise.lib.gwt.client.Style;
import easyenterprise.lib.gwt.client.StyleUtil;

public abstract class MasterDetail extends Composite implements RequiresResize, ProvidesResize {

	public enum Styles implements Style {
		MasterDetail, MasterDetailHeader, MasterDetailMaster, MasterDetailTable, MasterDetailSelection, MasterDetailSelectionPanel, MasterDetailDetailPanel, MasterDetailErasePanel;
		
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

	private LayoutPanel eraseLine;

	private LayoutPanel selectionLine;

	private int currentRow;

	private final int headerSize;

	private final int footerSize;
	private DockLayoutPanel masterPanel;
	private Widget tableContainer;

	public MasterDetail(int headerSize, int footerSize) {
		this.headerSize = headerSize;
		this.footerSize = footerSize;
		initWidget(mainPanel = new LayoutPanel() {{
			StyleUtil.add(this, Styles.MasterDetail);
			
			// auto-close detail when clicked on background
			addDomHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					closeDetail(true);
				}
			}, ClickEvent.getType());
		}});
	}
	
	public int getHeight() {
		return this.headerSize + this.footerSize + masterTable.getRowFormatter().getElement(masterTable.getRowCount()).getOffsetHeight();
	}

	protected LayoutPanel getMasterHeader() {
		initializeMainPanel();
		
		return headerPanel;
	}
	
	protected LayoutPanel getMasterFooter() {
		initializeMainPanel();

		return footerPanel; 
	}
	
	protected Table getMasterTable() {
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
		mainPanel.onResize();
	}
	
	protected void openDetail(final int row) {
		
		initializeDetailPanel();
		
		// Determine size/offset of selected table row.
		int offsetTop = masterTable.getAbsoluteTop() - mainPanel.getAbsoluteTop() + masterTable.getRowFormatter().getElement(row).getOffsetTop();
		int offsetLeft = tableContainer.getAbsoluteLeft();
		int offsetHeight = masterTable.getRowFormatter().getElement(row).getOffsetHeight();
		
		// Set selection 
		
		// If the details is not open, position to get a nice starting position
		if (!detailOpen) {

			mainPanel.setWidgetTopHeight(detailPanel, offsetTop -1, Unit.PX, offsetHeight + 2, Unit.PX);
			mainPanel.setWidgetLeftWidth(detailPanel, 20, Unit.PCT, 0, Unit.PX);
			
			int detailPanelOffset = mainPanel.getElement().getOffsetWidth() / 5;
			mainPanel.setWidgetTopHeight(selectionLine, offsetTop - 1, Unit.PX, offsetHeight + 2, Unit.PX);
			mainPanel.setWidgetLeftWidth(selectionLine, detailPanelOffset, Unit.PX, 0, Unit.PX);
			
			mainPanel.setWidgetTopHeight(eraseLine, offsetTop + 2, Unit.PX, offsetHeight - 4, Unit.PX);
			mainPanel.setWidgetLeftWidth(eraseLine, 20, Unit.PCT, 2, Unit.PX);
			
			mainPanel.forceLayout();
		}
		detailOpen = true;

		// Update selection
		mainPanel.setWidgetTopHeight(selectionLine, offsetTop - 1, Unit.PX, offsetHeight + 2, Unit.PX);
		mainPanel.setWidgetLeftWidth(selectionLine, offsetLeft - 1, Unit.PX, 25, Unit.PCT);

		// erase a bit of the detail panel:
		mainPanel.setWidgetTopHeight(eraseLine, offsetTop + 2, Unit.PX, offsetHeight - 4, Unit.PX);
		mainPanel.setWidgetLeftWidth(eraseLine, 20, Unit.PCT, 4, Unit.PX);
		
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
		mainPanel.setWidgetTopHeight(eraseLine, offsetTop + 1, Unit.PX, offsetHeight - 2, Unit.PX);
		mainPanel.setWidgetLeftWidth(eraseLine, 20, Unit.PCT, 2, Unit.PX);
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
				// Delay this until the animation is done, otherwise you see the erased line reappear.
				mainPanel.setWidgetTopHeight(eraseLine, offsetTop + 1, Unit.PX, offsetHeight - 2, Unit.PX);
				mainPanel.setWidgetLeftWidth(eraseLine, 20, Unit.PCT, 0, Unit.PX);
				
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
		mainPanel.setWidgetLeftRight(masterPanel, 24, Unit.PX, 24, Unit.PX);

		
		mainPanel.add(selectionLine = new LayoutPanel() {{
			StyleUtil.add(this, Styles.MasterDetailSelectionPanel);
		}});

		// Make sure the selection panel does not overlap the master
		mainPanel.setWidgetLeftWidth(selectionLine, 100, Unit.PCT, 0, Unit.PX);

		masterPanelCreated(masterPanel);
	}
	
	protected Widget tableCreated(Table table) {
		return table;
	}
	
	private void initializeDetailPanel() {
		if (detailInitialized) {
			return;
		}
		detailInitialized = true;

		mainPanel.add(detailPanel = new LayoutPanel() {{
			StyleUtil.add(this, Styles.MasterDetailDetailPanel);
			
			// make sure clicks on detail panel aren't causeing the detail panel to close
			addDomHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					event.stopPropagation();
				}
			}, ClickEvent.getType());

		}});
		mainPanel.add(eraseLine = new LayoutPanel() {{
			StyleUtil.add(this, Styles.MasterDetailErasePanel);
		}});

		// make sure erase panel does not overlap the detail panel
		mainPanel.setWidgetLeftWidth(eraseLine, 100, Unit.PCT, 0, Unit.PX);
		
		// fill in detail panel
		detailPanelCreated(detailPanel);
	}

	protected void masterPanelCreated(DockLayoutPanel masterPanel2) {
	}

	protected void detailPanelCreated(LayoutPanel detailPanel) {
	}
}
