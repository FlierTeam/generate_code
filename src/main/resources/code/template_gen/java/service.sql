/**service.java代码模板*/
#sql("service")
#[[
package ${servicePackage};

import com.lx.gen.base.BaseService;
import com.lx.gen.model.${modelName};
import com.jfinal.plugin.activerecord.Page;

import java.util.List;

public class ${modelName}Service extends BaseService<${modelName}> {

	@Override
	public ${modelName} findById(Object id) {
		return super.findById(${modelName}.dao, id);
	}

	/**
	 * 增加记录
	 *
	 * @param model
	 * @return
	 */
	public boolean add(${modelName} model) {
		return model.save();
	}

	/**
	 * 更新记录
	 *
	 * @param model
	 * @return
	 */
	public boolean update(${modelName} model) {
		return model.update();
	}

}
]]#
#end
