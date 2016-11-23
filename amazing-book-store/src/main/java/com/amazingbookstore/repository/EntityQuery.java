package com.amazingbookstore.repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.ejb.HibernateQuery;
import org.hibernate.transform.ResultTransformer;

public class EntityQuery<E> extends AbstractQuery<E> {

	private EntityManager entityManager;
	private Class<E> resultClass;
	
	private Map<String, String> hints;
	private Boolean cacheable;
	private String cacheRegion;
	private Integer fetchSize;
	private ResultTransformer resultTransformer;
	private Integer timeout;


	protected static EntityQuery<Object> newInstance(EntityManager entityManager) {
		return new EntityQuery<Object>(Object.class, entityManager);
	}

	protected static <T> EntityQuery<T> newInstance(Class<T> resultClass, EntityManager entityManager) {
		return new EntityQuery<T>(resultClass, entityManager);
	}

	private EntityQuery(Class<E> resultClass, EntityManager entityManager) {
		this.entityManager = entityManager;
		this.resultClass = resultClass;
	}

	@Override
	protected List<E> getResultList() {
		return (List<E>) this.createQuery().getResultList();
	}

	@Override
	protected Long getResultCount() {
		return (Long) this.createCountQuery().getSingleResult();
	}

	@Override
	protected ScrollableResults scroll() {
		return this.createQuery().unwrap(HibernateQuery.class).getHibernateQuery().scroll();
	}
	
	@Override
	public ScrollableResults scroll(ScrollMode mode) {
		return this.createQuery().unwrap(HibernateQuery.class).getHibernateQuery().scroll(mode);
	}
	

	protected TypedQuery<E> createQuery() {
		super.parseQuery();

		final TypedQuery<E> query = this.entityManager.createQuery(super.getRenderedQuery(), this.resultClass);
		this.setParameters(query, super.getParsedParameters(), 0);
		this.setParameters(query, super.getParameters());

		if (this.getFirstResult() != null) {
			query.setFirstResult(this.getFirstResult());
		}

		if (this.getMaxResults() != null) {
			query.setMaxResults(this.getMaxResults());
		}

		if (this.getFetchSize() != null) {
			query.unwrap(HibernateQuery.class).getHibernateQuery().setFetchSize(this.getFetchSize());
		}

		if (this.getCacheable() != null) {
			query.unwrap(HibernateQuery.class).getHibernateQuery().setCacheable(this.getCacheable());
		}

		if (this.getCacheRegion() != null) {
			query.unwrap(HibernateQuery.class).getHibernateQuery().setCacheRegion(this.getCacheRegion());
		}

		if (this.getResultTransformer() != null) {
			query.unwrap(HibernateQuery.class).getHibernateQuery().setResultTransformer(this.getResultTransformer());
		}

		if (this.getTimeout() != null) {
			query.unwrap(HibernateQuery.class).getHibernateQuery().setTimeout(this.getTimeout());
		}

		for (Entry<String, String> me : this.getHints() != null ? this.getHints().entrySet() : Collections.<Entry<String, String>>emptySet()) {
			query.setHint(me.getKey(), me.getValue());
		}

		return query;
	}

	protected Query createCountQuery() {
		super.parseQuery();

		final Query query = this.entityManager.createQuery(super.getCountQuery());
		this.setParameters(query, super.getParsedParameters(), 0);
		this.setParameters(query, super.getParameters());
		return query;
	}

	private void setParameters(Query query, List<Object> parameters, int start) {
		for (int i = 0; i < parameters.size(); i++) {
			query.setParameter(QueryParser.getParameterName(start + i), parameters.get(i));
		}
	}
	
	private void setParameters(Query query, Map<String, Object> parameters) {
		parameters.forEach((name, value) -> query.setParameter(name, value));
	}

	public Map<String, String> getHints() {
		return hints;
	}

	public void setHints(Map<String, String> hints) {
		this.hints = hints;
	}

	public Integer getFetchSize() {
		return fetchSize;
	}

	public void setFetchSize(Integer fetchSize) {
		this.fetchSize = fetchSize;
	}

	public Boolean getCacheable() {
		return cacheable;
	}

	public void setCacheable(Boolean cacheable) {
		this.cacheable = cacheable;
	}

	public String getCacheRegion() {
		return cacheRegion;
	}

	public void setCacheRegion(String cacheRegion) {
		this.cacheRegion = cacheRegion;
	}

	public ResultTransformer getResultTransformer() {
		return resultTransformer;
	}

	public void setResultTransformer(ResultTransformer resultTransformer) {
		this.resultTransformer = resultTransformer;
	}

	public Integer getTimeout() {
		return timeout;
	}

	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}

}