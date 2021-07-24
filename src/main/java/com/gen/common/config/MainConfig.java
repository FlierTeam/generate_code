
package com.gen.common.config;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.wall.WallFilter;
import com.gen.common.directive.MyNowDirective;
import com.gen.common.interceptors.LogInterceptor;
import com.jfinal.config.*;
import com.jfinal.core.JFinal;
import com.jfinal.ext.handler.UrlSkipHandler;
import com.jfinal.json.FastJsonFactory;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.CaseInsensitiveContainerFactory;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.activerecord.dialect.OracleDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.server.undertow.UndertowServer;
import com.jfinal.template.Engine;
import com.jfinal.template.source.ClassPathSourceFactory;

public class MainConfig extends JFinalConfig {


	/**
	 * 运行main方法启动项目
	 */
	public static void main(String[] args) {
		// 加载配置文件
		loadConfig();
		UndertowServer.start(MainConfig.class);
	}

	public static Prop p;

	static void loadConfig() {
		if (p == null) {
			p = PropKit.use("config-dev.txt");
		}
	}

	/**
	 * 配置JFinal常量
	 */
	@Override
	public void configConstant(Constants me) {
		//加载配置文件
		loadConfig();
		// 设置当前是否为开发模式
		me.setDevMode(p.getBoolean("devMode"));
		// 设置error渲染视图
		me.setError403View(WebContant.error403View);
		me.setError404View(WebContant.error404View);
		me.setError500View(WebContant.error500View);
		me.setJsonFactory(FastJsonFactory.me());
		//开启依赖注入
		me.setInjectDependency(true);
	}

	/**
	 * 配置JFinal路由映射
	 */
	@Override
	public void configRoute(Routes me) {
		me.scan(WebContant.scanPackageName);
		me.setBaseViewPath(WebContant.baseViewPath);
	}

	/**
	 * 根据配置文件获取数据库插件
	 * 抽取成独立的方法，便于重用该方法，减少代码冗余
	 */
	public static DruidPlugin createDruidPlugin() {
		loadConfig();
		return new DruidPlugin(p.get("jdbcUrl"), p.get("user"), p.get("password").trim());
	}

	/**
	 * 根据传入参数创建获取数据库插件
	 * 抽取成独立的方法，便于重用该方法，减少代码冗余
	 */
	public static DruidPlugin createDruidPlugin(String url, String user, String password) {
		DruidPlugin dbPlugin = new DruidPlugin(url, user, password.trim());
		return dbPlugin;
	}

	/**
	 * 配置JFinal插件 数据库连接池 ORM 缓存等插件 自定义插件
	 */
	@Override
	public void configPlugin(Plugins me) {
		// 配置数据库连接池插件
		DruidPlugin dbPlugin = createDruidPlugin();
		/** 配置druid监控 **/
		dbPlugin.addFilter(new StatFilter());
		WallFilter wall = new WallFilter();
		wall.setDbType(p.get("dbType"));
		dbPlugin.addFilter(wall);

		// orm映射 配置ActiveRecord插件
		ActiveRecordPlugin arp = new ActiveRecordPlugin(dbPlugin);
		//sql模板
		arp.getEngine().setSourceFactory(new ClassPathSourceFactory());
		arp.addSqlTemplate(WebContant.defaultSqlTemplate);
		// 代码器模板
		arp.addSqlTemplate(WebContant.defaultCodeTemplate);

		if ("oracle".equals(p.get("dbType"))) {
			dbPlugin.setDriverClass(JdbcConstants.ORACLE_DRIVER);
			// 配置属性名(字段名)大小写,true：小写，false:大写,统一小写，切换oracle数据库的时候可以不用改页面字段
			arp.setContainerFactory(new CaseInsensitiveContainerFactory(true));
			arp.setDialect(new OracleDialect());
		} else {
			arp.setDialect(new MysqlDialect());
		}

		// 添加到插件列表中
		me.add(dbPlugin);
		me.add(arp);

		/******** 多数据源配置*********/
		//  oracleDb(me);

		// 配置缓存插件
		me.add(new EhCachePlugin());
	}

	@Override
	public void configInterceptor(Interceptors me) {
		me.add(new LogInterceptor());
	}

	/**
	 * oracle 数据源
	 *
	 * @param me
	 */
	public void oracleDb(Plugins me) {
		DruidPlugin dbPluginOracle = new DruidPlugin(p.get("oracle.jdbcUrl"), p.get("oracle.user"), p.get("oracle.password").trim());
		me.add(dbPluginOracle);
		dbPluginOracle.addFilter(new StatFilter());
		WallFilter wallOracle = new WallFilter();
		wallOracle.setDbType(p.get("oracle.dbType"));
		dbPluginOracle.addFilter(wallOracle);
		// oracle ActiveRecrodPlugin 实例，并指定configName为 oracle
		ActiveRecordPlugin arpOracle = new ActiveRecordPlugin("oracle", dbPluginOracle);
		arpOracle.setDialect(new OracleDialect());
		me.add(arpOracle);
	}

	/**
	 * 配置全局处理器
	 */
	@Override
	public void configHandler(Handlers me) {
		// 放开/ureport/开头的请求
		me.add(new UrlSkipHandler("^\\/ureport.*", true));
	}

	/**
	 * 配置模板引擎
	 */
	@Override
	public void configEngine(Engine me) {
		// 这里只有选择JFinal TPL的时候才用
		me.setDevMode(p.getBoolean("engineDevMode"));
		// 当前时间指令
		me.addDirective("now", MyNowDirective.class);
		// 项目根路径
		me.addSharedObject("path", JFinal.me().getContextPath());
		// 项目名称
		me.addSharedObject("projectName", p.get("projectName"));
		// 项目版权
		me.addSharedObject("copyright", p.get("copyright"));
		// 配置共享函数模板
		me.addSharedFunction(WebContant.functionTemp);
		// 文件在线预览
		me.addSharedObject("onlinePreview", p.getBoolean("onlinePreview"));
		me.addSharedObject("onlinePreviewUrl", p.get("onlinePreviewUrl"));
	}
}
