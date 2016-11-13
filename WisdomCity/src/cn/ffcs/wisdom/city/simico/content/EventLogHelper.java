package cn.ffcs.wisdom.city.simico.content;

import java.util.ArrayList;

import cn.ffcs.wisdom.city.simico.api.model.EventLog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class EventLogHelper {

	private static final String CLOUNM_ID = "_id";
	private static final String CLOUNM_USER = "user";
	private static final String CLOUNM_AREA_ID = "area_id";
	private static final String CLOUNM_ITEM_TYPE = "item_type";
	private static final String CLOUNM_ITEM_ID = "item_id";
	private static final String CLOUNM_SUB_ITEM_ID = "sub_item_id";
	private static final String CLOUNM_DESC = "desc";
	private static final String CLOUNM_TIMESTAMP = "timestamp";
	private static final String CLOUNM_FIELD1 = "field1";
	private static final String CLOUNM_FIELD2 = "field2";
	private static final String CLOUNM_STATUS = "status";
	private static final String CLOUNM_CITY_CODE = "city_code";

	private static final String[] PROJECTION = new String[] { CLOUNM_ID,
			CLOUNM_USER, CLOUNM_AREA_ID, CLOUNM_ITEM_TYPE, CLOUNM_ITEM_ID,
			CLOUNM_SUB_ITEM_ID, CLOUNM_DESC, CLOUNM_TIMESTAMP, CLOUNM_FIELD1,
			CLOUNM_FIELD2, CLOUNM_STATUS, CLOUNM_CITY_CODE };

	private static final String TABLE_NAME = "event_log_table";

	private static final String QUERY_SECTION = "timestamp <? and user=?";

	private Context mContext;
	private EsDatabaseHelper.DatabaseWrapper mDb;

	public EventLogHelper(Context context) {
		mContext = context;
		mDb = null;
	}

	public Context getContext() {
		return mContext;
	}

	public void initialize() {
		if (mDb == null) {
			mDb = EsDatabaseHelper.getDatabaseHelper(mContext)
					.getWritableDatabaseWrapper();
		}
	}

	public void beginTransaction() {
		initialize();
		mDb.beginTransaction();
	}

	public void setTransactionSuccessful() {
		mDb.setTransactionSuccessful();
	}

	public void endTransaction() {
		if (mDb != null)
			mDb.endTransaction();
	}

	public void yieldTransaction() {
		mDb.yieldTransaction();
	}

	// 向数据库插入一条日志信息
	public void addEventLog(EventLog event) {
		ContentValues values = new ContentValues();
		values.put(CLOUNM_USER, event.getUser() == null?"":event.getUser());
		values.put(CLOUNM_AREA_ID, event.getAreaId());
		values.put(CLOUNM_ITEM_TYPE, event.getItemType());
		values.put(CLOUNM_ITEM_ID, event.getItemId());
		values.put(CLOUNM_SUB_ITEM_ID, event.getSubItemId());
		values.put(CLOUNM_DESC, event.getDesc());
		values.put(CLOUNM_TIMESTAMP, event.getTimestamp());
		values.put(CLOUNM_FIELD1, event.getField1());
		values.put(CLOUNM_FIELD2, event.getField2());
		values.put(CLOUNM_STATUS, event.getStatus() + "");
		values.put(CLOUNM_CITY_CODE, event.getCityCode());
		mDb.insert(TABLE_NAME, null, values);
	}

	// 删除一条日志信息
	public int deleteEventLogById(int id) {
		return mDb.delete(TABLE_NAME, "_id=?",
				new String[] { String.valueOf(id) });
	}

	/**
	 * 删除某个时间点钱的行为记录
	 * 
	 * @param timestamp
	 * @return
	 */
	public int deleteEventLogBeforeTime(String user, long timestamp) {
		if (user == null)
			user = "";
		return mDb.delete(TABLE_NAME, CLOUNM_TIMESTAMP + "<? and "
				+ CLOUNM_USER + "=?", new String[] { String.valueOf(timestamp),
				user });
	}

	// 获取某个时间前的日志信息
	public ArrayList<EventLog> queryEventLog(String user, long timestamp) {
		if (user == null)
			user = "";
		ArrayList<EventLog> events = new ArrayList<EventLog>();
		Cursor cursor = null;
		try {
			cursor = mDb.query(
					TABLE_NAME,
					PROJECTION,
					QUERY_SECTION,
					new String[] { String.valueOf(timestamp),
							String.valueOf(user) }, null, null, null);

			if (cursor != null) {
				if (cursor.moveToFirst()) {
					do {
						EventLog event = new EventLog();
						event.setAreaId(cursor.getString(cursor
								.getColumnIndex(CLOUNM_AREA_ID)));
						event.setDesc(cursor.getString(cursor
								.getColumnIndex(CLOUNM_DESC)));
						event.setField1(cursor.getString(cursor
								.getColumnIndex(CLOUNM_FIELD1)));
						event.setField2(cursor.getString(cursor
								.getColumnIndex(CLOUNM_FIELD2)));
						event.setItemId(cursor.getString(cursor
								.getColumnIndex(CLOUNM_ITEM_ID)));
						event.setItemType(cursor.getString(cursor
								.getColumnIndex(CLOUNM_ITEM_TYPE)));
						event.setStatus(cursor.getInt(cursor
								.getColumnIndex(CLOUNM_STATUS)));
						event.setSubItemId(cursor.getString(cursor
								.getColumnIndex(CLOUNM_SUB_ITEM_ID)));
						event.setTimestamp(cursor.getString(cursor
								.getColumnIndex(CLOUNM_TIMESTAMP)));
						event.setUser(cursor.getString(cursor
								.getColumnIndex(CLOUNM_USER)));
						event.setCityCode(cursor.getString(cursor
								.getColumnIndex(CLOUNM_CITY_CODE)));
						events.add(event);
					} while (cursor.moveToNext());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return events;
	}
}
