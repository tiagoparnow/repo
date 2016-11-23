package com.amazingbookstore.repository;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.amazingbookstore.persistence.EntityManagerResolverLiteral;
import com.amazingbookstore.util.ProxyUtils;


public abstract class EntityRepository<E, PK extends Serializable> extends AbstractRepository<E, PK> {

	@Inject @Any
	private Instance<EntityManager> entityManager;

	private Repository repository;

	private Class<E> entityClass;
	
	public EntityRepository(){
		this.init();
	}

	@SuppressWarnings("unchecked")
	private void init(){
		repository = ProxyUtils.getUnproxiedClass(this.getClass()).getAnnotation(Repository.class);
		if(repository == null){
			throw new IllegalArgumentException("no found annotation @Repository");
		}

		entityClass =  (Class<E>) (repository.forEntity() != Object.class ? repository.forEntity() : ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
	}


	public EntityManager em(){
		return entityManager.select(new EntityManagerResolverLiteral(repository.entityManager())).get();
	}

	@Override
	public Class<E> entityClass() {
		return entityClass;
	}

}