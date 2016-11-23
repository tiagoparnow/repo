package com.amazingbookstore;

import java.util.Arrays;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;

import com.amazingbookstore.persistence.EntityManagerResolver;
import com.amazingbookstore.service.MapperResolver;


@Singleton
public class AmazingBookStoreProducer {

	@Produces
	@EntityManagerResolver("amazingBookStore")
	@PersistenceContext(unitName="amazingBookStoreUnit")
	private EntityManager em;

	
	@Produces 
	@MapperResolver("amazingBookStore")
	@Singleton
	public Mapper mapper() {
    	final DozerBeanMapper mapper = new DozerBeanMapper();
    	mapper.setMappingFiles(Arrays.asList("dozer-mapping.xml"));
		return mapper;
    }

}