
package com.gen.portal.generator.service;

import com.gen.common.config.ConfigFactory;
import com.gen.common.config.MainConfig;
import com.gen.common.config.WebContant;
import com.gen.common.enumerate.GENERATION_CODE_TYPE;
import com.gen.common.vo.Grid;
import com.gen.portal.generator.kit.GeneratorKit;
import com.gen.portal.generator.kit.TreeModel;
import com.jfinal.kit.*;
import com.jfinal.plugin.activerecord.*;
import com.jfinal.plugin.activerecord.dialect.OracleDialect;
import com.jfinal.plugin.activerecord.generator.ColumnMeta;
import com.jfinal.plugin.activerecord.generator.MetaBuilder;
import com.jfinal.plugin.activerecord.generator.TableMeta;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.Engine;

import java.io.*;
import java.util.*;

import static com.alibaba.druid.util.JdbcConstants.ORACLE;

/**
 * 代码生成器接口
 *
 * @author admin
 * @date 2020-02-21
 */
public class CodeService {

	/**
	 * 针对 Model 中七种可以自动转换类型的 getter 方法，调用其具有确定类型返回值的 getter 方法
	 * 享用自动类型转换的便利性，例如 getInt(String)、getStr(String)
	 * 其它方法使用泛型返回值方法： get(String)
	 * 注意：jfinal 3.2 及以上版本 Model 中的六种 getter 方法才具有类型转换功能
	 */
	@SuppressWarnings("serial")
	protected static Map<String, String> getterTypeMap = new HashMap<String, String>() {{
		put("java.lang.String", "getStr");
		put("java.lang.Integer", "getInt");
		put("java.lang.Long", "getLong");
		put("java.lang.Double", "getDouble");
		put("java.lang.Float", "getFloat");
		put("java.lang.Short", "getShort");
		put("java.lang.Byte", "getByte");
	}};

	private static DbPro db;
	static Engine engine;
	static DruidPlugin druidPlugin;

	static {
		engine = new Engine();
		engine.setToClassPathSourceFactory();
		engine.addSharedMethod(new StrKit());
		engine.addSharedObject("getterTypeMap", getterTypeMap);
		engine.addSharedObject("javaKeyword", JavaKeyword.me);
		druidPlugin = MainConfig.createDruidPlugin();
		druidPlugin.start();
		db = Db.use();
		GeneratorKit.initCache();
	}

	/**
	 * 更改连接的数据库，jdbcType不变
	 */
	public void changeDatabaseConnection(String url, String user, String pwd) {
		druidPlugin = MainConfig.createDruidPlugin(url, user, pwd);
		druidPlugin.start();
		GeneratorKit.changed = true;
	}

	/**
	 * 更改连接的数据库，jdbcType发生改变
	 */
	public void changeDatabaseConnection(String jdbcType, String url, String user, String pwd) {
		if (StrKit.notNull(jdbcType) || StrKit.isBlank(jdbcType)) {
			//移除上一次数据源
			DbKit.removeConfig(GeneratorKit.dbType);
			GeneratorKit.dbType = jdbcType;
			druidPlugin = MainConfig.createDruidPlugin(url, user, pwd);
			druidPlugin.start();

			ConfigFactory configFactory = new ConfigFactory(GeneratorKit.dbType, druidPlugin.getDataSource());
			Config config = configFactory.getConfig();
			DbKit.addConfig(config);
			db = Db.use(GeneratorKit.dbType);
			GeneratorKit.changed = true;
		}
	}

	/**
	 * 查询表详情
	 */
	public Grid queryDetail(Record record) {
		String name = record.getStr("tablename");
		String sql = "select * from " + name;
		List<Record> detail = db.find(sql);
		return new Grid(detail, detail.size());
	}

