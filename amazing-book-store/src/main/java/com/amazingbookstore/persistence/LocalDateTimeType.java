package com.amazingbookstore.persistence;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Locale;

import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.internal.util.compare.ComparableComparator;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.LiteralType;
import org.hibernate.type.VersionType;
import org.hibernate.type.descriptor.sql.TimestampTypeDescriptor;

public class LocalDateTimeType extends AbstractSingleColumnStandardBasicType<LocalDateTime> implements VersionType<LocalDateTime>, LiteralType<LocalDateTime> {
	
	private static final long serialVersionUID = 1L;
	
	public static final LocalDateTimeType INSTANCE = new LocalDateTimeType();
		
	public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern( "yyyy-MM-dd HH:mm:ss.S", Locale.ENGLISH );
		
	public LocalDateTimeType() {
		super( TimestampTypeDescriptor.INSTANCE, LocalDateTimeJavaDescriptor.INSTANCE );
	}
		
	@Override
	public String getName() {
		return LocalDateTime.class.getSimpleName();
	}
	
	@Override
	protected boolean registerUnderJavaType() {
		return true;
	}
	
	@Override
	public String objectToSQLString(LocalDateTime value, Dialect dialect) throws Exception {
		return "{ts '" + FORMATTER.format( value ) + "'}";
	}
	
	@Override
	public LocalDateTime seed(SessionImplementor session) {
		return LocalDateTime.now();
	}
	
	@Override
	public LocalDateTime next(LocalDateTime current, SessionImplementor session) {
		return LocalDateTime.now();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Comparator<LocalDateTime> getComparator() {
		return ComparableComparator.INSTANCE;
	}

}
