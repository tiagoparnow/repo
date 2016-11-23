package com.amazingbookstore.persistence;

import javax.enterprise.util.AnnotationLiteral;


@SuppressWarnings("all")
public class EntityManagerResolverLiteral extends AnnotationLiteral<EntityManagerResolver> implements EntityManagerResolver {

	private static final long serialVersionUID = -4325920176903152623L;

	private String value;

	public EntityManagerResolverLiteral(String value) {
		this.value = value;
	}

	@Override
	public String value() {
		return value;
	}
	
}