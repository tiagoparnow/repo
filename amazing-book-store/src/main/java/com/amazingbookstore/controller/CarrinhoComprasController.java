package com.amazingbookstore.controller;

import java.math.BigDecimal;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import com.amazingbookstore.dao.CarrinhoCompraDAO;
import com.amazingbookstore.dao.ItemDAO;
import com.amazingbookstore.model.CarrinhoCompra;
import com.amazingbookstore.model.Item;

@RequestScoped
@ManagedBean
public class CarrinhoComprasController extends AbstractController {

	@ManagedProperty(value = UsuarioController.INJECTION_NAME)
	private UsuarioController usuarioController;
	
	private CarrinhoCompra carrinho;
	
	public CarrinhoCompra getCarrinho() {
		return carrinho;
	}

	public void setCarrinho(CarrinhoCompra carrinho) {
		this.carrinho = carrinho;
	}
	
	public void setUsuarioController(UsuarioController usuarioController) {
        this.usuarioController = usuarioController;
    }
	
	@PostConstruct
	public void init() {
		if (usuarioController != null && usuarioController.getUser() != null
				&& usuarioController.getUser().getCarrinhoCompra() != null) {
			carrinho = usuarioController.getUser().getCarrinhoCompra();
			carrinho.setItems(new ItemDAO().listarItem(carrinho.getIdCarrinhoCompras()));
		}
	}

	public String exibirCarrinho() {
		return "carrinho-compras.xhtml?faces-redirect=true";
	}

	
	public void excluirItem(Item item) {
		if (item != null && usuarioController != null && usuarioController.getUser() != null
				&& usuarioController.getUser().getCarrinhoCompra() != null
				&& usuarioController.getUser().getCarrinhoCompra().getItems() != null) {
			usuarioController.getUser().getCarrinhoCompra().getItems().remove(item);
			new ItemDAO().excluirItem(item);
			if (usuarioController.getUser().getCarrinhoCompra().getItems() == null || usuarioController.getUser().getCarrinhoCompra().getItems().isEmpty()) {
				usuarioController.getUser().getCarrinhoCompra().setQuantidadeTotal(0);
				usuarioController.getUser().getCarrinhoCompra().setValorTotal(BigDecimal.ZERO);
			} else {
				usuarioController.getUser().getCarrinhoCompra().setQuantidadeTotal(usuarioController.getUser().getCarrinhoCompra().getQuantidadeTotal() - 1);
				usuarioController.getUser().getCarrinhoCompra().setValorTotal(usuarioController.getUser().getCarrinhoCompra().getValorTotal().subtract(item.getId().getLivro().getValor()));
			}
		}
		
		new CarrinhoCompraDAO().alterarCarrinho(usuarioController.getUser().getCarrinhoCompra());
		
		displayInfoMessage("Livro removido do carrinho");
	}
	
}
