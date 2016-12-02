package com.amazingbookstore.dao;

import java.util.Collections;
import java.util.List;

import javax.persistence.NoResultException;

import org.apache.commons.lang3.StringUtils;

import com.amazingbookstore.model.Livro;

public class LivroDAO extends GenericDAO<Livro> {

	private static final long serialVersionUID = 1L;

	public LivroDAO() {
		super(Livro.class);
	}

	public boolean inserirLivro(Livro livro) {
        try {
              save(livro);
              return true;
        } catch (Exception e) {
              e.printStackTrace();
              return false;
        }
	}
	
	public boolean alterarLivro(Livro livro) {
        try {
              update(livro);
              return true;
        } catch (Exception e) {
              e.printStackTrace();
              return false;
        }
	}
	
	public boolean excluirLivro(Livro livro) {
        try {
              delete(livro.getIdLivro(), Livro.class);
              return true;
        } catch (Exception e) {
              e.printStackTrace();
              return false;
        }
	}
	
	public List<Livro> list(Integer codigo, String titulo) {
		try {
			if (codigo == null && StringUtils.isEmpty(titulo)) {
				return findAll();
			}
			
			if (codigo != null && StringUtils.isNotEmpty(titulo)) {
				return createNamedQuery(Livro.FIND_BY_ID_TITULO)
						 .setParameter("idLivro", codigo)
						 .setParameter("titulo", "%" + titulo + "%")
						 .getResultList();
			}
			
			if (codigo != null) {
				Livro livro = findById(codigo);
				return Collections.singletonList(livro);
			}
			
			if (StringUtils.isNotEmpty(titulo)) {
				return createNamedQuery(Livro.FIND_BY_TITULO)
						 .setParameter("titulo", "%" + titulo + "%")
						 .getResultList();
			}
			
			return null;
		} catch (NoResultException e) {
			return null;
		}
	}
	
}