	/**
	 * 查询所有模板文件树
	 *
	 * @param record
	 * @return
	 */
	public Grid queryTemplates(Record record) {
		int type = Integer.valueOf(record.get("type"));
		GeneratorKit.pathConversion(type);
		File file = new File(PathKit.getRootClassPath() + GeneratorKit.currentTemplatePath);
		List<TreeModel> list = new ArrayList<TreeModel>();
		List<TreeModel> list1 = new ArrayList<TreeModel>();

		if (file.isDirectory()) {
			File[] listFiles = file.listFiles();
			for (File f : listFiles) {
				TreeModel ptree = new TreeModel();
				if (f.isDirectory()) {
					File[] files = f.listFiles();
					List<TreeModel> list2 = new ArrayList<TreeModel>();
					for (File item : files) {
						TreeModel ptree1 = new TreeModel();
						ptree1.setId(item.getPath());
						ptree1.setTitle(item.getName());
						ptree1.setPid(f.getName());
						list2.add(ptree1);
					}
					ptree.setId(f.getPath());
					ptree.setTitle(f.getName());
					ptree.setChildren(list2);
					ptree.setPid("template_gen");
				} else {
					ptree.setId(f.getPath());
					ptree.setTitle(f.getName());
					ptree.setPid(file.getName());
				}
				list.add(ptree);
			}
			TreeModel ctree = new TreeModel();
			ctree.setId("pid");
			ctree.setTitle("template_gen");
			ctree.setChildren(list);
			ctree.setSpread(true);
			ctree.setPid("0");
			list1.add(ctree);
		}
		return new Grid(list1, list1.size());
	}

	/**
	 * 根据id获取文件内容
	 *
	 * @param record
	 * @return
	 */
	public Grid getTemplatesContents(Record record) {
		String title = "";
		String id = record.getStr("id");
		if (StrKit.isBlank(id)) {
			id = PathKit.getRootClassPath() + WebContant.baseTemplate;
		}
		File file = new File(id);
		if (!file.isDirectory()) {
			title=GeneratorKit.readFile(file);
		}
		List list = new ArrayList();
		list.add(new Record().set("data", title));
		return new Grid(list, list.size());
	}


	public Ret createTemplate(Record record) {
		String fileName = record.getStr("fileName");
		if (GeneratorKit.isSystemFile(fileName)) {
			return Ret.fail("msg", "创建" + fileName + "模板失败" + "，不允许新增系统级文件！");
		}

		fileName = GeneratorKit.checkSuffix(fileName);
		String fileContents = record.getStr("fileContents");
		if (!GeneratorKit.checkLegal(fileName, fileContents)) {
			return Ret.fail("msg", "创建" + fileName + "模板失败" + "，模板内容不合法！");
		}
		String rootPath = record.getStr("rootPath");
		fileName = rootPath + GeneratorKit.separator + fileName;
		String templatePath = PathKit.getRootClassPath() + GeneratorKit.currentTemplate;
		String createFile = PathKit.getRootClassPath() + GeneratorKit.currentTemplatePath + GeneratorKit.separator + fileName;

		File files = new File(templatePath);
		File file = new File(createFile);
		Collection<?> list = getTemplatesContents(new Record().set("id", templatePath)).getList();
		Optional<?> first = list.stream().findFirst();
		Record o = (Record) first.get();
		String data = o.getStr("data");
		if (data.indexOf(fileName) == -1) {
			data = data.replaceAll("#end", "\t#include(\"" + fileName + "\")\n#end");
		}
		GeneratorKit.writeFile(file, fileContents);
		GeneratorKit.writeFile(files, data);

		GeneratorKit.refreshByType(rootPath);
		return Ret.ok("msg", "创建" + fileName + "模板成功");
	}


	public Ret updateTemplate(Record record) {
		String fileName = record.getStr("fileName");
		if (GeneratorKit.isSystemFile(fileName)) {
			return Ret.fail("msg", "修改" + fileName + "模板失败" + "，不允许修改系统级文件！");
		}
		String fileContents = record.getStr("fileContents");
		fileName = GeneratorKit.checkSuffix(fileName);
		if (!GeneratorKit.checkLegal(fileName, fileContents)) {
			return Ret.fail("msg", "修改" + fileName + "模板失败" + "，模板内容不合法！");
		}

		String rootPath = record.getStr("rootPath");
		fileName = rootPath + GeneratorKit.separator + fileName;
		String createFile = PathKit.getRootClassPath() + GeneratorKit.currentTemplatePath + GeneratorKit.separator + fileName;
		File file = new File(createFile);
		GeneratorKit.writeFile(file, fileContents);
		GeneratorKit.refreshByType(rootPath);
		return Ret.ok("msg", "修改" + fileName + "模板成功");
	}

