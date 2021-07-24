
package com.gen.common.base.service;

import com.gen.common.kit.SqlKit;
import com.gen.common.vo.Grid;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.*;

import java.util.List;


/**
 * 没有model类的service接口
 *
 * @author admin
 * @date 2019年3月13日
 */
public abstract class DbService {


	/**
	 * 指定表
	 *
	 * @return
	 * @author admin
	 * @date 2019年3月14日
	 */
	public abstract String getTable();

	/**
	 * 指定主键
	 *
	 * @return
	 * @author admin
	 * @date 2019年3月14日
	 */
	public abstract String getPrimaryKey();

	/**
	 * 指定数据源<br/>
	 *
	 * @return configName 若return null，则使用主数据源
	 * @author admin
	 * @date 2019年3月13日
	 */
	public String getDb() {
		return null;
	}

	;

	/**
	 * 获取DBPro数据源
	 *
	 * @return
	 */
	private DbPro getDbPro() {
		if (getDb() != null) {
			return Db.use(getDb());
		}
		return Db.use();
	}

	public List<Record> findAll() {
		SqlPara sp = Db.getSqlPara("crud.find", Kv.by("tableName", getTable()).set("cond", new Kv()));
		return getDbPro().find(sp);
	}

	/**
	 * 无条件查询
	 *
	 * @param tableName
	 * @author admin
	 * @date 2019年3月13日
	 */
	public List<Record> find(String tableName) {
		SqlPara sp = Db.getSqlPara("crud.find", Kv.by("tableName", tableName).set("cond", new Kv()));
		return getDbPro().find(sp);
	}


	/**
	 * 条件查询
	 *
	 * @param tableName 表名
	 * @param columns   字段集合
	 * @author admin
	 * @date 2019年3月13日
	 */
	public List<Record> find(String tableName, Kv columns) {
		SqlPara sp = Db.getSqlPara("crud.find", Kv.by("tableName", tableName).set("cond", getKv(columns)));
		return getDbPro().find(sp);
	}

	public List<Record> find(Kv columns) {
		SqlPara sp = Db.getSqlPara("crud.find", Kv.by("tableName", getTable()).set("cond", getKv(columns)));
		return getDbPro().find(sp);
	}

	public List<Record> find(String tableName, Kv columns, String groupOrderBySql) {
		SqlPara sp = Db.getSqlPara("crud.find", Kv.by("tableName", tableName).set("cond", getKv(columns)).set("groupOrder", groupOrderBySql));
		return getDbPro().find(sp);
	}

	public List<Record> find(Kv columns, String groupOrderBySql) {
		SqlPara sp = Db.getSqlPara("crud.find", Kv.by("tableName", getTable()).set("cond", getKv(columns)).set("groupOrder", groupOrderBySql));
		return getDbPro().find(sp);
	}

	public List<Record> find(String sql, Object... paras) {
		return getDbPro().find(sql, paras);
	}

