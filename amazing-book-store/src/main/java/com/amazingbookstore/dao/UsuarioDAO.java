package com.amazingbookstore.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.amazingbookstore.model.Usuario;
import com.amazingbookstore.model.Usuario_;
import com.amazingbookstore.repository.EntityRepository;
import com.amazingbookstore.repository.Repository;
import com.amazingbookstore.util.Constantes;

@Repository(entityManager = "amazingBookStore")
public class UsuarioDAO extends EntityRepository<Usuario, Integer> implements Constantes {

	public Usuario getUsuario(String eMail, String senha) {
		final CriteriaQuery<Usuario> query = super.cb().createQuery(Usuario.class);
		final Root<Usuario> usuario = query.from(Usuario.class);
		final List<Predicate> where = new ArrayList<>();
		
		where.add(super.cb().equal(usuario.get(Usuario_.eMail), eMail));
		where.add(super.cb().equal(usuario.get(Usuario_.senha), senha));
		
		query.where(where.stream().toArray(Predicate[]::new));
		
		return this.findOne(query);
		
		
	}


}
