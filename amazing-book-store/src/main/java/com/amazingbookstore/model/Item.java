package com.amazingbookstore.model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the item database table.
 * 
 */
@Entity
@NamedQueries({
				@NamedQuery(name="Item.findAll", query="SELECT i FROM Item i"),
			  })
public class Item implements Serializable {
	private static final long serialVersionUID = 1L;

	@Transient
	public static final String FIND_ALL = "Item.findAll";
	
	@EmbeddedId
	private ItemPK id;

	private Integer quantidade;

	public Item() {
	}

	public Integer getQuantidade() {
		return this.quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public ItemPK getId() {
		return id;
	}

	public void setId(ItemPK id) {
		this.id = id;
	}

	@Embeddable
	public static class ItemPK implements Serializable {

		private static final long serialVersionUID = 1L;
		
		@Column(name="id_item")
		private Integer idItem;
		
		@ManyToOne(fetch=FetchType.LAZY)
		@JoinColumn(name="id_carrinho_compras")
		private CarrinhoCompra carrinhoCompra;

		//bi-directional many-to-one association to Livro
		@ManyToOne(fetch=FetchType.LAZY)
		@JoinColumn(name="id_livro")
		private Livro livro;

		public ItemPK() {
		}
		public Integer getIdItem() {
			return this.idItem;
		}
		public void setIdItem(Integer idItem) {
			this.idItem = idItem;
		}
		
		public Livro getLivro() {
			return livro;
		}
		public void setLivro(Livro livro) {
			this.livro = livro;
		}
		public CarrinhoCompra getCarrinhoCompra() {
			return carrinhoCompra;
		}
		public void setCarrinhoCompra(CarrinhoCompra carrinhoCompra) {
			this.carrinhoCompra = carrinhoCompra;
		}
		
		@Override
		public int hashCode() {
			final Integer prime = 31;
			Integer result = 1;
			result = prime * result + ((carrinhoCompra == null) ? 0 : carrinhoCompra.hashCode());
			result = prime * result + idItem;
			result = prime * result + ((livro == null) ? 0 : livro.hashCode());
			return result;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ItemPK other = (ItemPK) obj;
			if (carrinhoCompra == null) {
				if (other.carrinhoCompra != null)
					return false;
			} else if (!carrinhoCompra.equals(other.carrinhoCompra))
				return false;
			if (idItem != other.idItem)
				return false;
			if (livro == null) {
				if (other.livro != null)
					return false;
			} else if (!livro.equals(other.livro))
				return false;
			return true;
		}

	}
	
}