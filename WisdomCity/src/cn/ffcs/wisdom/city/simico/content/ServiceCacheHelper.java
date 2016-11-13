package cn.ffcs.wisdom.city.simico.content;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import cn.ffcs.wisdom.city.sqlite.model.MenuItem;

public class ServiceCacheHelper {

	private static final String CLOUNM_ID = "_id";
	private static final String CLOUNM_SID = "service_id";
	private static final String CLOUNM_SNAME = "service_name";
	private static final String CLOUNM_SICON = "service_icon";
	private static final String CLOUNM_SPID = "service_pid";
	private static final String CLOUNM_CID = "ctg_id";

	private static final String[] PROJECTION = new String[] { CLOUNM_ID,
			CLOUNM_ID, CLOUNM_SID, CLOUNM_SPID, CLOUNM_SNAME, CLOUNM_SICON,CLOUNM_CID };

	private static final String TABLE_NAME = "cache_service_table";

	private Context mContext;
	private EsDatabaseHelper.DatabaseWrapper mDb;

	public ServiceCacheHelper(Context context) {
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

	public void addNewsService(MenuItem service) {
		ContentValues values = new ContentValues();
		values.put(CLOUNM_SID, service.getMenuId() + "");
		values.put(CLOUNM_SNAME, service.getMenuName());
		values.put(CLOUNM_SICON, service.getIcon());
		values.put(CLOUNM_CID, service.getAncestorId());
		mDb.insert(TABLE_NAME, null, values);
	}

	public void clearCache() {
		mDb.delete(TABLE_NAME, null, null);
	}

	protected MenuItem cursorToMenuItem(Cursor cursor) {
		MenuItem service = new MenuItem();
		service.setMenuId(cursor.getString(cursor.getColumnIndex(CLOUNM_SID)));
		service.setMenuName(cursor.getString(cursor.getColumnIndex(CLOUNM_SNAME)));
		service.setIcon(cursor.getString(cursor.getColumnIndex(CLOUNM_SICON)));
		service.setAncestorId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(CLOUNM_CID))));
		return service;
	}

	public MenuItem queryHistoryById(String id) {
		Cursor cursor = null;
		try {
			cursor = mDb.query(TABLE_NAME, PROJECTION, CLOUNM_SID + "=?",
					new String[] { id }, null, null, null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					return cursorToMenuItem(cursor);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return null;
	}

	// 获取某个时间前的日志信息
	public ArrayList<MenuItem> queryCache() {
		ArrayList<MenuItem> events = new ArrayList<MenuItem>();
		Cursor cursor = null;
		try {
			cursor = mDb.query(TABLE_NAME, PROJECTION, null, null, null, null,
					null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					do {
						events.add(cursorToMenuItem(cursor));
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

	public ArrayList<MenuItem> queryService(String name) {
		ArrayList<MenuItem> events = new ArrayList<MenuItem>();
		Cursor cursor = null;
		try {
			cursor = mDb.query(TABLE_NAME, PROJECTION, CLOUNM_SNAME
					+ " like ?", new String[] { "%"+ name+ "%" }, null, null, null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					do {
						events.add(cursorToMenuItem(cursor));
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
