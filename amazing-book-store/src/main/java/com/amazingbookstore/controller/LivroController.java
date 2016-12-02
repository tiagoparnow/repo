package com.amazingbookstore.controller;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

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
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
		titulo = (String) request.getSession().getAttribute("titulo");
		request.getSession().removeAttribute("titulo");
		this.listarLivros();
    }
	
	public LivroDetalheController getLivroDetalheController() {
		return livroDetalheController;
	}

	public void setLivroDetalheController(LivroDetalheController livroDetalheController) {
		this.livroDetalheController = livroDetalheController;
	}
	
	public void listar() {
		this.setLivros(new LivroDAO().list(codigo, titulo));
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
	
	public void listarLivros() {
		setLivros(new LivroDAO().list(codigo, titulo));
	}

	public String detalharLivro(Livro livro) {
		livroDetalheController.setLivro(livro);
		return "detalhe-livro.xhtml?faces-redirect=true";
	}
	
	public String consultarLivro() {
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
		request.getSession().setAttribute("titulo", titulo);
		return "listagem-livros.xhtml?faces-redirect=true";
	}
}
