package com.amazingbookstore.model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the item database table.
 * 
 */
@Embeddable
public class ItemPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="id_item")
	private int idItem;

	@Column(name="id_carrinho_compras", insertable=false, updatable=false)
	private int idCarrinhoCompras;

	@Column(name="id_livro", insertable=false, updatable=false)
	private int idLivro;

	public ItemPK() {
	}
	public int getIdItem() {
		return this.idItem;
	}
	public void setIdItem(int idItem) {
		this.idItem = idItem;
	}
	public int getIdCarrinhoCompras() {
		return this.idCarrinhoCompras;
	}
	public void setIdCarrinhoCompras(int idCarrinhoCompras) {
		this.idCarrinhoCompras = idCarrinhoCompras;
	}
	public int getIdLivro() {
		return this.idLivro;
	}
	public void setIdLivro(int idLivro) {
		this.idLivro = idLivro;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof ItemPK)) {
			return false;
		}
		ItemPK castOther = (ItemPK)other;
		return 
			(this.idItem == castOther.idItem)
			&& (this.idCarrinhoCompras == castOther.idCarrinhoCompras)
			&& (this.idLivro == castOther.idLivro);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.idItem;
		hash = hash * prime + this.idCarrinhoCompras;
		hash = hash * prime + this.idLivro;
		
		return hash;
	}
}