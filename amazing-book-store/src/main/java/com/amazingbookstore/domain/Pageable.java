package com.amazingbookstore.domain;

public interface Pageable {

	public Pageable next();
	
	public Pageable previous();

	public Integer getPage();
	
	public Integer getSize();
	
	public Integer getOffset();

}