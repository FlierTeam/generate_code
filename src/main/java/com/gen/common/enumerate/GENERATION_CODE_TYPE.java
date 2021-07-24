package com.gen.common.enumerate;

/**
 * 生成代码类型枚举
 *
 * @author admin
 */
public enum GENERATION_CODE_TYPE {
	/**
	 * 通用模板生成
	 */
	GENERIC(1),
	/**
	 * JFinal_LayUi 模板生成
	 */
	JFINAL_LAYUI(2),
	/**
	 * 编辑模板生成
	 */
	EDIT_TEMPLATE(3);

	public int value;

	GENERATION_CODE_TYPE(int value) {
		this.value = value;
	}

	public static GENERATION_CODE_TYPE valueOf(int value) {
		for (GENERATION_CODE_TYPE type : GENERATION_CODE_TYPE.values()) {
			if (type.value == value) {
				return type;
			}
		}
		throw new RuntimeException("没有找到对应的枚举类型");
	}
}
