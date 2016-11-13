package cn.ffcs.wisdom.sqlite;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import cn.ffcs.wisdom.tools.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * <p>Title: BaseDBHelper       </p>
 * <p>Description: 
 *  提供创建和删除表
 * </p>
 * <p>@author: Eric.wsd                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-1-10             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public abstract class BaseDBHelper extends OrmLiteSqliteOpenHelper {
	public Context mContext;

	public BaseDBHelper(Context context, String dbName, int version) {
		super(context, dbName, null, version);
		this.mContext = context;
	}
	
	/**
	 * 获取数据库表
	 * @return
	 */
	public abstract String[] getDBTables();

	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		// 初始化数据库表
		createTables();
	}

	/**
	 * 初始化所有的数据库表
	 */
	private void createTables() {
		try {
			String[] tables = getDBTables();
			if (tables != null) {
				Log.i(".............begin creating table.............");
				for (int i = 0; i < tables.length; i++) {
					Class<?> cls = Class.forName(tables[i]);
					TableUtils.createTable(connectionSource, cls);
					Log.i("create table " + i + " : " + tables[i] + " successfully!");
				}
				Log.i("..............end creating table..............");
			}
		} catch (ClassNotFoundException e) {
			Log.e("table class is not found." + e);
		} catch (SQLException e) {
			Log.e("can't create table .\n" + e);
		}
	}

	/**
	 * Drop 掉所有的表格
	 */
	private void dropTables() {
		try {
			String[] tables = getDBTables();
			if (tables != null) {
				Log.i("..............begin drop table................");
				for (int i = 0; i < tables.length; i++) {
					Class<?> cls = Class.forName(tables[i]);
					TableUtils.dropTable(connectionSource, cls, true);
					Log.i("drop table " + i + " : " + tables[i] + " successfully!");
				}
				Log.i("................end drop table................");
			}
		} catch (ClassNotFoundException e) {
			Log.e("table class is not found." + e);
		} catch (SQLException e) {
			Log.e("can't create table .\n" + e);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, ConnectionSource arg1, int oldVersion, int newVersion) {
		dropTables();
		createTables();
	}
}
