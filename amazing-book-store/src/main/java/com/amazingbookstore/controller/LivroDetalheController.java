package com.amazingbookstore.controller;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import com.amazingbookstore.dao.CarrinhoCompraDAO;
import com.amazingbookstore.dao.UsuarioDAO;
import com.amazingbookstore.model.CarrinhoCompra;
import com.amazingbookstore.model.Item;
import com.amazingbookstore.model.Livro;
import com.amazingbookstore.model.Item.ItemPK;

@SessionScoped
@ManagedBean(name = "livroDetalheController")
public class LivroDetalheController extends AbstractController {
	public static final String INJECTION_NAME = "#{livroDetalheController}";
	private Livro livro;
	private Integer quantidade;
	
	@ManagedProperty(value = UsuarioController.INJECTION_NAME)
	private UsuarioController usuarioController;
	
	public void setUsuarioController(UsuarioController usuarioController) {
        this.usuarioController = usuarioController;
    }
	
	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}
	
	public Livro getLivro() {
		return livro;
	}
	
	public void setLivro(Livro livro) {
		this.livro = livro;
	}
	
	public void adicionarCarrinho() {
		if (usuarioController != null && usuarioController.getUser() != null
				&& usuarioController.getUser().getCarrinhoCompra() == null) {
			usuarioController.getUser().setCarrinhoCompra(new CarrinhoCompraDAO().inserirCarrinhoo(new CarrinhoCompra()));
			boolean atualizar = new UsuarioDAO().alterarUsuario(usuarioController.getUser());
			if (!atualizar) {
				displayErrorMessage("Ocorreu erro ao atualizar o usuário");
				return;
			}
		}
		
		ItemPK itemPK = new ItemPK();
		itemPK.setCarrinhoCompra(usuarioController.getUser().getCarrinhoCompra());
		itemPK.setLivro(livro);
		
		Item item = new Item();
		item.setQuantidade(quantidade);
		
	}

}
