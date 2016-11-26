package com.amazingbookstore.model;

import java.io.Serializable;
import javax.persistence.*;


import java.util.Date;


/**
 * The persistent class for the usuario database table.
 * 
 */
@Entity
@NamedQueries({ @NamedQuery(name="Usuario.findAll", query=" SELECT u       "
														+ " FROM Usuario u "),
				@NamedQuery(name="Usuario.findByEmailSenha", query = " SELECT u FROM Usuario u "
																   + " WHERE u.eMail = :email  "
																   + " AND u.senha = :senha    "),
				@NamedQuery(name="Usuario.findByEmail", query = " SELECT u FROM Usuario u "
						   									  + " WHERE u.eMail = :email  "),
			  })
public class Usuario implements Serializable {
	private static final long serialVersionUID = 1L;

	@Transient
	public static final String FIND_ALL = "Usuario.findAll";
	@Transient
	public static final String FIND_BY_EMAIL_SENHA = "Usuario.findByEmailSenha";
	@Transient
	public static final String FIND_BY_EMAIL = "Usuario.findByEmail";
	
	@Id
	@SequenceGenerator(name="USUARIO_IDUSUARIO_GENERATOR" )
	@GeneratedValue(strategy=GenerationType.AUTO, generator="USUARIO_IDUSUARIO_GENERATOR")
	@Column(name="id_usuario")
	private int idUsuario;

	private String nome;
	
	@Temporal(TemporalType.DATE)
	@Column(name="data_inclusao")
	private Date dataInclusao;

	@Column(name="e_mail")
	private String eMail;

	private String perfil;

	private String senha;

	//bi-directional many-to-one association to CarrinhoCompra
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_carrinho_compras")
	private CarrinhoCompra carrinhoCompra;

	public Usuario() {
	}

	public int getIdUsuario() {
		return this.idUsuario;
	}

	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Date getDataInclusao() {
		return this.dataInclusao;
	}

	public void setDataInclusao(Date dataInclusao) {
		this.dataInclusao = dataInclusao;
	}

	public String getEMail() {
		return this.eMail;
	}

	public void setEMail(String eMail) {
		this.eMail = eMail;
	}

	public String getPerfil() {
		return this.perfil;
	}

	public void setPerfil(String perfil) {
		this.perfil = perfil;
	}

	public String getSenha() {
		return this.senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public CarrinhoCompra getCarrinhoCompra() {
		return this.carrinhoCompra;
	}

	public void setCarrinhoCompra(CarrinhoCompra carrinhoCompra) {
		this.carrinhoCompra = carrinhoCompra;
	}
	
	public boolean isAdmin(){
        return Role.ADMIN.equals(perfil);
    }
    
    public boolean isUser(){
        return Role.USER.equals(perfil);
    }
	
}