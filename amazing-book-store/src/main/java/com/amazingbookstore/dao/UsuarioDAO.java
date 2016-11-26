package com.amazingbookstore.dao;

import javax.persistence.NoResultException;

import com.amazingbookstore.model.Usuario;
import com.amazingbookstore.util.Constantes;

public class UsuarioDAO extends GenericDAO<Usuario> implements Constantes {
	
	private static final long serialVersionUID = 1L;

	public UsuarioDAO() {
		super(Usuario.class);
	}

	public Usuario findByEmail(String email){
		try {
			return createNamedQuery(Usuario.FIND_BY_EMAIL)
					 .setParameter("email", email)
					 .getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	public Usuario findByEmailSenha(String email, String senha){
		try {
			return createNamedQuery(Usuario.FIND_BY_EMAIL_SENHA)
					 .setParameter("email", email)
					 .setParameter("senha", senha)
					 .getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	 
	public boolean inserirUsuario(Usuario usuario) {
        try {
              save(usuario);
              return true;
        } catch (Exception e) {
              e.printStackTrace();
              return false;
        }
	}
	
	public boolean alterarUsuario(Usuario usuario) {
        try {
              update(usuario);
              return true;
        } catch (Exception e) {
              e.printStackTrace();
              return false;
        }
	}
	

}
