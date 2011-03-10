package easyenterprise.lib.util;

import java.io.Serializable;

import com.google.common.base.Objects;

public class Paging implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final Paging NO_PAGING = new Paging(0, -1);
	
	private int pageStart;
	private int pageSize;

	public static Paging get(int pageStart, int pageSize) {
		if (pageSize < 0) {
			return NO_PAGING;
		} else {
			return new Paging(pageStart, pageSize);
		}
	}

	private Paging() {
		this.pageStart = 0;
		this.pageSize = -1;
	}
	
	private Paging(int pageStart, int pageSize) {
		this.pageStart = pageStart;
		this.pageSize = pageSize;
	}
	
	public boolean shouldPage() {
		return pageSize >= 0;
	}
	
	public int getPageStart() {
		return pageStart;
	}

	public int getPageSize() {
		return pageSize;
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(pageStart, pageSize);
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof Paging
			&& ((Paging) obj).pageStart == pageStart
			&& ((Paging) obj).pageSize == pageSize;
	}
	
	@Override
	public String toString() {
		return "Page(" + pageStart + ", " + pageSize +")";
	}
}
