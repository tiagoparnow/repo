package com.amazingbookstore.dao;

import com.amazingbookstore.model.CarrinhoCompra;

public class CarrinhoCompraDAO extends GenericDAO<CarrinhoCompra> {

	private static final long serialVersionUID = 1L;

	public CarrinhoCompraDAO() {
		super(CarrinhoCompra.class);
	}

	public CarrinhoCompra inserirCarrinho(CarrinhoCompra carrinhoCompra) {
        try {
              save(carrinhoCompra);
              return carrinhoCompra;
        } catch (Exception e) {
              e.printStackTrace();
              return null;
        }
	}
	
	public CarrinhoCompra alterarCarrinho(CarrinhoCompra carrinhoCompra) {
        try {
              update(carrinhoCompra);
              return carrinhoCompra;
        } catch (Exception e) {
              e.printStackTrace();
              return null;
        }
	}
	
}