	public Ret deleteTemplate(Record record) {
		String fileName = record.getStr("fileName");
		if (GeneratorKit.isSystemFile(fileName)) {
			return Ret.fail("msg", "删除" + fileName + "模板失败" + "，不允许删除系统级文件！");
		}

		fileName = GeneratorKit.checkSuffix(fileName);
		String rootPath = record.getStr("rootPath");
		fileName = rootPath + GeneratorKit.separator + fileName;
		String templatePath = PathKit.getRootClassPath() + GeneratorKit.currentTemplate;
		String deleteFile = PathKit.getRootClassPath() + GeneratorKit.currentTemplatePath + GeneratorKit.separator + fileName;
		File files = new File(templatePath);
		File file = new File(deleteFile);
		Collection<?> list = getTemplatesContents(new Record().set("id", templatePath)).getList();
		Optional<?> first = list.stream().findFirst();
		Record o = (Record) first.get();
		String data = o.getStr("data");
		data = data.replace("\n\t#include(\"" + fileName + "\")", "");
		boolean delete = file.delete();
		if (delete) {
			GeneratorKit.writeFile(files, data);
			GeneratorKit.refreshByType(rootPath);
			return Ret.ok("msg", "删除" + fileName + "模板成功");
		} else {
			return Ret.ok("msg", "删除" + fileName + "模板失败");
		}
	}

	/**
	 * 查询数据库表，会移除没有主键的表，不进行生成
	 *
	 * @param record
	 * @return
	 */
	public Grid queryTablesList(Record record) {
		MetaBuilder metaBuilder = new MetaBuilder(druidPlugin.getDataSource());
		if (ORACLE.equals(GeneratorKit.dbType)) {
			metaBuilder.setDialect(new OracleDialect());
		}
		metaBuilder.addExcludedTable(GeneratorKit.excTable);
		metaBuilder.setRemovedTableNamePrefixes(GeneratorKit.tableNamePrefixes);
		metaBuilder.setGenerateRemarks(true);
		// TableMeta 数据库的表
		List<TableMeta> tableMetas = metaBuilder.build();
		List<TableMeta> resultList = new ArrayList<>();

		String name = record.get("name");
		int pageNumber = Integer.valueOf(record.get("pageNumber", "1"));
		int pageSize = Integer.valueOf(record.get("pageSize", "10"));
		int startIndex = (pageNumber - 1) * pageSize;

		for (int i = 0; i < tableMetas.size(); i++) {
			//搜索
			if (name != null) {
				if (name.endsWith("=")) {
					if (tableMetas.get(i).name.equalsIgnoreCase(name.replace("=", ""))) {
						resultList.add(tableMetas.get(i));
					}
				} else {
					if (tableMetas.get(i).name.indexOf(name) != -1) {
						resultList.add(tableMetas.get(i));
					}
				}
			} else {
				if (i >= startIndex) {
					resultList.add(tableMetas.get(i));
				}
			}
			//分页
			if (resultList.size() == pageSize) {
				break;
			}
		}

		//oracle数据库
		if (ORACLE.equals(GeneratorKit.dbType)) {
			return new Grid(buildTableRemarksByOracle(resultList), tableMetas.size());
		}

		return new Grid(buildTableRemarks(resultList), tableMetas.size());
	}




	/**
	 * 查询表备注信息
	 *
	 * @param tableMetaList
	 * @return
	 */
	private List<TableMeta> buildTableRemarks(List<TableMeta> tableMetaList) {

		String sql = "SELECT TABLE_NAME,TABLE_COMMENT FROM information_schema.TABLES WHERE TABLE_NAME=?";
		for (TableMeta t : tableMetaList) {
			Record rd = db.find(sql, t.name).get(0);
			t.remarks = rd.getStr("TABLE_COMMENT");
		}
		return tableMetaList;
	}

	private List<TableMeta> buildTableRemarksByOracle(List<TableMeta> tableMetaList) {
		String sql = "select ut.COLUMN_NAME,uc.COMMENTS,tc.COMMENTS TABLE_COMMENTS"
				+ " from user_tab_columns  ut"
				+ " inner JOIN user_col_comments uc"
				+ " on ut.TABLE_NAME  = uc.TABLE_NAME and ut.COLUMN_NAME = uc.COLUMN_NAME"
				+ " inner JOIN user_tab_comments tc"
				+ " on ut.TABLE_NAME = tc.TABLE_NAME"
				+ " where ut.TABLE_NAME=? ";

		for (TableMeta t : tableMetaList) {
			List<Record> list = db.find(sql, t.name);
			Record rd = list.get(0);
			t.remarks = rd.getStr("TABLE_COMMENTS");
			t.primaryKey = t.primaryKey;
			List<ColumnMeta> columnMetas = t.columnMetas;
			List<Record> finalList = list;
			columnMetas.forEach(column -> {
				String name = column.name;
				finalList.forEach(r -> {
					String columnName = r.getStr("COLUMN_NAME");
					if (name.equals(columnName)) {
						column.remarks = r.getStr("COMMENTS");
						column.name = name;
					}
				});
			});
		}
		return tableMetaList;
	}

