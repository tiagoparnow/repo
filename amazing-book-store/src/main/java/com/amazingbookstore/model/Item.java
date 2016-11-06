package com.amazingbookstore.model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the item database table.
 * 
 */
@Entity
@NamedQuery(name="Item.findAll", query="SELECT i FROM Item i")
public class Item implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private ItemPK id;

	private int quantidade;

	//bi-directional many-to-one association to CarrinhoCompra
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_carrinho_compras")
	private CarrinhoCompra carrinhoCompra;

	//bi-directional many-to-one association to Livro
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_livro")
	private Livro livro;

	public Item() {
	}

	public ItemPK getId() {
		return this.id;
	}

	public void setId(ItemPK id) {
		this.id = id;
	}

	public int getQuantidade() {
		return this.quantidade;
	}

	public void setQuantidade(int quantidade) {
		this.quantidade = quantidade;
	}

	public CarrinhoCompra getCarrinhoCompra() {
		return this.carrinhoCompra;
	}

	public void setCarrinhoCompra(CarrinhoCompra carrinhoCompra) {
		this.carrinhoCompra = carrinhoCompra;
	}

	public Livro getLivro() {
		return this.livro;
	}

	public void setLivro(Livro livro) {
		this.livro = livro;
	}

}