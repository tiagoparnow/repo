package com.amazingbookstore.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.id.PersistentIdentifierGenerator;
import org.hibernate.id.SequenceGenerator;
import org.hibernate.id.SequenceHiLoGenerator;
import org.hibernate.id.enhanced.TableGenerator;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.mapping.Join;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;
import org.hibernate.metamodel.source.MetadataImplementor;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DefaultIntegrator implements Integrator {

	public static final HashMap<String, String> DBMS_CONFIG_MAP = new HashMap<String, String>();
	
	private Logger log = LoggerFactory.getLogger(DefaultIntegrator.class);

	
	@Override
	public void integrate(Configuration configuration, SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
		configuration.registerTypeOverride(new SimNaoType());
		configuration.registerTypeOverride(new LocalDateType());
		configuration.registerTypeOverride(new LocalDateTimeType());

		this.loadConfig(configuration, sessionFactory);
		this.loadSchemas(configuration, sessionFactory);
	}


	@Override
	public void integrate(MetadataImplementor metadata, SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
	}


	@Override
	public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
	}


	@SuppressWarnings("deprecation")
	private void loadConfig(Configuration configuration, SessionFactoryImplementor sessionFactory) {
		try{
			DBMS_CONFIG_MAP.clear();

			final ConnectionProvider connectionProvider = sessionFactory.getJdbcServices().getConnectionProvider();
			final Connection conn = connectionProvider.getConnection();
			
			final PreparedStatement ps = conn.prepareStatement("select sg_db, de_db from t900dbms");
			final ResultSet rs = ps.executeQuery();

			while(rs.next()){
				DBMS_CONFIG_MAP.put(rs.getString(1), rs.getString(2));
			}

			rs.close();
			ps.close();
			conn.close();

		}catch (SQLException e) {
			log.error("erro ao buscar as configuracoes da base de dados: ", e);
			throw new RuntimeException(e);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	private void loadSchemas(Configuration configuration, SessionFactoryImplementor sessionFactory) {
		final HashSet<String> set = new HashSet<String>();
		
		configuration.getClassMappings().forEachRemaining(persistentClass -> {
			if(!set.add(persistentClass.getClassName())){
				return;
			}
			
			persistentClass.getJoinIterator().forEachRemaining(join -> this.modifyTable(((Join)join).getTable()));
			persistentClass.setDynamicInsert(true);
			persistentClass.setDynamicUpdate(true);

			final String tableSchema = persistentClass.getTable().getSchema();
			this.modifyTable(persistentClass.getTable());

			if(persistentClass.getIdentifier() != null && persistentClass.getIdentifier() instanceof SimpleValue){
				final SimpleValue simpleValue = ((SimpleValue) persistentClass.getIdentifier());
				if (simpleValue.getIdentifierGeneratorStrategy().equals("native") && sessionFactory.getDialect().supportsSequences() && simpleValue.getIdentifierGeneratorProperties().containsKey("max_lo") ) {
					simpleValue.setIdentifierGeneratorStrategy(SequenceHiLoGenerator.class.getName());
				}

				final Properties pros = simpleValue.getIdentifierGeneratorProperties();
				final String idSchema = pros.getProperty(PersistentIdentifierGenerator.SCHEMA, tableSchema);

				if(StringUtils.isNotBlank(idSchema) && DBMS_CONFIG_MAP.containsKey(idSchema)){
					final String schema = DBMS_CONFIG_MAP.get(idSchema);

					if(pros.containsKey(SequenceGenerator.SEQUENCE)){
						final String sequence =  MessageFormat.format(schema, pros.getProperty(SequenceGenerator.SEQUENCE));
						pros.setProperty(SequenceGenerator.SEQUENCE, sequence);
						pros.remove(PersistentIdentifierGenerator.SCHEMA);

					}else if(pros.containsKey(TableGenerator.TABLE)){
						final String table =  MessageFormat.format(schema, pros.getProperty(TableGenerator.TABLE_PARAM));
						pros.setProperty(TableGenerator.TABLE_PARAM, table);
						pros.remove(PersistentIdentifierGenerator.SCHEMA);
					}
				}
			}

		});

		configuration.getTableMappings().forEachRemaining(this::modifyTable);
		
	}
	
	
	public static String formatTable(String key, String table){
		final String schema = DBMS_CONFIG_MAP.get(key);
		return MessageFormat.format(StringUtils.defaultString(schema, key), table).concat(" ");
	}

	public static String formatTable(String key, String table, String alias) {
		final String schema = DBMS_CONFIG_MAP.get(key);
		return MessageFormat.format(StringUtils.defaultString(schema, key), table).concat(" ").concat(alias).concat(" ");
	}

	private void modifyTable(Table table) {
		if(StringUtils.isBlank(table.getSchema()) || !DBMS_CONFIG_MAP.containsKey(table.getSchema())){
			return;
		}

		final String dbms = DBMS_CONFIG_MAP.get(table.getSchema());
		final String name =  MessageFormat.format(dbms, table.getName());

		table.setSchema(null);
		table.setName(name);
	}

}