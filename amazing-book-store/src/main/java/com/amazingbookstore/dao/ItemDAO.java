package com.amazingbookstore.dao;

import java.util.List;

import javax.persistence.NoResultException;

import com.amazingbookstore.model.Item;

public class ItemDAO extends GenericDAO<Item> {

	private static final long serialVersionUID = 1L;

	public ItemDAO() {
		super(Item.class);
	}
	
	public boolean inserirItem(Item item) {
        try {
              save(item);
              return true;
        } catch (Exception e) {
              e.printStackTrace();
              return false;
        }
	}
	
	public List<Item> listarItem(Integer idCarrinhoCompras) {
		 try {
			 return createNamedQuery(Item.FIND_BY_CAR_SHOP)
					 .setParameter("idCarrinhoCompras", idCarrinhoCompras)
					 .getResultList();
		 } catch (NoResultException e) {
				return null;
		}
	}
	
	public boolean excluirItem(Item item) {
        try {
              delete(item.getId(), Item.class);
              return true;
        } catch (Exception e) {
              e.printStackTrace();
              return false;
        }
	}
}
