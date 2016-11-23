package com.amazingbookstore.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.hibernate.LockMode;
import org.hibernate.SQLQuery;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.type.Type;

import com.amazingbookstore.persistence.LocalDateTimeType;
import com.amazingbookstore.persistence.LocalDateType;
import com.amazingbookstore.persistence.LocalTimeType;
import com.amazingbookstore.persistence.SimNaoType;

@SuppressWarnings({"unchecked", "unused"})
public class NativeQuery<E> extends AbstractQuery<E> {

	private EntityManager entityManager;
	private Class<E> resultClass;

	private Boolean cacheable;
	private String cacheRegion;
	private Integer fetchSize;
	private ResultTransformer resultTransformer;
	private Integer timeout;

	private List<ScalarType> scalars = new ArrayList<ScalarType>();
	private List<EntityType> entities = new ArrayList<EntityType>();
	private List<JoinType> joins = new ArrayList<JoinType>();

	
	protected static NativeQuery<Object> newInstance(EntityManager entityManager) {
		return new NativeQuery<Object>(Object.class, entityManager);
	}
	
	protected static <T> NativeQuery<T> newInstance(Class<T> resultClass, EntityManager entityManager) {
		return new NativeQuery<T>(resultClass, entityManager);
	}
	
	
	protected NativeQuery(Class<E> resultClass, EntityManager entityManager) {
		this.entityManager = entityManager;
		this.resultClass = resultClass;
	}

	@Override
	protected List<E> getResultList() {
		return this.createQuery().list();
	}

	@Override
	protected Long getResultCount() {
		return ((Number) this.createCountQuery().uniqueResult()).longValue();
	}

	@Override
	protected ScrollableResults scroll() {
		return this.createQuery().scroll();
	}
	
	@Override
	protected ScrollableResults scroll(ScrollMode mode) {
		return this.createQuery().scroll(mode);
	}

	protected SQLQuery createQuery() {
		super.parseQuery();

		final SQLQuery query = this.entityManager.unwrap(Session.class).createSQLQuery(super.getRenderedQuery());
		this.setParameters(query, super.getParsedParameters(), 0);
		this.setParameters(query, super.getParameters());

		if (this.getFirstResult() != null) {
			query.setFirstResult(this.getFirstResult());
		}

		if (this.getMaxResults() != null) {
			query.setMaxResults(this.getMaxResults());
		}

		if (this.getFetchSize() != null) {
			query.setFetchSize(this.getFetchSize());
		}

		if (this.getCacheable() != null) {
			query.setCacheable(this.getCacheable());
		}

		if (this.getCacheRegion() != null) {
			query.setCacheRegion(this.getCacheRegion());
		}

		if (this.getResultTransformer() != null) {
			query.setResultTransformer(this.getResultTransformer());
		}

		if (this.getTimeout() != null) {
			query.setTimeout(this.getTimeout());
		}

		for (final ScalarType scalar : this.scalars) {
			if (scalar.type != null) {
				query.addScalar(scalar.columnAlias, scalar.type);
			} else {
				query.addScalar(scalar.columnAlias);
			}
		}

		for (final EntityType entity : this.entities) {
			if (entity.tableAlias != null && entity.entityType != null && entity.lockMode != null) {
				query.addEntity(entity.tableAlias, entity.entityType, entity.lockMode);

			} else if (entity.tableAlias != null && entity.entityType != null) {
				query.addEntity(entity.tableAlias, entity.entityType);

			} else if (entity.entityType != null) {
				query.addEntity(entity.entityType);

			} else if (entity.tableAlias != null && entity.entityName != null && entity.lockMode != null) {
				query.addEntity(entity.tableAlias, entity.entityName, entity.lockMode);

			} else if (entity.tableAlias != null && entity.entityName != null) {
				query.addEntity(entity.tableAlias, entity.entityName);

			} else if (entity.entityName != null) {
				query.addEntity(entity.entityName);
			}
		}
		
		for (final JoinType join : this.joins) {
			if(join.path != null && join.lockMode != null){
				query.addJoin(join.tableAlias, join.path, join.lockMode);
			
			}else if(join.path != null){
				query.addJoin(join.tableAlias, join.path);
			
			}else if(join.ownerTableAlias != null || join.joinPropertyName != null){
				query.addJoin(join.tableAlias, join.ownerTableAlias, join.joinPropertyName);
			}
		}

		return query;
	}


	protected SQLQuery createCountQuery() {
		super.parseQuery();
		
		final StringBuilder builder = new StringBuilder("select count(*) from (");
		builder.append(super.getRenderedQuery(true, false));
		builder.append(") quant ");

		final SQLQuery query = this.entityManager.unwrap(Session.class).createSQLQuery(builder.toString());
		this.setParameters(query, super.getParsedParameters(), 0);
		this.setParameters(query, super.getParameters());
		return query;
	}

	private void setParameters(SQLQuery query, List<Object> parameters, int start) {
		for (int i = 0; i < parameters.size(); i++) {
			this.applyParameter(query, QueryParser.getParameterName(start + i), parameters.get(i));
		}
	}
	
	private void setParameters(SQLQuery query, Map<String, Object> parameters) {
		parameters.forEach((name, value) -> this.applyParameter(query, name, value));
	}
	
