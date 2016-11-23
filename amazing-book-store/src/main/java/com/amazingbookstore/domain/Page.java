package com.amazingbookstore.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


public class Page<T> implements Iterable<T> {

	private List<T> content = new ArrayList<T>();	

	private long total;

	private Pageable pageable;

	public Page(List<T> content) {
		this(content, null, null == content ? 0 : content.size());
	}

	public Page(List<T> content, Pageable pageable, long total) {
		this.content.addAll(content);
		this.pageable = pageable;
		this.total = total;
	}

	public List<T> getContent() {
		return Collections.unmodifiableList(this.content);
	}

	public int getTotalPages() {
		return getSize() == 0 ? 1 : (int) Math.ceil((double) total / (double) getSize());
	}

	public long getTotalCount() {
		return total;
	}

	public int getPage() {
		return pageable == null ? 0 : pageable.getPage();
	}

	public int getSize() {
		return pageable == null ? 0 : pageable.getSize();
	}

	public boolean hasNext() {
		return this.getPage() + 1 < this.getTotalPages();
	}

	public boolean hasPrevious() {
		return this.getPage() > 1;
	}
	
	public boolean isFirst() {
		return !this.hasPrevious();
	}
	
	public boolean isLast() {
		return !this.hasNext();
	}
	
	public Pageable nextPageable() {
		return this.hasNext() ? pageable.next() : null;
	}
	
	public Pageable previousPageable() {
		return this.hasPrevious() ? pageable.previous() : null;
	}

	public boolean hasContent() {
		return !content.isEmpty();
	}

	@Override
	public Iterator<T> iterator() {
		return content.iterator();
	}

}