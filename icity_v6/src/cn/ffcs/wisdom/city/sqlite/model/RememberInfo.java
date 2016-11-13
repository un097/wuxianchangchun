package cn.ffcs.wisdom.city.sqlite.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "t_remember_info")
public class RememberInfo {

	@DatabaseField(generatedId = true, columnName = "id")
	public int id;

	@DatabaseField(columnName = "user_name")
	public String userName;

	@DatabaseField(columnName = "password")
	public String password;

	@DatabaseField(columnName = "update_time")
	public Long updateTime;

}
