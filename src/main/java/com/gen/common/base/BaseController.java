

package com.gen.common.base;

import com.alibaba.fastjson.JSONObject;
import com.gen.common.vo.Feedback;
import com.jfinal.core.Controller;
import com.jfinal.core.NotAction;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Record;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BaseController extends Controller {

	/**
	 * 输出提示信息到页面
	 *
	 * @param message
	 */
	@NotAction
	public void setMsg(String message) {
		setAttr("msg", message);
	}

	/**
	 * {
	 * "code": "success",
	 * "success": true,
	 * "error": false,
	 * "msg": "成功"
	 * }
	 *
	 * @return
	 */
	@NotAction
	public Feedback suc() {
		return Feedback.success("成功");
	}

	/**
	 * {
	 * "code": "success",
	 * "success": true,
	 * "error": false,
	 * "msg": ""
	 * }
	 *
	 * @param msg 提示信息
	 * @return
	 */
	@NotAction
	public Feedback suc(String msg) {
		return Feedback.success(msg);
	}

	/**
	 * {
	 * "code": "error",
	 * "success": false,
	 * "error": true,
	 * "msg": "失败"
	 * }
	 *
	 * @return
	 */
	@NotAction
	public Feedback err() {
		return Feedback.error("失败");
	}

	/**
	 * {
	 * "code": "error",
	 * "success": false,
	 * "error": true,
	 * "msg": ""
	 * }
	 *
	 * @param msg 提示信息
	 * @return
	 */
	@NotAction
	public Feedback err(String msg) {
		return Feedback.error(msg);
	}

	/**
	 * 获取数组变量ids
	 *
	 * @return
	 * @author admin
	 * @date 2018年8月1日
	 */
	@NotAction
	public List<String> getIds() {
		return getArray("ids");
	}

	/**
	 * 获取数组变量
	 *
	 * @param arrayName
	 * @return
	 * @author admin
	 * @date 2018年9月17日
	 */
	@NotAction
	public List<String> getArray(String arrayName) {
		List<String> ids = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			if (getPara(arrayName + "[" + i + "]") == null) {
				break;
			}
			ids.add(getPara(arrayName + "[" + i + "]"));
		}
		return ids;
	}


	/**
	 * 返回接口成功数据
	 *
	 * @param data
	 * @return
	 * @author admin
	 * @date 2019年3月6日
	 */
	@NotAction
	public Ret ok(Object data) {
		return Ret.ok("msg", "成功").set("data", data);
	}

	@NotAction
	public Ret ok() {
		return Ret.ok("msg", "成功");
	}

	@NotAction
	public Ret fail() {
		return Ret.fail("msg", "失败");
	}


	/**
	 * 获取请求参数,转化为JSONObject
	 *
	 * @return
	 */
	@NotAction
	public JSONObject getAllParamsToJson() {
		JSONObject result = new JSONObject();
		Map<String, String[]> map = getParaMap();
		Set<String> keySet = map.keySet();
		for (String key : keySet) {
			if (map.get(key) instanceof String[]) {
				String[] value = map.get(key);
				if (value.length == 0) {
					result.put(key, null);

				} else if (value.length == 1 && key.indexOf("[]") < 0) {
					result.put(key, value[0]);
				} else {
					result.put(key, value);
				}
			} else {
				result.put(key, map.get(key));
			}
		}
		return result;
	}

	/**
	 * 获取请求参数,转化为JFinal的Record对象
	 *
	 * @return
	 */
	@NotAction
	public Record getAllParamsToRecord() {
		@SuppressWarnings("unchecked")
		Record result = new Record().setColumns(getKv());
		result.remove("_jfinal_token");
		return result;
	}

	/**
	 * 构造Kv对象
	 *
	 * @param key
	 * @param value
	 * @return
	 */
	@NotAction
	public Kv byKv(Object key, Object value) {
		return Kv.by(key, value);
	}
}