	/**
	 * 生成通用模板代码
	 *
	 * @param list
	 * @param content
	 * @return
	 */
	public String createGenericCode(List<ColumnMeta> list, String content) {
		String result = "";
		Iterator<ColumnMeta> iter = list.iterator();
		while (iter.hasNext()) {
			ColumnMeta columnMeta = iter.next();
			//转换
			String transform = "";
			transform = content.replace("${remarks}", columnMeta.remarks == null ? "" : columnMeta.remarks)
					.replace("${name}", columnMeta.attrName);
			result += transform + "\n";
		}
		return result;
	}

	/**
	 * 生成模板代码
	 *
	 * @param record
	 * @param formItem
	 * @param tableItem
	 * @param type
	 * @return
	 */
	public Record createCodeTemplate(Record record, String formItem, String tableItem, int type) {

		String modelName = record.getStr("modelName");
		String authorName = record.get("authorName");
		String basePackage = record.getStr("packageName");
		String tableComment = record.getStr("tableComment");
		String primaryKey = record.getStr("primaryKey");

		//模板变量
		GeneratorKit codeKit = GeneratorKit.getInstance();
		String portal = basePackage.split("\\.")[2];
		String modular = basePackage.substring(basePackage.indexOf(portal));
		@SuppressWarnings("static-access")
		Kv kv = codeKit.setBasePackage(basePackage).setModular(modular.replace(".", "/")).getJavaKv(modelName);
		kv.set("author", authorName).set("tableComment", tableComment).set("primaryKey", primaryKey);

		if (type == GENERATION_CODE_TYPE.JFINAL_LAYUI.value) {
			return creatJfinalLayuiCode(modelName, kv, record, formItem, tableItem);
		}
		return createCodeTemplate(kv, formItem, tableItem);
	}


	public Record creatJfinalLayuiCode(String modelName, Kv kv, Record record, String formItem, String tableItem) {
		Record result = new Record();
		List<String> codeJava = new ArrayList<>();
		List<String> codeHtml = new ArrayList<>();

		//java模板内容
		for (String str : GeneratorKit.defaultJava) {
			String content = db.getSql("code." + str);
			@SuppressWarnings("unchecked")
			Iterator<Object> iter = kv.keySet().iterator();
			while (iter.hasNext()) {
				Object obj = iter.next();
				content = content.replace("${" + obj + "}", kv.get(obj) == null ? "" : kv.get(obj).toString());
			}
			result.set(str + ".java", content);
			codeJava.add(str + ".java");
		}

		//html模板内容
		for (String str : GeneratorKit.defaultHtml) {
			String content = db.getSql("code." + str);
			@SuppressWarnings("unchecked")
			Iterator<Object> iter = kv.keySet().iterator();
			while (iter.hasNext()) {
				Object obj = iter.next();
				content = content.replace("${" + obj + "}", kv.get(obj) == null ? "" : kv.get(obj).toString());
			}

			content = content.replace("${formCols}", formItem);
			content = content.replace("${tableCols}", tableItem);

			result.set(str + ".html", content);
			codeHtml.add(str + ".html");
		}

		result.set("Model.java", createModelCode(record.getStr("name"), modelName));
		codeJava.add("Model.java");

		result.set("BaseModel.java", createBaseModelCode(record.getStr("name"), modelName));
		codeJava.add("BaseModel.java");

		result.set("codejava", codeJava);
		result.set("codehtml", codeHtml);

		return result;
	}

