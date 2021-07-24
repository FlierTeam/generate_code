
package com.gen.portal.generator.kit;

import com.gen.common.config.MainConfig;
import com.gen.common.config.WebContant;
import com.gen.common.enumerate.GENERATION_CODE_TYPE;
import com.jfinal.kit.Kv;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.DbKit;
import com.jfinal.plugin.activerecord.sql.SqlKit;
import com.jfinal.template.Template;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 代码生成器
 *
 * @author admin
 * @date 2020-02-21
 */
public class GeneratorKit {

	public GeneratorKit() {
	}

	/************************************ constant ***************************************/
	/**
	 * 模板类型
	 */
	public static final String JAVA = "java";
	public static final String HTML = "html";
	public static final String separator = "/";

	private static final String point = ".";
	private static final String author = "admin";
	private static final String javaResource = "src/main/java";
	private static final String htmlResource = "src/main/webapp/WEB-INF/views";

	/**
	 * packager名称
	 */
	private static String basePackage = "com.gen";
	private static String modular = "model";
	private static final String ctrl = "controller";
	private static final String service = "service";

	/**
	 * 文件package目录
	 * package样例：com.gen.portal.business.ctrl
	 */
	private static String ctrlPackage = "";
	/**
	 * package样例：com.gen.portal.business.service
	 */
	private static String servicePackage = "";

	/**
	 * 文件地址
	 */
	private static String ctrlPath = "";
	private static String servicePath = "";
	private static String modelPath = "";
	private static String baseModelPath = "";

	/**
	 * 模板参数集
	 */
	private static final Kv kv = Kv.by("author", author);

	private static final GeneratorKit single = new GeneratorKit();

	/************************************ config ***************************************/
	/**
	 * 数据库连接类型
	 */
	public static String dbType = MainConfig.p.get("dbType");

	/**
	 * 是否有变更模板
	 */
	public static boolean changed = false;

	/**
	 * 项目模板
	 */
	public static Set<String> currentJava;
	public static Set<String> currentHtml;

	/**
	 * 项目模板
	 */
	public static String[] defaultJava = {"controller", "service"};
	public static String[] defaultHtml = {"index", "add", "edit", "_form"};

	/**
	 * 添加不需要获取的数据表
	 */
	public static String[] excTable = {"sys_user", "sys_role", "sys_user_role", "sys_log"};

	/**
	 * 实体名称去掉前缀
	 */
	public static String[] tableNamePrefixes = {
			"sys_", "t_", "W_", "T_", "w_"
	};

	public static String[] isSystemFileList = {
			"template_gen",
			"html",
			"java",
			"_all_code.sql"
	};

	/**
	 * 当前模板文件集，此文件中引入了所有模板文件
	 */
	public static String currentTemplate = WebContant.modifyCodeTemplate;
	/**
	 * 当前模板文件根路径
	 */
	public static String currentTemplatePath = WebContant.modifySqlTemplatePath;

	/**
	 * 文件生成路径，默认在本地
	 */
	public static String filePath = System.getProperty("user.dir") + separator;

	/************************************ util ***************************************/
	/**
	 * 校验模板文件内容合法
	 * 可以放到前端校验
	 *
	 * @param fileName
	 * @param fileContents
	 * @return
	 */
	public static boolean checkLegal(String fileName, String fileContents) {
		String start = "#sql(\"" + fileName.substring(0, fileName.indexOf('.')) + "\")";
		String end = "#end";
		if (fileContents.indexOf(start) == -1 || fileContents.indexOf(end) == -1) {
			return false;
		}
		return true;
	}

	/**
	 * 检查文件名后缀、补充
	 * 可以放到前端补充
	 *
	 * @param fileName
	 * @return
	 */
	public static String checkSuffix(String fileName) {
		String suffix = ".sql";
		if (!fileName.contains(suffix)) {
			fileName += suffix;
		}
		return fileName;
	}

	/**
	 * 检查是否是系统级文件
	 * 为了避免混淆，也不允许操作与系统级文件同名文件
	 *
	 * @param fileName
	 * @return
	 */
	public static boolean isSystemFile(String fileName) {
		for (String name : GeneratorKit.isSystemFileList) {
			if (StrKit.equals(name, fileName)) {
				return true;
			}
		}
		return false;
	}

