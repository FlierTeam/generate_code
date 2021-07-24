package com.gen.portal.index.service;

import com.gen.common.base.service.BaseService;
import com.gen.common.vo.TreeNode;
import com.jfinal.plugin.activerecord.Model;

import java.util.*;

/**
 * @author admin
 */
public class IndexService extends BaseService {

	@Override
	public Model<?> getDao() {
		return null;
	}

	/**
	 * 获取顶部块
	 */
	public Map<String, Object> getMenuInfo() {
		Map<String, Object> resultMap = new HashMap<>(2);
		List topMenuList = new ArrayList();
		Map topMenuMap = new HashMap(16);
		topMenuMap.put("order_no", 1);
		topMenuMap.put("parent_code", "frame");
		topMenuMap.put("func_type", 0);
		topMenuMap.put("is_window_open", 0);
		topMenuMap.put("id", "sys");
		topMenuMap.put("is_stop", 0);
		topMenuMap.put("func_name", "业务管理");
		topMenuMap.put("spread", 1);
		topMenuList.add(topMenuMap);

		Map menuMap = new HashMap(16);
		menuMap.put("funcName", "业务管理");
		menuMap.put("funcType", 0);
		menuMap.put("id", "sys");
		menuMap.put("isStop", 0);
		menuMap.put("isWindowOpen", 0);
		menuMap.put("orderNo", 1);
		menuMap.put("parentCode", "frame");
		menuMap.put("parentName", "企业级代码生成器");
		menuMap.put("spread", 1);

		resultMap.put("topMenuList", topMenuList);
		resultMap.put("menu", menuMap);

		return resultMap;
	}

	/**
	 * 构建菜单栏树节点
	 */
	public Collection<TreeNode> getFunctionTree() {
		List<TreeNode> nodes = new ArrayList<TreeNode>();

		TreeNode universalNode = createMenuNode(
				"universal_generate",
				"sys", "通用代码生成器",
				"/generator/code",
				"layui-icon-fonts-code",
				false,
				0,
				new ArrayList<TreeNode>());
		nodes.add(universalNode);

		TreeNode jfinalLayuiNode = createMenuNode(
				"jfinal_layui",
				"sys",
				"Jfinal-Layui生成器",
				"/generator/code/jfinalLayui",
				"layui-icon-fonts-code",
				false,
				0,
				new ArrayList<TreeNode>());
		nodes.add(jfinalLayuiNode);

		TreeNode springLayuiNode = createMenuNode(
				"spring_layui",
				"sys",
				"可修改模板的生成器",
				"/generator/code/editTemplate",
				"layui-icon-fonts-code",
				false,
				0,
				new ArrayList<TreeNode>());
		nodes.add(springLayuiNode);
		return nodes;
	}

	/**
	 * 构建树节点
	 *
	 * @param id
	 * @param pid
	 * @param text
	 * @param url
	 * @param icon
	 * @param spread
	 * @param isWindowOpen
	 * @param children
	 * @return
	 */
	public TreeNode createMenuNode(String id, String pid, String text, String url, String icon,
	                               boolean spread, int isWindowOpen, ArrayList<TreeNode> children) {
		TreeNode node = new TreeNode();
		node.setId(id);
		node.setPid(pid);
		node.setText(text);
		node.setUrl(url);
		node.setIcon(icon);
		node.setSpread(spread);
		node.setIsWindowOpen(isWindowOpen);
		node.setChildren(children);
		return node;
	}
}