	public Record createCodeTemplate(Kv kv, String formItem, String tableItem) {
		Record result = new Record();
		List<String> codeJava = new ArrayList<>();
		List<String> codeHtml = new ArrayList<>();

		//java模板内容
		for (String str : GeneratorKit.currentJava) {
			String content = db.getSql("code." + str);
			if (content == null) {
				continue;
			}
			@SuppressWarnings("unchecked")
			Iterator<Object> iter = kv.keySet().iterator();
			while (iter.hasNext()) {
				Object obj = iter.next();
				System.out.println(obj);
				content = content.replace("${" + obj + "}", kv.get(obj) == null ? "" : kv.get(obj).toString());
			}
			result.set(str + ".java", content);
			codeJava.add(str + ".java");
		}

		//html模板内容
		for (String str : GeneratorKit.currentHtml) {
			String content = db.getSql("code." + str);
			if (content == null) {
				continue;
			}
			@SuppressWarnings("unchecked")
			Iterator<Object> iter = kv.keySet().iterator();
			while (iter.hasNext()) {
				Object obj = iter.next();
				content = content.replace("${" + obj + "}", kv.get(obj) == null ? "" : kv.get(obj).toString());
			}

			content = content.replace("${formCols}", formItem);
			content = content.replace("${tableCols}", tableItem);

			result.set(str + ".html", content);
			codeHtml.add(str + ".html");
		}

		result.set("codejava", codeJava);
		result.set("codehtml", codeHtml);
		return result;
	}

	/**
	 * 创建Model代码
	 *
	 * @param tableName
	 * @param className
	 * @return
	 */
	public String createModelCode(String tableName, String className) {
		// model 所使用的包名 (MappingKit 默认使用的包名)
		String modelPackageName = className.substring(0, className.lastIndexOf("."));
		// base model 所使用的包名
		String baseModelPackageName = modelPackageName + ".base";

		Record record = new Record();
		record.set("pageNumber", "1").set("pageSize", "1").set("name", tableName);
		@SuppressWarnings("unchecked")
		List<TableMeta> tableMetas = (List<TableMeta>) queryTablesList(record).getList();

		String template = "/com/gen/portal/generator/model_template.jf";
		Kv data = Kv.by("modelPackageName", modelPackageName);
		data.set("baseModelPackageName", baseModelPackageName);
		data.set("generateDaoInModel", false);
		data.set("tableMeta", tableMetas.get(0));
		String content = engine.getTemplate(template).renderToString(data);
		return content;
	}

	/**
	 * 创建BaseModel代码
	 *
	 * @param tableName
	 * @param className
	 * @return
	 */
	public String createBaseModelCode(String tableName, String className) {
		// model 所使用的包名 (MappingKit 默认使用的包名)
		String modelPackageName = className.substring(0, className.lastIndexOf("."));
		// base model 所使用的包名
		String baseModelPackageName = modelPackageName + ".base";

		Record record = new Record();
		record.set("pageNumber", "1").set("pageSize", "1").set("name", tableName);
		@SuppressWarnings("unchecked")
		List<TableMeta> tableMetas = (List<TableMeta>) queryTablesList(record).getList();

		String template = "/com/jfinal/plugin/activerecord/generator/base_model_template.jf";
		Kv data = Kv.by("baseModelPackageName", baseModelPackageName);
		data.set("generateChainSetter", true);
		data.set("tableMeta", tableMetas.get(0));
		String content = engine.getTemplate(template).renderToString(data);
		return content;
	}

	/**
	 * 下载文件
	 *
	 * @param type
	 * @param className
	 * @param packageName
	 * @param record
	 * @throws IOException
	 */
	@SuppressWarnings("static-access")
	public void downloadFile(String type, String className, String packageName, Record record) throws IOException {
		GeneratorKit genratorKit = GeneratorKit.getInstance();
		String portal = packageName.split("\\.")[2];
		String modular = packageName.substring(packageName.indexOf(portal));
		//设置package
		genratorKit.setBasePackage(packageName.substring(0, packageName.indexOf(portal)));
		//设置模块
		genratorKit.setModular(modular.replace(".", "/"));
		List<String> listCode = record.get(type);
		String[] codeName = record.getStr("codeName").split(",");
		//后端代码
		if ("codeJava".equals(type)) {
			for (String str : codeName) {
				genratorKit.createJavaFile(className, str, record.getStr(str));
			}
		}
		//前端代码
		else if ("codeHtml".equals(type)) {
			for (String str : codeName) {
				genratorKit.createHtmlFile(className, str, record.getStr(str));
			}
		}
		//下载所有代码
		else {
			listCode = record.get("codejava");
			for (String str : listCode) {
				genratorKit.createJavaFile(className, str, record.getStr(str));
			}
			listCode = record.get("codehtml");
			for (String str : listCode) {
				genratorKit.createHtmlFile(className, str, record.getStr(str));
			}
		}

	}
}
