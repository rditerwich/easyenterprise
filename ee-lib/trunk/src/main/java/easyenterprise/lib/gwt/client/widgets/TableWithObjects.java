package easyenterprise.lib.gwt.client.widgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import easyenterprise.lib.util.CollectionUtil;

public class TableWithObjects<T> extends Table {

	public TableWithObjects() {
		super();
	}

	public TableWithObjects(int rows, int columns) {
		super(rows, columns);
	}

	private List<T> objects = Collections.emptyList();
	
	@Override
	public int insertRow(int beforeRow) {
		if (beforeRow < objects.size()) {
			objects.add(beforeRow, null);
		}
		return super.insertRow(beforeRow);
	}

	@Override
	public void removeRow(int row) {
		if (row < objects.size()) {
			objects.remove(row);
		}
		super.removeRow(row);
	}

	@Override
	public void resizeRows(int rows) {
		super.resizeRows(rows);
		while (objects.size() > rows) objects.remove(objects.size() - 1);
	}

	public T getObject(int row) {
		if (row < objects.size()) {
			return objects.get(row);
		}
		return null;
	}
	
	public void setObject(int row, T object) {
		if (objects.isEmpty()) {
			objects = new ArrayList<T>();
		}
		while (objects.size() <= row) {
			objects.add(null);
		}
		objects.set(row, object);
	}
	
	public int findObject(T object) {
		int index = CollectionUtil.indexOfRef(objects, object);
		if (index != -1) return index;
		return objects.indexOf(object);
	}
}
