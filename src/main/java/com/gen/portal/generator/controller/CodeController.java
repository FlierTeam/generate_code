
package com.gen.portal.generator.controller;

import com.gen.common.base.BaseController;
import com.gen.common.vo.Grid;
import com.gen.portal.generator.kit.GeneratorKit;
import com.gen.portal.generator.service.CodeService;
import com.jfinal.aop.Inject;
import com.jfinal.core.Path;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.generator.ColumnMeta;
import com.jfinal.plugin.activerecord.generator.TableMeta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Jfinal_Layui代码生成器
 *
 * @author admin
 * @date 2020-02-21
 */
@Path(value = "/generator/code", viewPath = "/portal/generator/code")
public class CodeController extends BaseController {

	@Inject
	CodeService codeService;

	/**
	 * 通用生成
	 */
	public void index() {
		render("generic.html");
	}

	/**
	 * 代码器首页
	 */
	public void jfinalLayui() {
		render("jfinalLayui.html");
	}

	/**
	 * 可以修改模板，然后按照模板生成
	 */
	public void editTemplate() {
		render("editTemplate.html");
	}

	/**
	 * 数据库表详情页
	 */
	public void tables() {
		render("tables.html");
	}

	/**
	 * 展示模板页面 模板管理
	 */
	public void templateCodeHtml() {
		Integer type = getInt("type");
		GeneratorKit.pathConversion(type);
		setAttr("type", type);
		render("templateManageHtml.html");
	}

	/**
	 * 数据库表详情页面列表
	 */
	public void tablesList() {
		renderJson(codeService.queryTablesList(getAllParamsToRecord()));
	}

	/**
	 * 查询模板文件列表
	 */
	public void queryTemplates() {
		renderJson(codeService.queryTemplates(getAllParamsToRecord()));
	}

	/**
	 * 获取模板文件对应内容
	 */
	public void getTemplatesContents() {
		renderJson(codeService.getTemplatesContents(getAllParamsToRecord()));
	}

	/**
	 * 创建新模板
	 */
	public void createTemplate() {
		renderJson(codeService.createTemplate(getAllParamsToRecord()));
	}

	/**
	 * 修改模板
	 */
	public void updateTemplate() {
		renderJson(codeService.updateTemplate(getAllParamsToRecord()));
	}


	/**
	 * 删除模板
	 */
	public void deleteTemplate() {
		renderJson(codeService.deleteTemplate(getAllParamsToRecord()));
	}

	/**
	 * 查询表内容
	 */
	public void tablesListByname() {
		renderJson(codeService.queryDetail(getAllParamsToRecord()));
	}

	/**
	 * 更换数据源
	 */
	public void changeSource() {
		String url = get("url");
		String user = get("user");
		String pwd = get("pwd");
		String jdbcType = get("jdbcType");
		if (!StrKit.hasBlank(url, user, pwd, jdbcType)) {
			codeService.changeDatabaseConnection(jdbcType, url, user, pwd);
			renderJson(Ret.ok("data", "更换成功"));
			return;
		} else if (!StrKit.hasBlank(url, user, pwd)) {
			codeService.changeDatabaseConnection(url, user, pwd);
			renderJson(Ret.ok("data", "更换成功"));
			return;
		}
		renderJson(fail());
	}

