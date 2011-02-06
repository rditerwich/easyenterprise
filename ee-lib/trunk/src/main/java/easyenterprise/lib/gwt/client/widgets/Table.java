package easyenterprise.lib.gwt.client.widgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableSectionElement;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Grid;

public class Table extends Grid implements IsTable {

	public class HeaderRowFormatter extends RowFormatter {
		/**
		 * Gets the TR element representing the specified row.
		 * 
		 * @param row
		 *            the row whose TR element is to be retrieved
		 * @return the row's TR element
		 * @throws IndexOutOfBoundsException
		 */
		@Override
		public com.google.gwt.user.client.Element getElement(int row) {
			checkHeaderRowBounds(row);
			return getRow(
					(com.google.gwt.user.client.Element) Element
							.as(theadElement),
					row);
		}

		/**
		 * Ensure the TR element representing the specified row exists for
		 * subclasses that allow dynamic addition of elements.
		 * 
		 * @param row
		 *            the row whose TR element is to be retrieved
		 * @return the row's TR element
		 * @throws IndexOutOfBoundsException
		 */
		@Override
		protected com.google.gwt.user.client.Element ensureElement(int row) {
			prepareHeaderRow(row);
			return getRow(
					(com.google.gwt.user.client.Element) Element
							.as(theadElement),
					row);
		}

		@Override
		protected com.google.gwt.user.client.Element getRow(
				com.google.gwt.user.client.Element elem, int row) {
			return super.getRow(elem, row);
		}
	}

	private TableSectionElement theadElement;
	private int numHeaderRows;

	/**
	 * Current header cell formatter.
	 */
	private CellFormatter headerCellFormatter;

	/**
	 * Current row formatter.
	 */
	private HeaderRowFormatter headerRowFormatter;

	public Table() {
		super();
		setStyleName("ee-Table");
		setHeader();
	}

	public Table(int rows, int columns) {
		super(rows, columns);
		setStyleName("ee-Table");
		setHeader();
	}

	@Override
	public Table asTable() {
		return this;
	}

	/**
	 * Gets the {@link CellFormatter} associated with this table. Use casting to
	 * get subclass-specific functionality
	 * 
	 * @return this table's cell formatter
	 */
	public CellFormatter getHeaderCellFormatter() {
		return headerCellFormatter;
	}

	public int getHeaderRowCount() {
		return numHeaderRows;
	}

	@Override
	public void resizeColumns(int columns) {
		final int oldColumns = numColumns;

		super.resizeColumns(columns);
		if (oldColumns > columns) {
			// Fewer columns. Remove extraneous cells.
			for (int i = 0; i < numHeaderRows; i++) {
				for (int j = oldColumns - 1; j >= columns; j--) {
					removeHeaderCell(i, j);
				}
			}
		} else {
			// More columns. add cells where necessary.
			for (int i = 0; i < numHeaderRows; i++) {
				for (int j = oldColumns; j < columns; j++) {
					insertHeaderCell(i, j);
				}
			}
		}
	}

	@Override
	public void resizeRows(int rows) {
		final int oldRows = numRows;
		final int startIndex = numRows == 0 ? 0 : numRows - 1;
		boolean even = (startIndex & 1) == 0;

		super.resizeRows(rows);
		if (rows > 0 && oldRows < 1) {
			getRowFormatter().addStyleName(0, "ee-TableFirst");
		}
		if (rows > 0 && oldRows == 0) {
			getRowFormatter().addStyleName(rows - 1, "ee-TableLast");
		}
		for (int i = startIndex; i < numRows; i++) {
			getRowFormatter().addStyleName(i, even ? "ee-TableEven" : "ee-TableOdd");
			even = !even;
		}
	}

	public void removeHeaderRow(int row) {
		final int columnCount = getCellCount(row);
		for (int column = 0; column < columnCount; ++column) {
			cleanHeaderCell(row, column, false);
		}
		theadElement.removeChild(headerRowFormatter.getRow(
				(com.google.gwt.user.client.Element) Element.as(theadElement),
				row));
		numHeaderRows--;
	}

	public void resizeHeaderRows(int rows) {
		if (numHeaderRows == rows) {
			return;
		}
		if (rows < 0) {
			throw new IndexOutOfBoundsException(
					"Cannot set number of header rows to " + rows);
		}
		if (numHeaderRows < rows) {
			addHeaderRows(getTHeadElement(), rows - numHeaderRows, numColumns);
			numHeaderRows = rows;
		} else {
			while (numHeaderRows > rows) {
				// Fewer rows. Remove extraneous ones.
				removeHeaderRow(numHeaderRows - 1);
			}
		}
	}

	public void setHeader() {
		if (theadElement == null) {
			theadElement = Document.get().createTHeadElement();
			getElement().insertBefore(theadElement, getBodyElement());
			setHeaderRowFormatter(new HeaderRowFormatter());
			resizeHeaderRows(1);
		}
	}
	
	protected void setHeaderRowFormatter(HeaderRowFormatter headerRowFormatter) {
		this.headerRowFormatter = headerRowFormatter;
	}

	public void setHeaderHTML(int row, int column, String text) {
		prepareHeaderCell(row, column);
		final Element th = cleanHeaderCell(row, column, text == null);

		if (text != null) {
			th.setInnerHTML(text);
		}
	}

