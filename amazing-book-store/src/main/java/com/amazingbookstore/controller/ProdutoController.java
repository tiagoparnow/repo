package com.amazingbookstore.controller;

import java.util.HashMap;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

import com.amazingbookstore.dao.LivroDAO;
import com.amazingbookstore.model.Livro;

@RequestScoped
@ManagedBean(name = "produtoController")
public class ProdutoController extends AbstractController {

	public static final String INJECTION_NAME = "#{produtoController}";
	private Livro livro;
	
	public ProdutoController() {
		livro = new Livro();
	}
	
	public Livro getLivro() {
		return livro;
	}

	public void setLivro(Livro livro) {
		this.livro = livro;
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

	public void escolherLivro() {
        Map<String,Object> options = new HashMap<String, Object>();
        options.put("resizable", false);
        options.put("draggable", false);
        options.put("modal", true);
        RequestContext.getCurrentInstance().openDialog("selecionar-livro", options, null);
    }
	
	public void onLivroChosen(SelectEvent event) {
        Livro livro = (Livro) event.getObject();
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Car Selected", "Id:" + livro.getIdLivro());
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
	
}
