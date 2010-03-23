package agilexs.catalogxsadmin.presentation.client;

import java.util.ArrayList;

import agilexs.catalogxsadmin.presentation.client.Util.DeleteHandler;
import agilexs.catalogxsadmin.presentation.client.catalog.PropertyType;
import agilexs.catalogxsadmin.presentation.client.i18n.I18NCatalogXS;
import agilexs.catalogxsadmin.presentation.client.page.View;
import agilexs.catalogxsadmin.presentation.client.util.CatalogWidgetUtil;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HTMLTable.Cell;

class ItemPropertiesView extends Composite implements View {

  private final static I18NCatalogXS i18n = GWT.create(I18NCatalogXS.class);
  private static final ResourceBundle rb = GWT.create(ResourceBundle.class);

  private static final int DELETE_COLUMN = 6;

  public class PGPRowView {
    final TextBox defaultName = new TextBox();
    final ListBox type = new ListBox();
    final SimplePanel defaultValueWrapper = new SimplePanel();
    final TextBox name = new TextBox();
    final SimplePanel valueWrapper = new SimplePanel();
    final CheckBox pgOnly = new CheckBox();

    public TextBoxBase getDefaultName() {
      return defaultName;
    }

    public TextBoxBase getName() {
      return name;
    }

    public ListBox getType() {
      return type;
    }

    public Widget getDefaultValueWidget() {
      return defaultValueWrapper;
    }

    public Widget getValueWidget() {
      return valueWrapper;
    }

    public CheckBox getPGOnly() {
      return pgOnly;
    }

    public Widget setDefaultValueWidget(PropertyType type) {
      return CatalogWidgetUtil.setPropertyTypeWidget(defaultValueWrapper, type);
    }

    public Widget setValueWidget(PropertyType type) {
      return CatalogWidgetUtil.setPropertyTypeWidget(valueWrapper, type);
    }
  }

  interface ItemPropertiesUiBinder extends UiBinder<Widget, ItemPropertiesView> {}
  private static ItemPropertiesUiBinder uiBinder = GWT.create(ItemPropertiesUiBinder.class);

  @UiField Button addNew;
  @UiField Grid grid;

  private ArrayList<PGPRowView> rowViews = new ArrayList<PGPRowView>();

  private DeleteHandler<Integer> deleteHandler;

  public ItemPropertiesView() {
    initWidget(uiBinder.createAndBindUi(this));
    grid.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        if (deleteHandler == null) return;
        final Cell c = grid.getCellForEvent(event);

        if (c.getCellIndex() == DELETE_COLUMN) {
          deleteHandler.onDelete(Integer.valueOf(c.getRowIndex()));
        }
      }
    });
    resetTable();
    addNew.setText("Add new property");
    addHeader();
  }

  @Override
  public Widget asWidget() {
    return this;
  }

  private void addHeader() {
    grid.setWidget(0, 0, new InlineLabel(i18n.name()));
    grid.setWidget(0, 1, new InlineLabel(i18n.type()));
    grid.setWidget(0, 2, new InlineLabel(i18n.value()));
    grid.setWidget(0, 3, new InlineLabel(i18n.groupOnly()));
    grid.setWidget(0, 4, new InlineLabel(i18n.languageSpecificName()));
    grid.setWidget(0, 5, new InlineLabel(i18n.languageSpecificValue()));
    grid.setWidget(0, 6, new InlineHTML("&nbsp;"));
  }

  public Button getNewPropertyButton() {
    return addNew;
  }

  public PGPRowView addRow() {
    return setRow(grid.getRowCount()-1);
  }

  public void resetTable() {
    grid.resize(1, 7);
  }

  public void setDeleteHandler(DeleteHandler<Integer> deleteHandler) {
    this.deleteHandler = deleteHandler;
  }

  /**
   * Initializes the grid at the given row + 1 number. 1 is added because this
   * first row is reserved for the header. This means the  
   * @param row
   * @return
   */
  public PGPRowView setRow(int row) {
    PGPRowView rowView;
    
    if (rowViews.size() > row) {
      rowView = rowViews.get(row);
    } else {
      rowView = new PGPRowView();
      rowViews.add(rowView);
    }
    row = row + 1; //offset header
    if (row >= grid.getRowCount()) {
      grid.resizeRows(row + 1);
    }
    grid.setWidget(row, 0, (Widget) rowView.getDefaultName());
    grid.setWidget(row, 1, rowView.getType());
    grid.setWidget(row, 2, rowView.getDefaultValueWidget());
    grid.setWidget(row, 3, rowView.getPGOnly());
    grid.setWidget(row, 4, (Widget) rowView.getName());
    grid.setWidget(row, 5, rowView.getValueWidget());
    grid.setWidget(row, DELETE_COLUMN, new Image(rb.deleteImage()));

    return rowView;
  }
}
