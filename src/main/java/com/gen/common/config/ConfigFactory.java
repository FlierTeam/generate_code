package com.gen.common.config;

import com.gen.common.interfaces.initConfig;
import com.jfinal.plugin.activerecord.Config;
import com.jfinal.plugin.activerecord.IContainerFactory;
import com.jfinal.plugin.activerecord.cache.EhCache;
import com.jfinal.plugin.activerecord.dialect.Dialect;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;

import javax.sql.DataSource;

/**
 * @author admin
 */
public class ConfigFactory implements initConfig {

	Config config = null;
	Integer defaultLevel = 4;
	IContainerFactory defaultIContainerFactory = IContainerFactory.defaultContainerFactory;
	EhCache defaultEhCache = new EhCache();

	public ConfigFactory(String configName, DataSource dataSource) {
		this.init(configName, dataSource, new MysqlDialect(), true, true, defaultLevel, defaultIContainerFactory, defaultEhCache);
	}

	public ConfigFactory(String configName, DataSource dataSource, Dialect dialect, Boolean showSql, Boolean devMode, Integer transactionLevel, IContainerFactory iContainerFactory, EhCache ehCache) {
		this.init(configName, dataSource, dialect, showSql, devMode, transactionLevel, iContainerFactory, ehCache);
	}

	@Override
	public void init(String configName, DataSource dataSource, Dialect dialect, Boolean showSql, Boolean devMode, Integer transactionLevel, IContainerFactory iContainerFactory, EhCache ehCache) {
		Config config = new Config(configName, dataSource, dialect, showSql, devMode, transactionLevel, iContainerFactory, ehCache);
		if (this.config == null) {
			this.config = config;
		}
	}

	public Config getConfig() {
		this.initTemplate(config);
		return config;
	}

	public Config initTemplate(Config config) {
		config.getSqlKit().addSqlTemplate(WebContant.defaultSqlTemplate);
		config.getSqlKit().addSqlTemplate(WebContant.defaultCodeTemplate);
		config.getSqlKit().parseSqlTemplate();
		return config;
	}
}