	public void setHeaderText(int row, int column, String text) {
		prepareHeaderCell(row, column);
		final Element th = cleanHeaderCell(row, column, text == null);

		if (text != null) {
			th.setInnerText(text);
		}
	}

	/**
	 * Checks that the header row is within the correct bounds.
	 * 
	 * @param row
	 *            header row index to check
	 * @throws IndexOutOfBoundsException
	 */
	protected void checkHeaderRowBounds(int row) {
		int rowSize = getHeaderRowCount();
		if ((row >= rowSize) || (row < 0)) {
			throw new IndexOutOfBoundsException("Row index: " + row
					+ ", Row size: " + rowSize);
		}
	}

	/**
	 * Creates a new, empty cell.
	 */
	protected com.google.gwt.user.client.Element createHeaderCell() {
		Element td = Document.get().createTHElement();

		// Add a non-breaking space to the TH. This ensures that the cell is
		// displayed.
		td.setInnerHTML("&nbsp;");
		return (com.google.gwt.user.client.Element) td;
	}

	/**
	 * Inserts a new cell into the specified row.
	 * 
	 * @param row
	 *            the row into which the new cell will be inserted
	 * @param column
	 *            the column before which the cell will be inserted
	 * @throws IndexOutOfBoundsException
	 */
	protected void insertHeaderCell(int row, int column) {
		DOM.insertChild(headerRowFormatter.getRow(
				(com.google.gwt.user.client.Element) Element.as(theadElement),
				row), createHeaderCell(), column);
	}

	/**
	 * Checks that a cell is a valid cell in the table.
	 * 
	 * @param row
	 *            the cell's row
	 * @param column
	 *            the cell's column
	 * @throws IndexOutOfBoundsException
	 */
	protected void prepareHeaderCell(int row, int column) {
		// Ensure that the indices are not negative.
		prepareHeaderRow(row);
		if (column < 0) {
			throw new IndexOutOfBoundsException(
					"Cannot access a header column with a negative index: "
							+ column);
		}

		if (column >= numColumns) {
			throw new IndexOutOfBoundsException("Column index: " + column
					+ ", Column size: " + numColumns);
		}
	}

	/**
	 * Checks that the header row index is valid.
	 * 
	 * @param row
	 *            The header row index to be checked
	 * @throws IndexOutOfBoundsException
	 *             if the header row is negative
	 */
	protected void prepareHeaderRow(int row) {
		// Ensure that the indices are not negative.
		if (row < 0) {
			throw new IndexOutOfBoundsException(
					"Cannot access a header row with a negative index: " + row);
		}

		/**
		 * Grid does not lazily create cells, so simply ensure that the
		 * requested row and column are valid
		 */
		if (row >= numHeaderRows) {
			throw new IndexOutOfBoundsException("Header Row index: " + row
					+ ", Header Row size: " + numHeaderRows);
		}
	}

	protected Element getTHeadElement() {
		return theadElement;
	}

	/**
	 * Removes the specified cell from the table.
	 * 
	 * @param row
	 *            the row of the cell to remove
	 * @param column
	 *            the column of cell to remove
	 * @throws IndexOutOfBoundsException
	 */
	protected void removeHeaderCell(int row, int column) {
		checkCellBounds(row, column);
		final Element th = cleanHeaderCell(row, column, false);
		final Element tr = headerRowFormatter.getRow(
				(com.google.gwt.user.client.Element) Element.as(theadElement),
				row);

		tr.removeChild(th);
	}

	/**
	 * Native method to add rows into a table with a given number of columns.
	 * 
	 * @param table
	 *            the table element
	 * @param rows
	 *            number of rows to add
	 * @param columns
	 *            the number of columns per row
	 */
	private void addHeaderRows(Element head, int rows, int columns) {
		final Element th = Document.get().createTHElement();

		th.setInnerHTML("&nbsp;");
		final Element row = Document.get().createTRElement();

		for (int cellNum = 0; cellNum < columns; cellNum++) {
			row.appendChild(th.cloneNode(true));
		}
		head.appendChild(row);
		for (int rowNum = 1; rowNum < rows; rowNum++) {
			head.appendChild(row.cloneNode(true));
		}
	}

	/**
	 * Removes any widgets, text, and HTML within the cell. This method assumes
	 * that the requested cell already exists.
	 * 
	 * @param row
	 *            the cell's row
	 * @param column
	 *            the cell's column
	 * @param clearInnerHTML
	 *            should the cell's inner html be cleared?
	 * @return element that has been cleaned
	 */
	private com.google.gwt.user.client.Element cleanHeaderCell(int row,
			int column, boolean clearInnerHTML) {
		// Clear whatever is in the cell.
		final com.google.gwt.user.client.Element th = (com.google.gwt.user.client.Element) Element
				.as(getHeaderCellElement(theadElement, row, column));

		internalClearCell(th, clearInnerHTML);
		return th;
	}

	/**
	 * Native method to get a cell's element.
	 * 
	 * @param table
	 *            the table element
	 * @param row
	 *            the row of the cell
	 * @param col
	 *            the column of the cell
	 * @return the element
	 */

	private TableCellElement getHeaderCellElement(TableSectionElement head,
			int row, int col) {
		return head.getRows().getItem(row).getCells().getItem(col);
	};
}