	public Record findById(String id) {
		String sql = "select * from " + getTable() + " where " + getPrimaryKey() + "=?";
		List<Record> list = getDbPro().find(sql, id);
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	public Record findPk(String pk, Object value) {
		String sql = "select * from " + getTable() + " where " + pk + "=?";
		List<Record> list = getDbPro().find(sql, value);
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * 根据某字段查询是否存在数据
	 *
	 * @param key
	 * @param paras
	 * @return
	 */
	public boolean isExit(String key, Object paras) {
		String sql = "select * from " + getTable() + " where " + key + "=?";
		List<Record> list = getDbPro().find(sql, paras);
		if (list.size() > 0) {
			return true;
		}

		return false;
	}

	/**
	 * 分页条件查询
	 *
	 * @param pageNumber
	 * @param pageSize
	 * @param tableName
	 * @param columns
	 * @author admin
	 * @date 2019年3月13日
	 */
	public Grid page(int pageNumber, int pageSize, String tableName, Kv columns) {
		SqlPara sp = Db.getSqlPara("crud.find", Kv.by("tableName", tableName).set("cond", getKv(columns)));
		return getGrid(getDbPro().paginate(pageNumber, pageSize, sp));
	}

	public Grid page(int pageNumber, int pageSize, Kv columns) {
		SqlPara sp = Db.getSqlPara("crud.find", Kv.by("tableName", getTable()).set("cond", getKv(columns)));
		return getGrid(getDbPro().paginate(pageNumber, pageSize, sp));
	}

	/**
	 * 分页查询，分组、排序
	 *
	 * @param pageNumber
	 * @param pageSize
	 * @param tableName
	 * @param columns
	 * @param groupOrderBySql
	 * @author admin
	 * @date 2019年3月13日
	 */
	public Grid page(int pageNumber, int pageSize, String tableName, Kv columns, String groupOrderBySql) {
		SqlPara sp = Db.getSqlPara("crud.find", Kv.by("tableName", tableName).set("cond", getKv(columns)).set("groupOrder", groupOrderBySql));
		return getGrid(getDbPro().paginate(pageNumber, pageSize, sp));
	}

	public Grid page(int pageNumber, int pageSize, Kv columns, String groupOrderBySql) {
		SqlPara sp = Db.getSqlPara("crud.find", Kv.by("tableName", getTable()).set("cond", getKv(columns)).set("groupOrder", groupOrderBySql));
		return getGrid(getDbPro().paginate(pageNumber, pageSize, sp));
	}

	public Grid page(int pageNumber, int pageSize, String select, String sqlExceptSelect) {
		return getGrid(getDbPro().paginate(pageNumber, pageSize, select, sqlExceptSelect));
	}

	public Grid page(int pageNumber, int pageSize, String select, String sqlExceptSelect, Object... paras) {
		return getGrid(getDbPro().paginate(pageNumber, pageSize, select, sqlExceptSelect, paras));
	}


	/**
	 * Save record with default primary key.
	 *
	 * <pre>
	 * Example:
	 * Record record = new Record().set("id", 123).set("name", 456);
	 * Db.use().save("user_role", userRole);
	 * </pre>
	 *
	 * @param tableName
	 * @param record
	 * @author admin
	 * @date 2019年3月13日
	 */
	public boolean save(String tableName, Record record) {
		return getDbPro().save(tableName, record);
	}

	public boolean save(Record record) {
		return getDbPro().save(getTable(), record);
	}

	/**
	 * Save record.
	 * <pre>
	 * Example:
	 * Record userRole = new Record().set("user_id", 123).set("role_id", 456);
	 * Db.use().save("user_role", "user_id, role_id", userRole);
	 * </pre>
	 *
	 * @param tableName  the table name of the table
	 * @param primaryKey the primary key of the table, composite primary key is separated by comma character: ","
	 * @param record     the record will be saved
	 * @author admin
	 * @date 2019年3月13日
	 */
	public boolean save(String tableName, String primaryKey, Record record) {
		return getDbPro().save(tableName, primaryKey, record);
	}

	/**
	 * Update record with default primary key.
	 *
	 * @param tableName
	 * @param record
	 * @return
	 * @author admin
	 * @date 2019年3月13日
	 */
	public boolean update(String tableName, Record record) {
		return getDbPro().update(tableName, record);
	}

	public boolean update(Record record) {
		return getDbPro().update(getTable(), record);
	}

	public int update(Kv data, Kv whereData) {
		Kv templateData = Kv.by("tableName", getTable()).set("cond", data).set("whereCond", getKv(data));
		return getDbPro().update(Db.getSqlPara("crud.update", templateData));
	}

	/**
	 * Update Record.
	 * <pre>
	 * Example:
	 * Db.use().update("user_role", "user_id, role_id", record);
	 * </pre>
	 *
	 * @param tableName  the table name of the Record save to
	 * @param primaryKey the primary key of the table, composite primary key is separated by comma character: ","
	 * @param record     the Record object
	 * @author admin
	 * @date 2019年3月13日
	 */
	public boolean update(String tableName, String primaryKey, Record record) {
		return getDbPro().update(tableName, primaryKey, record);
	}

	public int update(String sql) {
		return getDbPro().update(sql);
	}

	public int update(String sql, Object... paras) {
		return getDbPro().update(sql, paras);
	}

	/**
	 * Delete Record by primary key
	 *
	 * @param tableName
	 * @param primaryKey
	 * @param list
	 * @return
	 * @author admin
	 * @date 2019年3月13日
	 */
	public boolean deleteByIds(String tableName, String primaryKey, List<String> list) {
		String sql = "delete from " + tableName + " where " + primaryKey + " in " + SqlKit.joinIds(list);
		return getDbPro().delete(sql) > 0;
	}

	/**
	 * Delete Record width  primary key id
	 *
	 * @param tableName
	 * @param list
	 * @return
	 * @author admin
	 * @date 2019年3月13日
	 */
	public boolean deleteByIds(String tableName, List<String> list) {
		String sql = "delete from " + tableName + " where id in " + SqlKit.joinIds(list);
		return getDbPro().delete(sql) > 0;
	}

	public boolean deleteByIds(List<String> list) {
		String sql = "delete from " + getTable() + " where id in " + SqlKit.joinIds(list);
		return getDbPro().delete(sql) > 0;
	}

	public boolean deleteByIds(List<String> list, String primaryKey) {
		String sql = "delete from " + getTable() + " where " + primaryKey + " in " + SqlKit.joinIds(list);
		return getDbPro().delete(sql) > 0;
	}

	public int delete(Kv data) {
		Kv templateData = Kv.by("tableName", getTable()).set("cond", data);
		return getDbPro().update(Db.getSqlPara("crud.delete", templateData));
	}

	/**
	 * 封装layui分页参数
	 *
	 * @param page
	 * @return
	 * @author admin
	 * @date 2019年3月13日
	 */
	private Grid getGrid(Page<Record> page) {
		return new Grid(page.getList(), page.getPageNumber(), page.getPageSize(), page.getTotalRow());
	}

	/**
	 * 过滤查询条件
	 *
	 * @param columns
	 * @return
	 * @author admin
	 * @date 2019年3月13日
	 */
	private Kv getKv(Kv columns) {
		Kv kv = new Kv();
		for (Object key : columns.keySet()) {
			if (columns.get(key) != null && !columns.get(key).equals("")) {
				if (key.toString().toLowerCase().endsWith("like")) {
					kv.set(key, "%" + columns.get(key) + "%");
					continue;
				}
				kv.set(key, columns.get(key));
			}
		}
		return kv;
	}

}
