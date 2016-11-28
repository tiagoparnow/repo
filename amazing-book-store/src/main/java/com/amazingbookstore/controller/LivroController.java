package com.amazingbookstore.controller;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.collections4.CollectionUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CloseEvent;

import com.amazingbookstore.dao.LivroDAO;
import com.amazingbookstore.model.Livro;

@RequestScoped
@ManagedBean
public class LivroController extends AbstractController {

	private Integer codigo;
	private String titulo;
	private List<Livro> livros;
	
	@ManagedProperty(value = LivroDetalheController.INJECTION_NAME)
    private LivroDetalheController livroDetalheController;
	
	@PostConstruct
    public void init() {
		this.listarLivros();
    }
	
	public LivroDetalheController getLivroDetalheController() {
		return livroDetalheController;
	}

	public void setLivroDetalheController(LivroDetalheController livroDetalheController) {
		this.livroDetalheController = livroDetalheController;
	}
	
	public void handleClose(CloseEvent event) {
        addMessage(event.getComponent().getId() + " closed", "So you don't like nature?");
    }
	
	public void addMessage(String summary, String detail) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
	
	public void listar() {
		this.setLivros(new LivroDAO().list(codigo, titulo));
	}
	
	public void selecionarLivroFromDialog(Livro livro) {
        RequestContext.getCurrentInstance().closeDialog(livro);
    }
	
	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public List<Livro> getLivros() {
		return livros;
	}

	public void setLivros(List<Livro> livros) {
		this.livros = livros;
	}
	
	public void selecionar() {
		if (!CollectionUtils.isEmpty(livros)) {
			RequestContext.getCurrentInstance().execute("PF('dlg').hide()");
		}
	}
	
	public void listarLivros() {
		setLivros(new LivroDAO().findAll());
	}

	public String detalharLivro(Livro livro) {
		livroDetalheController.setLivro(livro);
		return "detalhe-livro.xhtml?faces-redirect=true";
	}
}
