package com.gen.portal.index.controller;

import com.gen.common.base.BaseController;
import com.gen.portal.index.service.IndexService;
import com.jfinal.aop.Inject;
import com.jfinal.core.Path;
import com.jfinal.kit.JsonKit;

import java.util.Map;

/**
 * 首页控制器
 *
 * @author admin
 */
@Path(value = "/", viewPath = "/portal/index")
public class IndexController extends BaseController {

	@Inject
	IndexService indexService;

	/**
	 * 首页
	 * 加载静态菜单栏
	 */
	public void index() {
		Map<String, Object> menuInfo = indexService.getMenuInfo();
		setAttr("menuId", "sys");
		setAttr("menu", menuInfo.get("menu"));
		setAttr("topMenuList", menuInfo.get("topMenuList"));
		setAttr("funcList", JsonKit.toJson(indexService.getFunctionTree()));
		render("index.html");
	}
}
