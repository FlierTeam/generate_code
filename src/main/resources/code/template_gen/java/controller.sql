/**Controller.java代码模板*/
#sql("controller")
#[[
package ${controllerPackage};

import com.lx.gen.base.BaseController;
import com.lx.gen.common.IdKit;
import com.lx.gen.common.sql.Like;
import com.lx.gen.common.sql.OrderBy;
import com.lx.gen.model.${modelName};
import ${servicePackage};
import com.jfinal.aop.Inject;
import com.jfinal.core.Path;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;

/**
 * ${tableComment}${actionKey}
 *
 * @author ${author}
 * @date ${date}
 */
@Path("${actionKey}")
public class ${modelName}Controller extends BaseController {

	@Inject
	${modelName}Service ${lowercaseModelName}Service;

	/**
	 * ${tableComment}页面
	 */
	public void listHtml() {
		render("list.html");
	}

	/**
	 * ${tableComment}列表
	 */
	public void list() {
		int page = getInt("page", 1);
		int limit = getInt("limit", 20);
		String name = get("name", null);
		Like like = Like.by(${modelName}._Name, name);
		Page<${modelName}> list = ${lowercaseModelName}Service.search( page, limit, like,
				OrderBy.byDesc(${modelName}._Pri));
		renderJson(Ret.ok("data", list));
	}

	/**
	 * 删除
	 */
	public void delete() {
		String id = get("id");
		if (StrKit.isBlank(id)) {
			renderJson(Ret.fail("msg", "${tableComment}为空!"));
			return;
		}
		boolean b = ${lowercaseModelName}Service.deleteById(${modelName}.dao, id);
		if (b) {
			logger.info(getL("删除成功").subject("用户操作"));
			renderJson(Ret.ok());
			return;
		} else {
			logger.error(getL("删除失败").subject("用户操作"));
			renderJson(Ret.fail("msg", "${tableComment}删除失败"));
			return;
		}
	}

	/**
	 * 添加页
	 */
	public void addHtml() {
		render("add.html");
	}

	/**
	 * 编辑页
	 */
	public void editHtml() {
		String id = get("id");
		if (StrKit.isBlank(id)) {
			renderJson(Ret.fail("msg", "参数不全"));
			return;
		}
		${modelName} data = ${lowercaseModelName}Service.findById(id);
		if (data == null) {
			renderJson(Ret.fail("msg", "数据不存在"));
			return;
		}
		set("data", data);
		render("edit.html");
	}

	/**
	 * 添加或编辑
	 */
	public void add() {
		String id = get("id");
        ${modelName} model = getModel(${modelName}.class, "");
        model.setId(IdKit.simpleNumberId());
        ${lowercaseModelName}Service.add(model.setEnable(true));
        logger.info(getL("新增成功").subject("用户操作"));
        renderJson(Ret.ok());
        return;
	}

	/**
	 * 添加或编辑
	 */
	public void edit() {
        String id = get("id");
        ${modelName} model = ${lowercaseModelName}Service.findById(id);
        if (model == null) {
            logger.error(getL("修改失败").subject("用户操作"));
            return;
        }
        model = getModel(${modelName}.class, "");
        ${lowercaseModelName}Service.update(model);
        logger.info(getL("修改成功").subject("用户操作"));
        renderJson(Ret.ok());
        return;
	}

	/**
	 * 启用与禁用
	 */
	public void enable() {
		String id = get("id");
		if (StrKit.isBlank(id)) {
			renderJson(Ret.fail("msg", "${tableComment}为空!"));
			return;
		}
		${modelName} model = ${lowercaseModelName}Service.findById(id);
		if (model == null) {
			logger.error(getL("${tableComment}操作失败").subject("用户操作"));
			renderJson(Ret.fail("msg", "${tableComment}不存在!"));
			return;
		}
		logger.error(getL("${tableComment}操作成功").subject("用户操作"));
		if (model.getEnable()) {
			model.setEnable(false);
		} else {
			model.setEnable(true);
		}
		model.update();
		renderJson(Ret.ok());
		return;
	}
}
]]#
#end