	private void applyParameter(SQLQuery query, String name, Object value){
		if(value instanceof LocalDate){
			query.setParameter(name, value, LocalDateType.INSTANCE);

		}else if(value instanceof LocalDateTime){
			query.setParameter(name, value, LocalDateTimeType.INSTANCE);

		}else if(value instanceof LocalTime){
			query.setParameter(name, value, LocalTimeType.INSTANCE);

		}else if(value instanceof Boolean){
			query.setParameter(name, value, SimNaoType.INSTANCE);

		}else if (value instanceof Collection<?>){
			final Type type = resolveType((Collection<?>) value);
			if(type != null){
				query.setParameterList(name, (Collection<?>) value, type);
			}else{
				query.setParameterList(name, (Collection<?>) value);
			}
			
		}else if (value instanceof Object[]){
			final Type type = resolveType((Object[]) value);
			if(type != null){
				query.setParameterList(name, (Object[]) value, type);
			}else{
				query.setParameterList(name, (Object[]) value);
			}
			
		}else{
			query.setParameter(name, value);
		}
	}
	
	private Type resolveType(Object[] vals){
		return this.resolveType(Arrays.asList(vals));
	}

	private Type resolveType(Collection<?> vals){		
		if(vals.stream().filter(v -> v instanceof LocalDate).count() == vals.size()){
			return LocalDateType.INSTANCE;
		}
		
		if(vals.stream().filter(v -> v instanceof LocalDateTime).count() == vals.size()){
			return LocalDateTimeType.INSTANCE;
		}
		
		if(vals.stream().filter(v -> v instanceof LocalTime).count() == vals.size()){
			return LocalTimeType.INSTANCE;
		}
		
		if(vals.stream().filter(v -> v instanceof Boolean).count() == vals.size()){
			return SimNaoType.INSTANCE;
		}
		
		return null;
	}
	
	public NativeQuery<E> addScalar(String columnAlias) {
		this.scalars.add(new ScalarType(columnAlias));
		return this;
	}

	public NativeQuery<E> addScalar(String columnAlias, Type type) {
		this.scalars.add(new ScalarType(columnAlias, type));
		return this;
	}

	public NativeQuery<E> addEntity(String entityName) {
		this.entities.add(new EntityType(null, entityName, null));
		return this;
	}

	public NativeQuery<E> addEntity(String tableAlias, String entityName) {
		this.entities.add(new EntityType(tableAlias, entityName, null));
		return this;
	}

	public NativeQuery<E> addEntity(String tableAlias, String entityName, LockMode lockMode) {
		this.entities.add(new EntityType(tableAlias, entityName, lockMode));
		return this;
	}

	public NativeQuery<E> addEntity(Class<?> entityType) {
		this.entities.add(new EntityType(null, entityType, null));
		return this;
	}

	public NativeQuery<E> addEntity(String tableAlias, Class<?> entityType) {
		this.entities.add(new EntityType(tableAlias, entityType, null));
		return this;
	}

	public NativeQuery<E> addEntity(String tableAlias, Class<?> entityType, LockMode lockMode) {
		this.entities.add(new EntityType(tableAlias, entityType, lockMode));
		return this;
	}
	
	public NativeQuery<E> addJoin(String tableAlias, String path) {
		this.joins.add(new JoinType(tableAlias, path));
		return this;
	}

	public NativeQuery<E> addJoin(String tableAlias, String ownerTableAlias, String joinPropertyName) {
		this.joins.add(new JoinType(tableAlias, ownerTableAlias, joinPropertyName));
		return this;
	}

	public NativeQuery<E> addJoin(String tableAlias, String path, LockMode lockMode) {
		this.joins.add(new JoinType(tableAlias, path, lockMode));
		return this;
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

	private static class ScalarType {
		public final String columnAlias;
		public Type type;

		public ScalarType(String columnAlias) {
			this(columnAlias, null);
		}

		public ScalarType(String columnAlias, Type type) {
			this.columnAlias = columnAlias;
			this.type = type;
		}
	}

	private static class EntityType {
		public final String tableAlias;
		public String entityName;
		public final LockMode lockMode;
		public Class<?> entityType;

		public EntityType(String tableAlias, String entityName,
				LockMode lockMode) {
			this.tableAlias = tableAlias;
			this.entityName = entityName;
			this.lockMode = lockMode;
		}

		public EntityType(String tableAlias, Class<?> entityType,
				LockMode lockMode) {
			this.tableAlias = tableAlias;
			this.entityType = entityType;
			this.lockMode = lockMode;
		}
	}
	
	private static class JoinType {
		public String tableAlias;
		public String path;
		public LockMode lockMode;
		
		public String ownerTableAlias;
		public String joinPropertyName;

		
		public JoinType(String tableAlias, String path) {
			this.tableAlias = tableAlias;
			this.path = path;
		}

		public JoinType(String tableAlias, String path, LockMode lockMode){
			this.tableAlias = tableAlias;
			this.path = path;
			this.lockMode = lockMode;
		}

		public JoinType(String tableAlias, String ownerTableAlias, String joinPropertyName) {
			this.tableAlias = tableAlias;
			this.ownerTableAlias = ownerTableAlias;
			this.joinPropertyName = joinPropertyName;
		}		
	}

}