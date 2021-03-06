package com.amazingbookstore.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


/**
 * The persistent class for the carrinho_compras database table.
 * 
 */
@Entity
@Table(name="carrinho_compras", schema="site_mysql")
@NamedQuery(name="CarrinhoCompra.findAll", query="SELECT c FROM CarrinhoCompra c")
public class CarrinhoCompra implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="CARRINHO_COMPRAS_IDCARRINHOCOMPRAS_GENERATOR" )
	@GeneratedValue(strategy=GenerationType.AUTO, generator="CARRINHO_COMPRAS_IDCARRINHOCOMPRAS_GENERATOR")
	@Column(name="id_carrinho_compras")
	private Integer idCarrinhoCompras;

	@Column(name="quantidade_total")
	private Integer quantidadeTotal;

	@Column(name="valor_total")
	private BigDecimal valorTotal;

	//bi-directional many-to-one association to Item
	@OneToMany(mappedBy="id.carrinhoCompra")
	private List<Item> items;

	//bi-directional many-to-one association to Usuario
	@OneToMany(mappedBy="carrinhoCompra")
	private List<Usuario> usuarios;

	public CarrinhoCompra() {
	}

	public Integer getIdCarrinhoCompras() {
		return this.idCarrinhoCompras;
	}

	public void setIdCarrinhoCompras(Integer idCarrinhoCompras) {
		this.idCarrinhoCompras = idCarrinhoCompras;
	}

	public Integer getQuantidadeTotal() {
		return this.quantidadeTotal;
	}

	public void setQuantidadeTotal(Integer quantidadeTotal) {
		this.quantidadeTotal = quantidadeTotal;
	}

	public BigDecimal getValorTotal() {
		return this.valorTotal;
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}

	public List<Item> getItems() {
		return this.items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}

	public Item addItem(Item item) {
		getItems().add(item);
		item.getId().setCarrinhoCompra(this);

		return item;
	}

	public Item removeItem(Item item) {
		getItems().remove(item);
		item.getId().setCarrinhoCompra(null);

		return item;
	}

	public List<Usuario> getUsuarios() {
		return this.usuarios;
	}

	public void setUsuarios(List<Usuario> usuarios) {
		this.usuarios = usuarios;
	}

	public Usuario addUsuario(Usuario usuario) {
		getUsuarios().add(usuario);
		usuario.setCarrinhoCompra(this);

		return usuario;
	}

	public Usuario removeUsuario(Usuario usuario) {
		getUsuarios().remove(usuario);
		usuario.setCarrinhoCompra(null);

		return usuario;
	}

}