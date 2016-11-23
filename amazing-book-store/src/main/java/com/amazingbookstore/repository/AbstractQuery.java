package com.amazingbookstore.repository;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.math.NumberUtils;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;

public abstract class AbstractQuery<Entity> {

	private static final Pattern SUBJECT_PATTERN = Pattern.compile("^select\\s+(\\w+(?:\\s*\\.\\s*\\w+)*?)(?:\\s*,\\s*(\\w+(?:\\s*\\.\\s*\\w+)*?))*?\\s+from", Pattern.CASE_INSENSITIVE); 
	private static final Pattern FROM_PATTERN = Pattern.compile("(^|\\s)(from)\\s", Pattern.CASE_INSENSITIVE);
	private static final Pattern WHERE_PATTERN = Pattern.compile("\\s(where)\\s", Pattern.CASE_INSENSITIVE);
	private static final Pattern ORDER_PATTERN = Pattern.compile("\\s(order)(\\s)+by\\s", Pattern.CASE_INSENSITIVE);
	private static final Pattern GROUP_PATTERN = Pattern.compile("\\s(group)(\\s)+by\\s", Pattern.CASE_INSENSITIVE);

	private static final String LOGIC_OPERATOR_AND = "and";
	private static final String LOGIC_OPERATOR_OR = "or";

	private Entry<String, Object> query;
	private Integer firstResult;
	private Integer maxResults;

	private final Map<String, Object> where = new HashMap<String, Object>();
	private final Map<String, Object> parameters = new HashMap<String, Object>();

	private String restrictionLogicOperator;
	private String orderBy;
	private String groupBy;

	private boolean useWildcardAsCountQuerySubject = true;

	private String parsedQuery;
	private List<String> parsedRestrictions;
	private List<Object> parsedParameters;

	protected abstract List<Entity> getResultList();
	
	protected abstract Long getResultCount();

	protected abstract ScrollableResults scroll();
	
	protected abstract ScrollableResults scroll(ScrollMode mode);


	protected void parseQuery() {
		if (this.parsedParameters != null && this.parsedRestrictions != null){
			return;
		}

		this.parsedRestrictions = new ArrayList<String>();
		this.parsedParameters = new ArrayList<Object>();

		if(this.query != null){
			final QueryParser parser = new QueryParser(this.query.getKey(), this.query.getValue(), 0);
			this.parsedQuery = parser.getParameter();
			this.parsedParameters.addAll(parser.getParameters());
		}

		for(String key : this.where != null ? this.where.keySet() : Collections.<String>emptySet()){
			final QueryParser parser = new QueryParser(key, this.where.get(key), this.parsedParameters.size());
			this.parsedRestrictions.add(parser.getParameter());
			this.parsedParameters.addAll(parser.getParameters());
		}
	}

	protected String getRenderedQuery() {
		return this.getRenderedQuery(true, true);
	}

	protected String getRenderedQuery(boolean groupBy, boolean orderBy) {
		final StringBuilder builder = new StringBuilder().append(this.parsedQuery);

		for (int i = 0; i < this.parsedRestrictions.size(); i++){
			if (WHERE_PATTERN.matcher(builder).find()){
				builder.append(" ").append(this.getRestrictionLogicOperator()).append(" ");
			}else{
				builder.append(" where ");
			}
			builder.append(this.parsedRestrictions.get(i) );
		}

		if (groupBy && this.getGroupBy() != null) {
			builder.append(" group by ").append(this.getGroupBy());
		}

		if (orderBy && this.getOrderBy() != null) {
			builder.append(" order by ").append(this.getOrderBy());
		}

		return builder.toString();
	}
   
	protected String getCountQuery() {
		final String ejbql = this.getRenderedQuery();

		final Matcher fromMatcher = FROM_PATTERN.matcher(ejbql);
		if (!fromMatcher.find()){
			throw new IllegalArgumentException("no from clause found in query");
		}
      
		final int fromLoc = fromMatcher.start(2);
      
		final Matcher orderMatcher = ORDER_PATTERN.matcher(ejbql);
		final int orderLoc = orderMatcher.find() ? orderMatcher.start(1) : ejbql.length();

		final Matcher groupMatcher = GROUP_PATTERN.matcher(ejbql);
		final int groupLoc = groupMatcher.find() ? groupMatcher.start(1) : orderLoc;

		final Matcher whereMatcher = WHERE_PATTERN.matcher(ejbql);
		final int whereLoc = whereMatcher.find() ? whereMatcher.start(1) : groupLoc;

		String subject;
		if (getGroupBy() != null) {
			subject = "distinct " + getGroupBy();
      
		}else if (useWildcardAsCountQuerySubject) {
			subject = "*";
      
		}else {
			final Matcher subjectMatcher = SUBJECT_PATTERN.matcher(ejbql);
			if (subjectMatcher.find()){
				subject = subjectMatcher.group(1);
			}else{
				throw new IllegalStateException("invalid select clause for query");
			}
		}
      
		return new StringBuilder(ejbql.length() + 15).append("select count(").append(subject).append(") ").
				append(ejbql.substring(fromLoc, whereLoc).replace("join fetch", "join")).
				append(ejbql.substring(whereLoc, groupLoc)).toString().trim();
	}
   
