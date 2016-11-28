package com.amazingbookstore.dao;

import com.amazingbookstore.model.CarrinhoCompra;

public class CarrinhoCompraDAO extends GenericDAO<CarrinhoCompra> {

	private static final long serialVersionUID = 1L;

	public CarrinhoCompraDAO() {
		super(CarrinhoCompra.class);
	}

	public CarrinhoCompra inserirCarrinhoo(CarrinhoCompra carrinhoCompra) {
        try {
              save(carrinhoCompra);
              return carrinhoCompra;
        } catch (Exception e) {
              e.printStackTrace();
              return null;
        }
	}
	
}
