package com.amazingbookstore.dao;

import com.amazingbookstore.model.Item;

public class ItemDAO extends GenericDAO<Item> {

	private static final long serialVersionUID = 1L;

	public ItemDAO() {
		super(Item.class);
	}
	
}