	/************************************ method ***************************************/

	public static GeneratorKit getInstance() {
		return single;
	}

	/**
	 * 初始化模板文件集合
	 */
	public static void initCache() {
		currentJava = new HashSet<>();
		currentHtml = new HashSet<>();
		refreshCache(JAVA, HTML);
	}

	/**
	 * 模板文件变动时或初始化时，刷新java、html列表缓存
	 */
	public static void refreshCache(String... type) {
		for (String s : type) {
			refreshByType(s);
		}
	}

	/**
	 * 根据类型刷新
	 *
	 * @param type
	 */
	public static void refreshByType(String type) {
		Set<String> currentSet = null;
		String javaPath = PathKit.getRootClassPath() + currentTemplatePath + separator + type;
		if (StrKit.equals(type, JAVA)) {
			currentSet = GeneratorKit.currentJava;
		} else if (StrKit.equals(type, HTML)) {
			currentSet = GeneratorKit.currentHtml;
		}
		currentSet.clear();
		File file = new File(javaPath);
		if (file.isDirectory()) {
			File[] listFiles = file.listFiles();
			for (File f : listFiles) {
				String name = f.getName().substring(0, f.getName().indexOf('.'));
				currentSet.add(name);
			}
		}
	}

	/**
	 * 根据类型转换模板根路径，以及重新加载模板文件
	 *
	 * @param type
	 */
	public static void switchChangeTemplate(int type) {
		String codeTemplate = "";
		String sqlTemplate = "";

		if (GENERATION_CODE_TYPE.GENERIC.value == type) {
			codeTemplate = WebContant.baseTemplate;
			GeneratorKit.currentTemplatePath = WebContant.baseTemplatePath;
		} else if (GENERATION_CODE_TYPE.JFINAL_LAYUI.value == type) {
			codeTemplate = WebContant.defaultCodeTemplate;
			sqlTemplate = WebContant.defaultSqlTemplate;
			GeneratorKit.currentTemplatePath = WebContant.defaultCodeTemplatePath;
		} else if (GENERATION_CODE_TYPE.EDIT_TEMPLATE.value == type) {
			codeTemplate = WebContant.modifyCodeTemplate;
			sqlTemplate = WebContant.modifySqlTemplate;
			GeneratorKit.currentTemplatePath = WebContant.modifySqlTemplatePath;
		} else {
			return;
		}

		changeSqlTemplate(codeTemplate, sqlTemplate);
	}

