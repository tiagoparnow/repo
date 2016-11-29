package com.amazingbookstore.model;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the livro database table.
 * 
 */
@Entity
@Table(name ="livro", schema="site_mysql")
@NamedQueries({ @NamedQuery(name="Livro.findAll", query=" SELECT l "
													  + " FROM Livro l "),
				@NamedQuery(name="Livro.findByIdTitulo", query=" SELECT l "
													  + " FROM Livro l "
													  + " WHERE l.idLivro = :idLivro "
													  + " AND l.titulo LIKE :titulo "),
				@NamedQuery(name="Livro.findByTitulo", query=" SELECT l "
						  + " FROM Livro l "
						  + " WHERE l.titulo LIKE :titulo "),
			  })

public class Livro implements Serializable {
	private static final long serialVersionUID = 1L;

	@Transient
	public static final String FIND_ALL = "Livro.findAll";
	@Transient
	public static final String FIND_BY_ID_TITULO = "Livro.findByIdTitulo";
	@Transient
	public static final String FIND_BY_TITULO = "Livro.findByTitulo";
	
	@Id
	@SequenceGenerator(name="LIVRO_IDLIVRO_GENERATOR" )
	@GeneratedValue(strategy=GenerationType.AUTO, generator="LIVRO_IDLIVRO_GENERATOR")
	@Column(name="id_livro")
	private Integer idLivro;

	private String autor;

	@Temporal(TemporalType.DATE)
	@Column(name="data_publicacao")
	private Date dataPublicacao;

	private String detalhes;

	private String imagem;

	private Integer quantidade;

	private String titulo;

	private BigDecimal valor;

	//bi-directional many-to-one association to Item
	@OneToMany(mappedBy="id.livro")
	private List<Item> items;

	public Livro() {
	}

	public Integer getIdLivro() {
		return this.idLivro;
	}

	public void setIdLivro(Integer idLivro) {
		this.idLivro = idLivro;
	}

	public String getAutor() {
		return this.autor;
	}

	public void setAutor(String autor) {
		this.autor = autor;
	}

	public Date getDataPublicacao() {
		return this.dataPublicacao;
	}

	public void setDataPublicacao(Date dataPublicacao) {
		this.dataPublicacao = dataPublicacao;
	}

	public String getDetalhes() {
		return this.detalhes;
	}

	public void setDetalhes(String detalhes) {
		this.detalhes = detalhes;
	}

	public String getImagem() {
		return this.imagem;
	}

	public void setImagem(String imagem) {
		this.imagem = imagem;
	}

	public Integer getQuantidade() {
		return this.quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public String getTitulo() {
		return this.titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public BigDecimal getValor() {
		return this.valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public List<Item> getItems() {
		return this.items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}

	public Item addItem(Item item) {
		getItems().add(item);
		item.getId().setLivro(this);

		return item;
	}

	public Item removeItem(Item item) {
		getItems().remove(item);
		item.getId().setLivro(null);

		return item;
	}

}