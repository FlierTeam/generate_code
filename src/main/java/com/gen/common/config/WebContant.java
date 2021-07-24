

package com.gen.common.config;

import com.jfinal.kit.PropKit;


/**
 * 系统配置常量
 *
 * @author admin
 * @date 2018年11月19日
 */
public interface WebContant {

	/** ============================系统常量=========================== **/
	/**
	 * 视图基础目录
	 **/
	String baseViewPath = "/WEB-INF/views";
	/**
	 * 项目名称
	 **/
	String projectName = PropKit.get("projectName", "代码生成器");

	/**
	 * 错误页面
	 **/
	String error403View = baseViewPath + "/common/error/403.html";
	String error404View = baseViewPath + "/common/error/404.html";
	String error500View = baseViewPath + "/common/error/500.html";
	/**
	 * 前端函数模板
	 **/
	String functionTemp = baseViewPath + "/common/templete/_layout.html";

	/**
	 * 默认sql模板 jfinal_layui
	 **/
	String defaultSqlTemplate = "/sql/jfinal_layui/all_sqls.sql";
	/**
	 * 默认代码模板 jfinal_layui
	 **/
	String defaultCodeTemplate = "/code/jfinal_layui/_all_code.sql";
	/**
	 * template_gen 通用用于被修改然后生成sql模板
	 **/
	String modifySqlTemplate = "/sql/template_gen/all_sqls.sql";
	/**
	 * template_gen 通用用于被修改然后生成的代码模板
	 **/
	String modifyCodeTemplate = "/code/template_gen/_all_code.sql";
	/**
	 * baseTemplate 通用生成代码的模板
	 **/
	String baseTemplate = "/code/generic_generation/_all_code.sql";

	/**
	 * 默认代码模板的根路径
	 **/
	String defaultCodeTemplatePath = "/code/jfinal_layui";
	/**
	 * template_gen 通用用于被修改然后生成sql模板的根路径
	 **/
	String modifySqlTemplatePath = "/code/template_gen";
	/**
	 * baseTemplatePath 通用生成代码的模板的根路径
	 **/
	String baseTemplatePath = "/code/generic_generation";

	/**
	 * 自动扫描的Controller、Model所在的包，仅扫描该包及其子包下面的路由,默认com.gen
	 */
	String scanPackageName = PropKit.get("scanPackageName", "com.gen");

}
