package com.amazingbookstore.controller;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import com.amazingbookstore.dao.LivroDAO;
import com.amazingbookstore.model.Livro;

@RequestScoped
@ManagedBean(name = "produtoController")
public class ProdutoController extends AbstractController {

	public static final String INJECTION_NAME = "#{produtoController}";
	private Livro livro;
	private Livro livroSelecionado;
	
	public ProdutoController() {
		livro = new Livro();
	}
	
	public Livro getLivro() {
		return livro;
	}

	public void setLivro(Livro livro) {
		this.livro = livro;
	}

	public Livro getLivroSelecionado() {
		return livroSelecionado;
	}

	public void setLivroSelecionado(Livro livroSelecionado) {
		this.livroSelecionado = livroSelecionado;
	}
	
	public void manterLivro() {
		boolean sucesso = false;
		if (livro.getIdLivro() == null) {
			sucesso = new LivroDAO().inserirLivro(livro);
		} else {
			sucesso = new LivroDAO().alterarLivro(livro);
		}
		
		if (!sucesso) {
			displayErrorMessage("Ocorreu erro ao salvar produto");
			return;
		}
		displayInfoMessage("Produto salvo com sucesso");
	}
	
	public void removerLivro() {
		if (!new LivroDAO().excluirLivro(livro)) {
			displayErrorMessage("Ocorreu erro ao remover produto");
			return;
		}
		this.setLivro(null);
		displayInfoMessage("Produto removido com sucesso");
	}

	public void selecionarLivro() {
		if (livroSelecionado != null) {
			setLivro(livroSelecionado);
		}
	}
	
}
