package cn.ffcs.wisdom.city.sqlite;

import android.content.Context;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.sqlite.BaseDBHelper;

/**
 * <p>Title: DBHelper       </p>
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
public class DBHelper extends BaseDBHelper {

	public DBHelper(Context context) {

		super(context, context.getString(R.string.db_name), Integer.valueOf(context
				.getString(R.string.db_version)));
	}

	@Override
	public String[] getDBTables() {
		String [] tables = mContext.getResources().getStringArray(R.array.db_tables);
		return tables;
	}
}