	/**
	 * 生成代码
	 */
	public void createCode() {
		Record record = getAllParamsToRecord();
		int type = Integer.parseInt(getPara("type"));
		GeneratorKit.switchChangeTemplate(type);

		String[] tableNameArray = getPara("name").split(",");
		String modelPackage = getPara("packageName") + ".model.";
		String[] modelNameArray = getPara("modelName").split(",");
		List<Record> codeList = new ArrayList<>();
		List<String> modelList = new ArrayList<>();
		for (int i = 0; i < tableNameArray.length; i++) {
			// 查询表信息
			record.set("name", tableNameArray[i] + "=");
			Grid grid = codeService.queryTablesList(record);
			@SuppressWarnings("unchecked")
			List<TableMeta> tableList = (List<TableMeta>) grid.getList();
			List<ColumnMeta> columnMetas = tableList.get(0).columnMetas;

			// 用模板引擎生成 HTML 片段 replyItem
			Ret ret = Ret.by("columnMetas", columnMetas);

			ret.set("modelName", StrKit.firstCharToLowerCase(modelNameArray[i]));
			ret.set("primaryKey", tableList.get(0).primaryKey);
			String formItem = renderToString("temp/_form.html", ret);
			String tableItem = renderToString("temp/_table.html", ret);

			// 创建模板内容
			record.set("modelName", modelPackage + modelNameArray[i]);
			record.set("primaryKey", tableList.get(0).primaryKey);
			record.set("tableComment", tableList.get(0).remarks);
			record.set("tableNames", tableNameArray);
			record.set("isSubTable", false);
			Record codeRecord = codeService.createCodeTemplate(record, formItem, tableItem, type);
			codeList.add(codeRecord);
			modelList.add(modelPackage + modelNameArray[i]);
		}

		// 存储数据，用于创建本地文件
		setSessionAttr("downloadCode", codeList);
		setSessionAttr("modelList", modelList);
		setSessionAttr("packageName", getPara("packageName"));
		renderJson(Ret.ok("data", codeList));
	}

	/**
	 * 通用代码生成
	 */
	public void universalGenerate() {
		Record record = getAllParamsToRecord();
		String[] tableNameArray = getPara("name").split(",");
		String genCodeArea = getPara("genCodeArea");
		Map<String, String> map = new HashMap(2);
		String list = "";
		for (int i = 0; i < tableNameArray.length; i++) {
			// 查询表信息
			record.set("name", tableNameArray[i] + "=");
			Grid grid = codeService.queryTablesList(record);
			@SuppressWarnings("unchecked")
			List<TableMeta> tableList = (List<TableMeta>) grid.getList();
			List<ColumnMeta> columnMetas = tableList.get(0).columnMetas;
			list = codeService.createGenericCode(columnMetas, genCodeArea);
			map.put(tableNameArray[i], list);
		}
		renderJson(Ret.ok("data", map));
	}

	/**
	 * 下载代码
	 */
	public void download() {
		File yfile=new File(GeneratorKit.filePath +"/src/main/webapp/WEB-INF/views"
				+ GeneratorKit.separator+"model");
		File filezip=new File(GeneratorKit.filePath +"/src/main/webapp/WEB-INF/views"
				+ GeneratorKit.separator+"model.zip");
		if (yfile.isDirectory()){
			GeneratorKit.delFiles(yfile);
		}
		if (filezip.exists()){
			filezip.delete();
		}
		List<Record> codeList = getSessionAttr("downloadCode");
		String packageName = getSessionAttr("packageName");
		List<String> modelList = getSessionAttr("modelList");
		String codeName = getPara("codeName");
		String filePath = getPara("filePath");
		if (StrKit.notBlank(filePath)) {
			File file = new File(filePath);
			if (file.exists()) {
				GeneratorKit.filePath = filePath + GeneratorKit.separator;
			} else {
				boolean mkdirs = file.mkdirs();
				if (!mkdirs) {
					renderJson(fail());
					return;
				}
			}
		} else {
			GeneratorKit.filePath = System.getProperty("user.dir") + GeneratorKit.separator;
		}
		try {
			for (int i = 0; i < codeList.size(); i++) {
				codeService.downloadFile(getPara("type"), modelList.get(i), packageName, codeList.get(i).set("codeName", codeName));
			}
			GeneratorKit.ZipCompress(GeneratorKit.filePath +"/src/main/webapp/WEB-INF/views"
					+ GeneratorKit.separator+"model",GeneratorKit.filePath +"/src/main/webapp/WEB-INF/views"
							+ GeneratorKit.separator+"model.zip");
			renderJson(ok());
		} catch (IOException e) {
			renderJson(fail());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void downloadFile(){
		File file=new File(GeneratorKit.filePath +"/src/main/webapp/WEB-INF/views"
				+ GeneratorKit.separator+"model.zip");
		if (file.exists()){
			renderFile(file);
		}else {
			renderJson(fail());
		}
	}
}
