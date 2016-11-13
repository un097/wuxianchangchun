package cn.ffcs.surfingscene.sqlite;

import android.content.Context;
import cn.ffcs.surfingscene.R;
import cn.ffcs.wisdom.sqlite.BaseDBHelper;

public class DBHelper extends BaseDBHelper {

	public DBHelper(Context context) {
		super(context, context.getString(R.string.eyes_db_name), Integer.valueOf(context
				.getString(R.string.eyes_db_version)));
	}

	@Override
	public String[] getDBTables() {
		return mContext.getResources().getStringArray(R.array.eyes_db_tables);
	}
}