	/**
	 * 重新加载模板文件
	 *
	 * @param CodeTemplate
	 * @param SqlTemplate
	 */
	public static void changeSqlTemplate(String CodeTemplate, String SqlTemplate) {
		SqlKit sqlKit;
		if (changed) {
			sqlKit = DbKit.getConfig(dbType).getSqlKit();
		} else {
			sqlKit = DbKit.getConfig().getSqlKit();
		}
		Class c = sqlKit.getClass();
		Field SourceList = null;
		Field TemplateMap = null;
		try {
			SourceList = c.getDeclaredField("sqlSourceList");
			TemplateMap = c.getDeclaredField("sqlTemplateMap");
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		TemplateMap.setAccessible(true);
		SourceList.setAccessible(true);
		Map<String, Template> stringTemplateMap = null;
		List sqlSourceList = null;
		try {
			stringTemplateMap = (Map<String, Template>) TemplateMap.get(sqlKit);
			sqlSourceList = (List) SourceList.get(sqlKit);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		sqlSourceList.clear();
		stringTemplateMap.clear();
		if (StrKit.notBlank(CodeTemplate)) {
			sqlKit.addSqlTemplate(CodeTemplate);
		}
		if (StrKit.notBlank(SqlTemplate)) {
			sqlKit.addSqlTemplate(SqlTemplate);
		}
		sqlKit.parseSqlTemplate();
	}

	/**
	 * 根据类型转换当前模板根路径
	 *
	 * @param type
	 */
	public static void pathConversion(int type) {
		if (GENERATION_CODE_TYPE.GENERIC.value == type) {
			GeneratorKit.currentTemplatePath = WebContant.baseTemplatePath;
		} else if (GENERATION_CODE_TYPE.JFINAL_LAYUI.value == type) {
			GeneratorKit.currentTemplatePath = WebContant.defaultCodeTemplatePath;
		} else if (GENERATION_CODE_TYPE.EDIT_TEMPLATE.value == type) {
			GeneratorKit.currentTemplatePath = WebContant.modifySqlTemplatePath;
		} else {
			return;
		}
	}

	/**
	 * 文件基础package，默认值是com.gen.portal
	 *
	 * @param basePackage
	 * @return
	 */
	public GeneratorKit setBasePackage(String basePackage) {
		GeneratorKit.basePackage = basePackage;
		return this;
	}

	/**
	 * 模块package，默认是business
	 *
	 * @param modular
	 * @return
	 */
	public GeneratorKit setModular(String modular) {
		GeneratorKit.modular = modular;
		return this;
	}

	/**
	 * 业务模块
	 *
	 * @return
	 */
	public String getModular() {
		return modular;
	}

	/**
	 * 获取Java模板参数集
	 *
	 * @param className 如：com.gen.common.model.SysUser
	 */
	public static Kv getJavaKv(String className) {
		String cName = getLastChar(className);
		ctrlPackage = basePackage + point + ctrl;
		servicePackage = basePackage + point + service;
		kv.set("controllerPackage", ctrlPackage);
		kv.set("servicePackage", servicePackage);
		kv.set("modelName", cName);
		kv.set("lowercaseModelName", getLowercaseChar(cName));
		kv.set("importModel", className);
		kv.set("actionKey", separator + modular + separator + getLowercaseChar(cName));
		kv.set("date", getDate());
		return kv;
	}

	/**
	 * 创建java文件
	 *
	 * @param modelClassName com.gen.common.model.SysUser
	 * @param fileType       Service,Controller
	 * @return
	 * @throws IOException
	 */
	public static File createJavaFile(String modelClassName, String fileType, String content) throws IOException {
		String modelName = getLastChar(modelClassName);
		String modelPackage = modelClassName.substring(0, modelClassName.lastIndexOf(point));
		//controller类
		ctrlPath =  modular + separator+"java"+ separator + ctrl;
		//service类
		servicePath =  modular + separator+"java"+ separator + service;
		//model类
		modelPath = modular + separator+"java"+ separator + "entity";
		//baseModel类
		baseModelPath = modular + separator+"java"+ separator + "vo";
		String path = GeneratorKit.filePath + htmlResource
				+ separator;
		String fileName = modelName + fileType;
		if ("Service.java".equalsIgnoreCase(fileType)) {
			path += servicePath + separator;
		} else if ("Controller.java".equalsIgnoreCase(fileType)) {
			path += ctrlPath + separator;
		} else if ("Model.java".equals(fileType)) {
			path += modelPath + separator;
			fileName = modelName + ".java";
		} else if ("BaseModel.java".equals(fileType)) {
			path += baseModelPath + separator;
			fileName = "Base" + modelName + ".java";
		} else if ("_MappingKit.java".equals(fileType)) {
			path += modelPath + separator;
			fileName = fileType;
		} else {
			path += fileType + separator;
		}

		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}

		file = new File(path + fileName);
		writeContentToFile(file, content);
		return file;
	}

	/**
	 * 创建html文件
	 *
	 * @param className 如：com.gen.common.model.SysUser
	 * @param fName
	 * @return
	 * @throws IOException
	 */
	public static File createHtmlFile(String className, String fName, String content) throws IOException {
		String cName = getLastChar(className);
		String filePath = GeneratorKit.filePath + htmlResource
				+ separator + modular + separator +"html"+ separator+ getLowercaseChar(cName) + separator;

		File file = new File(filePath);
		if (!file.exists()) {
			file.mkdirs();
		}
		file = new File(filePath + fName);
		writeContentToFile(file, content);
		return file;
	}


	/**
	 * 写内容入文件
	 *
	 * @param file
	 * @param content
	 * @throws IOException
	 */
	public static void writeContentToFile(File file, String content) throws IOException {
		System.out.print("正在创建文件：" + file.getPath());
		OutputStreamWriter out = null;
		try {
			out = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
			out.write(content);
			out.flush();
			System.out.println("   ：创建成功");
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	/**
	 * 获取路径的最后面字符串<br>
	 * 如：<br>
	 * str = "com.gen.common.model.SysUser"<br>
	 * return "SysUser";
	 *
	 * @param str
	 * @return
	 */
	private static String getLastChar(String str) {
		if ((str != null) && (str.length() > 0)) {
			int dot = str.lastIndexOf('.');
			if ((dot > -1) && (dot < (str.length() - 1))) {
				return str.substring(dot + 1);
			}
		}
		return str;
	}

	/**
	 * 把第一个字母变为小写<br>
	 * 如：<br>
	 * str = "SysUser";<br>
	 * return "sysUser";
	 *
	 * @param str
	 * @return
	 */
	private static String getLowercaseChar(String str) {
		return str.substring(0, 1).toLowerCase() + str.substring(1);
	}

	/**
	 * 获取系统时间
	 *
	 * @return
	 */
	private static String getDate() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return simpleDateFormat.format(new Date());
	}

	/**
	 * 写文件
	 * @param file
	 * @param fileContents
	 */
	public static void writeFile(File file, String fileContents) {
		FileWriter writer = null;
		BufferedWriter bufferedWriter = null;
		try {
			writer = new FileWriter(file);
			bufferedWriter = new BufferedWriter(writer);
			bufferedWriter.write(fileContents);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bufferedWriter.flush();
				writer.close();
				bufferedWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 读文件
	 * @param file
	 *
	 */
	public static String readFile(File file) {
		String title="";
		Reader reader = null;
		BufferedReader bufferedReader = null;
		try {
			reader = new FileReader(file);
			bufferedReader = new BufferedReader(reader);
			String str = "";
			while ((str = bufferedReader.readLine()) != null) {
				title += str + "\n";
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
				bufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return title;
	}

	/**
	 * zip文件压缩
	 * @param inputFile 待压缩文件夹/文件名
	 * @param outputFile 生成的压缩包名字
	 */

	public static void ZipCompress(String inputFile, String outputFile) throws Exception {
		//创建zip输出流
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outputFile));
		//创建缓冲输出流
		BufferedOutputStream bos = new BufferedOutputStream(out);
		File input = new File(inputFile);
		compress(out, bos, input,null);
		bos.close();
		out.close();
	}
	/**
	 * @param name 压缩文件名，可以写为null保持默认
	 */
	//递归压缩
	public static void compress(ZipOutputStream out, BufferedOutputStream bos, File input, String name) throws IOException {
		if (name == null) {
			name = input.getName();
		}
		//如果路径为目录（文件夹）
		if (input.isDirectory()) {
			//取出文件夹中的文件（或子文件夹）
			File[] flist = input.listFiles();

			if (flist.length == 0)//如果文件夹为空，则只需在目的地zip文件中写入一个目录进入
			{
				out.putNextEntry(new ZipEntry(name + "/"));
			} else//如果文件夹不为空，则递归调用compress，文件夹中的每一个文件（或文件夹）进行压缩
			{
				for (int i = 0; i < flist.length; i++) {
					compress(out, bos, flist[i], name + "/" + flist[i].getName());
				}
			}
		} else//如果不是目录（文件夹），即为文件，则先写入目录进入点，之后将文件写入zip文件中
		{
			out.putNextEntry(new ZipEntry(name));
			FileInputStream fos = new FileInputStream(input);
			BufferedInputStream bis = new BufferedInputStream(fos);
			int len;
			//将源文件写入到zip文件中
			byte[] buf = new byte[1024];
			while ((len = bis.read(buf)) != -1) {
				bos.write(buf,0,len);
			}
			bos.flush();
			bis.close();
			fos.close();
		}
	}

	/**
	 * 递归删除
	 * 删除某个目录及目录下的所有子目录和文件
	 * @param file 文件或目录
	 * @return 删除结果
	 */
	public static boolean delFiles(File file){
		boolean result = false;
		//目录
		if(file.isDirectory()){
			File[] childrenFiles = file.listFiles();
			for (File childFile:childrenFiles){
				result = delFiles(childFile);
				if(!result){
					return result;
				}
			}
		}
		//删除 文件、空目录
		result = file.delete();
		return result;
	}
}
