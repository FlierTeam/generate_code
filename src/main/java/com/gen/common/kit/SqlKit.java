

package com.gen.common.kit;

import java.util.List;

/**
 * SqlKit
 */
public class SqlKit {

	/**
	 * 将 id 列表 join 起来，用逗号分隔，并且用小括号括起来
	 */
	public static void joinIds(List<Integer> idList, StringBuilder ret) {
		ret.append("(");
		boolean isFirst = true;
		for (Integer id : idList) {
			if (isFirst) {
				isFirst = false;
			} else {
				ret.append(", ");
			}
			ret.append(id.toString());
		}
		ret.append(")");
	}

	/**
	 * 将 id 列表 join 起来，用逗号分隔，并且用小括号括起来
	 *
	 * @param ids
	 * @return
	 * @author admin
	 * @date 2019年3月13日
	 */
	public static String joinIds(List<String> ids) {
		StringBuilder ret = new StringBuilder();
		ret.append("(");
		boolean isFirst = true;
		for (String id : ids) {
			if (isFirst) {
				isFirst = false;
			} else {
				ret.append(", ");
			}
			ret.append("'").append(id.toString()).append("'");
		}
		ret.append(")");
		return ret.toString();
	}
}

