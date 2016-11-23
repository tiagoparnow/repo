package com.amazingbookstore.persistence;

import java.io.Serializable;

import org.hibernate.dialect.Dialect;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.DiscriminatorType;
import org.hibernate.type.PrimitiveType;
import org.hibernate.type.StringType;
import org.hibernate.type.descriptor.java.BooleanTypeDescriptor;
import org.hibernate.type.descriptor.sql.CharTypeDescriptor;

/**
 * Classe para conversao responsavel por fazer conversao de valores booleanos
 * para 'S' ou 'N'
 * 
 * 
 */
public class SimNaoType extends AbstractSingleColumnStandardBasicType<Boolean> implements PrimitiveType<Boolean>, DiscriminatorType<Boolean> {

	private static final long serialVersionUID = 1L;
	
	public static final SimNaoType INSTANCE = new SimNaoType();

	public SimNaoType() {
		super(CharTypeDescriptor.INSTANCE, new BooleanTypeDescriptor('S', 'N'));
	}

	@Override
	public String getName() {
		return "simNaoType";
	}
	
	@Override
	public String[] getRegistrationKeys() {
		return new String[]{ this.getName(), boolean.class.getName(), Boolean.class.getName() }; 
	}
	
	@Override
	public Class<?> getPrimitiveClass() {
		return boolean.class;
	}

	@Override
	public Boolean stringToObject(String xml) throws Exception {
		return fromString(xml);
	}

	@Override
	public Serializable getDefaultValue() {
		return Boolean.FALSE;
	}

	@Override
	public String objectToSQLString(Boolean value, Dialect dialect) throws Exception {
		return StringType.INSTANCE.objectToSQLString(value.booleanValue() ? "S" : "N", dialect);
	}

}