	public AbstractQuery<Entity> setQuery(String query) {
		this.query = new AbstractMap.SimpleEntry<String, Object>(query, null);
		return this;
	}
	
	public AbstractQuery<Entity> setQuery(String query, Object parameter) {
		this.query = new AbstractMap.SimpleEntry<String, Object>(query, parameter);
		return this;
	}

	public Entry<String, Object> getQuery() {
		return query;
	}
    
	public Integer getFirstResult() {
		return firstResult;
	}
	
	protected AbstractQuery<Entity> setFirstResult(Integer firstResult){
		this.firstResult = firstResult;
		return this;
	}

	protected Integer getMaxResults(){
		return maxResults;
	}

	protected AbstractQuery<Entity> setMaxResults(Integer maxResults){
		this.maxResults = maxResults;
		return this;
	}
	
	public String getGroupBy() {
		return groupBy;
	}
    
	public AbstractQuery<Entity> setGroupBy(String groupBy) {
		this.groupBy = groupBy;
		return this;
	}

	public String getOrderBy() {
		return orderBy;
	}
	
	public AbstractQuery<Entity> setOrderBy(String orderBy) {
		this.orderBy = orderBy;
		return this;
	}
	
  	public String getRestrictionLogicOperator(){
	   return restrictionLogicOperator != null ? restrictionLogicOperator : LOGIC_OPERATOR_AND;
  	}
   
  	public AbstractQuery<Entity> setRestrictionLogicOperator(String operator){
  		restrictionLogicOperator = sanitizeRestrictionLogicOperator(operator);
  		return this;
  	}

  	private String sanitizeRestrictionLogicOperator(String operator) {
  		if (operator == null || operator.trim().length() == 0){
  			return LOGIC_OPERATOR_AND;
  		}
      
  		if (!(LOGIC_OPERATOR_AND.equals(operator) || LOGIC_OPERATOR_OR.equals(operator))){
  			throw new IllegalArgumentException("Invalid restriction logic operator: " + operator);
  		}      
  		return operator;
  	}

  	protected boolean isUseWildcardAsCountQuerySubject() {
  		return useWildcardAsCountQuerySubject;
  	}

  	protected void setUseWildcardAsCountQuerySubject(boolean useCompliantCountQuerySubject) {
  		this.useWildcardAsCountQuerySubject = useCompliantCountQuerySubject;
  	}
  	
  	protected List<Object> getParsedParameters() {
		return parsedParameters;
	}
  	
  	public Map<String, Object> getWhere() {
		return where;
	}

  	public Map<String, Object> getParameters() {
		return parameters;
	}

  	protected static class QueryParser {
  		
  		private StringBuilder parameterBuilder = new StringBuilder();
	   
  		private List<Object> parameters = new ArrayList<Object>();

  		public static String getParameterName(int loc) {
  			return "el" + (loc);
  		}

  		public String getParameter() {
  			return parameterBuilder.toString();
  		}

  		public List<Object> getParameters() {
  			return parameters;
  		}

  		public QueryParser(String parameter, Object value, int startingParameterNumber) {
  			final StringTokenizer tokens = new StringTokenizer(parameter, "? ", true);

  			while (tokens.hasMoreTokens()) {
  				final String token = tokens.nextToken();
			   
  				if ("?".equals(token) && tokens.hasMoreTokens()) {
  					final String expressionToken = tokens.nextToken();
  					
  					if (NumberUtils.isDigits(expressionToken)) {
  						this.parameterBuilder.append(":").append(getParameterName(startingParameterNumber + parameters.size()));
  						this.parameters.add(value instanceof List<?> ? ((List<?>) value).get(NumberUtils.toInt(expressionToken) - 1) : value);
  					
  					} else {
  						this.parameterBuilder.append(token).append(expressionToken);
  					}

  				} else {
  					this.parameterBuilder.append(token);
  				}
  				
  			}
  		}
  	}

}