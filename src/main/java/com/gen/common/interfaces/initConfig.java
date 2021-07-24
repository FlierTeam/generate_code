package com.gen.common.interfaces;

import com.jfinal.plugin.activerecord.IContainerFactory;
import com.jfinal.plugin.activerecord.cache.EhCache;
import com.jfinal.plugin.activerecord.dialect.Dialect;

import javax.sql.DataSource;

/**
 * @author admin
 */
public interface initConfig {

	/**
	 * 初始化config
	 * @param configName
	 * @param dataSource
	 * @param dialect
	 * @param showSql
	 * @param devMode
	 * @param transactionLevel
	 * @param iContainerFactory
	 * @param ehCache
	 */
	void init(String configName, DataSource dataSource, Dialect dialect, Boolean showSql, Boolean devMode, Integer transactionLevel, IContainerFactory iContainerFactory, EhCache ehCache);
}
