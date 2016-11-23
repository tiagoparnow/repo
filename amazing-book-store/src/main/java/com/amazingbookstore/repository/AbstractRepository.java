package com.amazingbookstore.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.ejb.HibernateQuery;

import com.amazingbookstore.domain.Page;
import com.amazingbookstore.domain.Pageable;
import com.amazingbookstore.persistence.DefaultIntegrator;


public abstract class AbstractRepository<Entity, PK extends Serializable> {

	protected abstract EntityManager em();

	protected abstract Class<Entity> entityClass();
	

	/*
	 *  Utils
	 */
	protected CriteriaBuilder cb(){
		return this.em().getCriteriaBuilder();
	}
	
	protected <T> TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery){
		return this.em().createQuery(criteriaQuery);
	}	
	
	public CriteriaQuery<Object> createCriteriaQuery(){
		return this.cb().createQuery();
	}

	public <T> CriteriaQuery<T> createCriteriaQuery(Class<T> resultClass){
		return this.cb().createQuery(resultClass);
	}

	public CriteriaQuery<Tuple> createCriteriaTupleQuery(){
		return this.cb().createTupleQuery();
	}	

	public EntityQuery<Object> createEntityQuery(){
		return EntityQuery.newInstance(this.em());
	}
	
	public <T> EntityQuery<T> createEntityQuery(Class<T> resultClass){
		return EntityQuery.newInstance(resultClass, this.em());
	}

	public NativeQuery<Object> createNativeQuery(){
		return NativeQuery.newInstance(this.em());
	}
	
	public <T> NativeQuery<T> createNativeQuery(Class<T> resultClass){
		return NativeQuery.newInstance(resultClass, this.em());
	}
	
	public String formatTable(String key, String table){
		return DefaultIntegrator.formatTable(key, table);
	}

	public String formatTable(String key, String table, String alias) {
		return DefaultIntegrator.formatTable(key, table, alias);
	}
	
	
	
	
	
	/*
	 * Persistences
	 */
	public boolean isNew(Object entity){
		return this.em().getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entity) == null;
    }

	public Entity save(Entity entity){
        if (this.isNew(entity)){
        	this.em().persist(entity);
            return entity;
        }
        return this.em().merge(entity);
    }

	public Entity saveAndFlush(Entity entity){
        final Entity result = this.save(entity);
        this.flush();
        return result;
    }
	
	public Entity saveAndFlushAndClear(Entity entity){
    	final Entity result = this.saveAndFlush(entity);
        this.clear();
        return result;
    }

    public Entity saveAndFlushAndRefresh(Entity entity){
    	final Entity result = this.saveAndFlush(entity);
        this.refresh(result);
        return result;
    }

    public void refresh(Entity entity){
    	this.em().refresh(entity);
    }
    
    public void clear(){
    	this.em().clear();
    }
    
    public void flush(){
    	this.em().flush();
    }
    
	public void remove(Entity entity){
		 this.em().remove(entity);
    }

    public void removeAndFlush(Entity entity){
    	this.em().remove(entity);
    	this. flush();
    }
    
    public Entity findBy(PK primaryKey){
		return this.em().find(entityClass(), primaryKey);
	}
	
	public Entity findBy(PK primaryKey, LockModeType lockMode){
		return this.em().find(entityClass(), primaryKey, lockMode);
	}	
	
    public List<Entity> findAll(){
    	final CriteriaQuery<Entity> query = this.createCriteriaQuery(entityClass());
    	query.from(entityClass());
        return this.getResultList(query);
    }

    public List<Entity> findAll(Iterable<PK> keys){
    	final ArrayList<Entity> list = new ArrayList<Entity>();
    	for(PK primaryKey : keys){
    		final Entity entity = this.findBy(primaryKey);
    		if(entity != null){
    			list.add(entity);
    		}
    	}
    	return list;
    }    

    public Page<Entity> findAll(Pageable pageable){
    	final CriteriaQuery<Entity> query = this.createCriteriaQuery(entityClass());
    	query.from(entityClass());
        return this.pager(query, pageable);
    }

    

	
	/*
	 * Pager
	 */	
    public <T> Page<T> pager(CriteriaQuery<T> query, Pageable pageable) {	
    	return this.pager(query, pageable, null);
    }
    
	public <T> Page<T> pager(CriteriaQuery<T> query, com.amazingbookstore.domain.Pageable pageable, Collection<Predicate> where) {	
		final List<T> list = this.getResultList(query, pageable, where);
		if(pageable == null){
			return new Page<T>(list);
		}

		return new Page<T>(list, pageable, list.size() > 0 && list.size() < pageable.getSize() ? pageable.getOffset() + list.size() : this.getCount(query));
	}

	public <T> Page<T> pager(AbstractQuery<T> query, Pageable pageable) {
		final List<T> list = this.getResultList(query, pageable);
		if(pageable == null){
			return new Page<T>(list);
		}

		return new Page<T>(list, pageable, list.size() > 0 && list.size() < pageable.getSize() ? pageable.getOffset() + list.size() : this.getCount(query));
	}

	

	/*
	 * Scroll 
	 */	
	public ScrollableResults scroll(CriteriaQuery<?> query) {
		return this.scroll(query, null, null);
	}
	
	public ScrollableResults scroll(CriteriaQuery<?> query, ScrollMode mode){
		return this.scroll(query, mode, null);
	}

	public ScrollableResults scroll(CriteriaQuery<?> query, Collection<Predicate> where) {
		return this.scroll(query, null, where);
	}
	
	public ScrollableResults scroll(CriteriaQuery<?> query, ScrollMode mode, Collection<Predicate> where) {
		if(where != null && !where.isEmpty()){
			query.where(where.stream().toArray(Predicate[]::new));
		}
		
		final TypedQuery<?> typedQuery = this.em().createQuery(query);
		return mode != null ? typedQuery.unwrap(HibernateQuery.class).getHibernateQuery().scroll(mode) : typedQuery.unwrap(HibernateQuery.class).getHibernateQuery().scroll();
	}
	
	
	public ScrollableResults scroll(AbstractQuery<?> query){
		return query.scroll();
	}
	
	public ScrollableResults scroll(AbstractQuery<?> query, ScrollMode mode){
		return query.scroll(mode);
	}
	
	
	
	/*
	 * Result List 
	 */
	public <T> List<T> getResultList(CriteriaQuery<T> query) {		
		return this.getResultList(query, null, null);
	}
	
	public <T> List<T> getResultList(CriteriaQuery<T> query, Pageable pageable) {
		return this.getResultList(query, pageable, null);
	}
	
	public <T> List<T> getResultList(CriteriaQuery<T> query, Collection<Predicate> where) {
		return this.getResultList(query, null, where);
	}
	
	public <T> List<T> getResultList(CriteriaQuery<T> query, Pageable pageable, Collection<Predicate> where) {
		if(where != null && !where.isEmpty()){
			query.where(where.stream().toArray(Predicate[]::new));
		}
		
		final TypedQuery<T> typedQuery = this.em().createQuery(query);
		if(pageable != null){
			typedQuery.setMaxResults(pageable.getSize());
			typedQuery.setFirstResult(pageable.getOffset());
		}
		return typedQuery.getResultList();
	}
	
	
	public <T> List<T> getResultList(AbstractQuery<T> query){
		return this.getResultList(query, null);
	}
	
	public <T> List<T> getResultList(AbstractQuery<T> query, Pageable pageable){
		if(pageable != null){
			query.setMaxResults(pageable.getSize());
			query.setFirstResult(pageable.getOffset());
		}
		return query.getResultList();
	}

	
	/*
	 * Count
	 */	
	public Long getCount(CriteriaQuery<?> criteria) {
		return this.getCount(criteria, null);
	}
	
	public Long getCount(CriteriaQuery<?> criteria, Collection<Predicate> where) {
		final CriteriaQuery<Long> query = this.createCountByCriteria(criteria);
		if(where != null && !where.isEmpty()){
			query.where(where.stream().toArray(Predicate[]::new));
		}
		return this.em().createQuery(query).getSingleResult();
	}
	
	public Long getCount(AbstractQuery<?>  query){
		return query.getResultCount();
	}



	/*
	 * Find One
	 */
	public <T> T findOne(CriteriaQuery<T> query) {
		return this.findOne(query, null);
	}
	
	public <T> T findOne(CriteriaQuery<T> query, Collection<Predicate> where) {	
		if(where != null && !where.isEmpty()){
			query.where(where.stream().toArray(Predicate[]::new));
		}
		
		final List<T> list = this.createQuery(query).setFirstResult(0).setMaxResults(1).getResultList();
		return list.isEmpty() ? null : list.get(0);
	}

	public <T> T findOne(AbstractQuery<T> query){
		final List<T> list = query.setFirstResult(0).setMaxResults(1).getResultList();
		return list.isEmpty() ? null : list.get(0);
	}

	

	/*
	 * Privados 
	 */	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private CriteriaQuery<Long> createCountByCriteria(CriteriaQuery<?> criteria){
		final CriteriaBuilder builder = this.em().getCriteriaBuilder();
		final CriteriaQuery<Long> query = builder.createQuery(Long.class);

		if(criteria.getGroupRestriction() != null || (criteria.getGroupList() != null && criteria.getGroupList().size() > 0)){
			final Subquery subquery = query.subquery(findRoot(criteria).getJavaType());

			for (Root root : criteria.getRoots()) {
				final Root<?> dest = subquery.from(root.getJavaType());
				dest.alias(root.getAlias());
				copyJoins(root, dest);			
			}

			subquery.select(findRoot(subquery));
			subquery.groupBy(criteria.getGroupList());
			subquery.distinct(criteria.isDistinct());
			
			if(criteria.getGroupRestriction() != null){
				subquery.having(criteria.getGroupRestriction());
			}
			
			if(criteria.getRestriction() != null){
				subquery.where(criteria.getRestriction());
			}		
			
			final Root<?> root = query.from(findRoot(criteria).getJavaType());
			query.select(builder.count(root));
			query.where(builder.in(root).value(subquery));
		
		}else{
			
			for (Root root : criteria.getRoots()) {
				final Root<?> dest = query.from(root.getJavaType());
				dest.alias(root.getAlias());
				copyJoins(root, dest);			
			}
			
			query.groupBy(criteria.getGroupList());
			query.distinct(criteria.isDistinct());

			if(criteria.getGroupRestriction() != null){
				query.having(criteria.getGroupRestriction());
			}
			
			if(criteria.getRestriction() != null){
				query.where(criteria.getRestriction());
			}
			
			query.select(builder.count(findRoot(criteria)));
		}			
			
		return query;
	}
	
	private void copyJoins(From<?, ?> from, From<?, ?> to) {
		for (Join<?, ?> j : from.getJoins()) {
			final Join<?, ?> toJoin = to.join(j.getAttribute().getName(), j.getJoinType());
			toJoin.alias(j.getAlias());
			this.copyJoins(j, toJoin);
		}

		for (Fetch<?, ?> f : from.getFetches()) {
			final Fetch<?, ?> toFetch = to.fetch(f.getAttribute().getName());
			this.copyFetches(f, toFetch);
		}
	}
	
	private void copyFetches(Fetch<?, ?> from, Fetch<?, ?> to) {
		for (Fetch<?, ?> f : from.getFetches()) {
			final Fetch<?, ?> toFetch = to.fetch(f.getAttribute().getName());
			this.copyFetches(f, toFetch);
		}
	}
	
	private <T> Root<T> findRoot(javax.persistence.criteria.AbstractQuery<T> query) {
		return this.findRoot(query, query.getResultType());
	}
	
	@SuppressWarnings("unchecked")
	private <T> Root<T> findRoot(javax.persistence.criteria.AbstractQuery<?> query, Class<T> clazz) {
		for (Root<?> r : query.getRoots()) {
			if (clazz.equals(r.getJavaType())) {
				return (Root<T>) r.as(clazz);
			}
		}
		return (Root<T>) query.getRoots().iterator().next();
	}

}