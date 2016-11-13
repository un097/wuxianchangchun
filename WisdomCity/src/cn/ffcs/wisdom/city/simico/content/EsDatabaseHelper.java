package cn.ffcs.wisdom.city.simico.content;

import java.io.File;
import java.util.Locale;
import java.util.Stack;
import java.util.regex.Pattern;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.AsyncTask;
import cn.ffcs.wisdom.city.simico.kit.log.TLog;


public class EsDatabaseHelper extends SQLiteOpenHelper {
	private static EsDatabaseHelper sHelper = null;
	private static long sLastDatabaseDeletionTimestamp;
	private static String sExplainQueryPlanRegexp;
	private static long sMaxYieldTime = 2000L;
	// private final Context mContext;
	private boolean mDeleted;
	private static final String[] MASTER_COLUMNS = new String[] { "name" };

	private EsDatabaseHelper(Context context, int accountIndex) {
		super(context, "event_" + accountIndex + ".db", null, 5);
		// mContext = context;
	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		rebuildTables(new DatabaseWrapper(db));
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		if (!db.isReadOnly())
			db.execSQL("PRAGMA foreign_keys=ON;");
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// 行为日志表
		String table = "CREATE TABLE event_log_table (_id INTEGER PRIMARY KEY,user TEXT, area_id TEXT, item_type TEXT," +
				"item_id TEXT,sub_item_id TEXT,desc TEXT,timestamp TEXT,field1 TEXT,field2 TEXT,status INTEGER,city_code TEXT)";
		db.execSQL(table);
		// 最近使用服务表
		String serviceHistory = "CREATE TABLE service_history_table_2(" +
				"_id INTEGER PRIMARY KEY,icon,menuName TEXT,menuId TEXT,timestamp TEXT,menuPid TEXT," +
				"cityCode TEXT,menuType TEXT ,package_ TEXT,main TEXT,url TEXT,appUrl TEXT,menudesc TEXT,mustLogin TEXT," +
				"baseLine TEXT,isApp TEXT,v6Icon TEXT,recommend TEXT,menuVer TEXT,appsize TEXT," +
				"menuOrder TEXT,recommendOrder TEXT,isNew TEXT,isRed TEXT,parMapString TEXT,frontMenuOrder TEXT," +
				"commonOrder TEXT,mobile TEXT,city TEXT,shareContent TEXT,shareType TEXT);";
		db.execSQL(serviceHistory);
		// 更多服务列表
		String cacheServices = "CREATE TABLE cache_service_table(_id INTEGER PRIMARY KEY,service_id TEXT,service_pid TEXT,service_icon TEXT,service_name TEXT,ctg_id TEXT);";
		db.execSQL(cacheServices);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		rebuildTables(new DatabaseWrapper(db));
	}

	public static synchronized EsDatabaseHelper getDatabaseHelper(
			Context context) {
		// synchronized (sHelper) {
		if (sHelper == null) {
			sHelper = new EsDatabaseHelper(context, 0);
		}
		// }
		return sHelper;
	}

	public static long getMaxYieldTime() {
		return sMaxYieldTime;
	}

	public static boolean isDatabaseRecentlyDeleted() {
		boolean flag = false;
		if (sLastDatabaseDeletionTimestamp != 0L
				&& System.currentTimeMillis() - sLastDatabaseDeletionTimestamp < 60000L)
			flag = true;
		return flag;
	}

	public void rebuildTables(DatabaseWrapper dbwrapper) {
		dropAllTables(dbwrapper.getDatabase());
		onCreate(dbwrapper.getDatabase());
	}

	private void dropAllTables(SQLiteDatabase sqlitedatabase) {
		Cursor cursor = sqlitedatabase.query("sqlite_master", MASTER_COLUMNS,
				"type='table'", null, null, null, null);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				String s = cursor.getString(0);
				if (s != null && !s.startsWith("android_")
						&& !s.startsWith("sqlite_")) {
					sqlitedatabase.execSQL("DROP TABLE IF EXISTS " + s);
				}
			}
			cursor.close();
		}
	}

	public static void refreshGservices(Context context) {
	}

	public void createNewDatabase() {
		mDeleted = false;
	}

	private void doDeleteDatabase() {
		if (!mDeleted) {
			DatabaseWrapper dbWrapper = getWritableDatabaseWrapper();
			dbWrapper.beginTransaction();
			mDeleted = true;
			sLastDatabaseDeletionTimestamp = System.currentTimeMillis();
			dbWrapper.endTransaction();
			dbWrapper.getDatabase().close();
			(new File(dbWrapper.getDatabase().getPath())).delete();
		}
	}

	public void deleteDatabase() {
		new AsyncTask<Void, Void, Void>() {

			protected Void doInBackground(Void... avoid1) {
				doDeleteDatabase();
				return null;
			}

		}.execute();
	}

	public SQLiteDatabase getReadableDatabase() {
		if (mDeleted)
			throw new SQLiteException("Database deleted");
		SQLiteDatabase db = super.getReadableDatabase();
		return db;
	}

	public DatabaseWrapper getReadableDatabaseWrapper() {
		if (mDeleted)
			throw new SQLiteException("Database deleted");
		DatabaseWrapper dbWrapper = DatabaseWrapper.getWrapper(super
				.getReadableDatabase());
		return dbWrapper;
	}

	public SQLiteDatabase getWritableDatabase() {
		if (mDeleted)
			throw new SQLiteException("Database deleted");
		return super.getWritableDatabase();
	}

	public DatabaseWrapper getWritableDatabaseWrapper() {
		if (mDeleted)
			throw new SQLiteException("Database deleted");
		DatabaseWrapper dbWrapper = DatabaseWrapper.getWrapper(super
				.getWritableDatabase());
		return dbWrapper;
	}

	public static class DatabaseWrapper {
		private static boolean mLog = true;
		private static String sFormatStrings[] = new String[] {
				"took %d ms to %s", "   took %d ms to %s",
				"      took %d ms to %s" };
		private static DatabaseWrapper sInstance;
		private static ThreadLocal<Stack<Long>> sTransactionDepth = new ThreadLocal<Stack<Long>>() {

			public Stack<Long> initialValue() {
				return new Stack<Long>();
			}

		};
		private final SQLiteDatabase mDatabase;

		private DatabaseWrapper(SQLiteDatabase db) {
			// mLog = LogUtil.isLoggable("Babel_dbperf", 2);
			mDatabase = db;
		}

		@SuppressWarnings("deprecation")
		private static void explainQueryPlan(SQLiteQueryBuilder sql,
				SQLiteDatabase db, String[] projectionIn, String selection,
				String[] selectionArgs, String groupBy, String s2,
				String sortOrder, String limit) {
			String queryPlan = sql.buildQuery(projectionIn, selection,
					selectionArgs, groupBy, null, sortOrder, limit);
			if (Pattern.matches(
					EsDatabaseHelper.sExplainQueryPlanRegexp,
					queryPlan)) {
				Cursor cursor = db.rawQuery("explain query plan " + queryPlan,
						null);
				if (cursor != null && cursor.moveToFirst()) {
					int i = cursor.getColumnIndex("detail");
					StringBuilder sb = new StringBuilder();
					do {
						sb.append(cursor.getString(i));
						sb.append("\n");
					} while (cursor.moveToNext());
					if (sb.length() > 0)
						sb.setLength(-1 + sb.length());
					TLog.log("Babel", "for query " + queryPlan + " plan is: "
							+ sb.toString());
				}
				cursor.close();
			}
		}

		public static DatabaseWrapper getWrapper(SQLiteDatabase db) {
			if (sInstance == null || sInstance.mDatabase != db)
				sInstance = new DatabaseWrapper(db);
			return sInstance;
		}

		private static void printTiming(long time, String log) {
			int i = (sTransactionDepth.get()).size();
			String logFormat = sFormatStrings[Math.min(-1
					+ sFormatStrings.length, i)];
			TLog.log(
					"Babel",
					String.format(
							Locale.US,
							logFormat,
							new Object[] {
									Long.valueOf(System.currentTimeMillis()
											- time), log }));
		}

		public static Cursor query(SQLiteQueryBuilder sql,
				DatabaseWrapper dbWrapper, String[] projectionIn,
				String selection, String[] selectionArgs, String groupBy,
				String having, String sortOrder, String limit) {
			if (EsDatabaseHelper.sExplainQueryPlanRegexp != null)
				explainQueryPlan(sql, dbWrapper.getDatabase(), projectionIn,
						selection, selectionArgs, groupBy, having, sortOrder,
						limit);
			long time = 0L;
			if (mLog)
				time = System.currentTimeMillis();
			Cursor cursor = sql
					.query(dbWrapper.mDatabase, projectionIn, selection,
							selectionArgs, groupBy, having, sortOrder, limit);
			if (mLog) {
				printTiming(time, String.format(
						Locale.US,
						"query %s with %s ==> %d",
						new Object[] { sql.getTables(), selection,
								Integer.valueOf(cursor.getCount()) }));
			}
			return cursor;
		}

		public void beginTransaction() {
			if (mLog) {
				long time = System.currentTimeMillis();
				printTiming(time, ">>> beginTransaction");
				(sTransactionDepth.get()).push(Long.valueOf(time));
			}
			mDatabase.beginTransaction();
		}

		public int delete(String table, String whereClause, String whereArgs[]) {
			long time = 0L;
			if (mLog)
				time = System.currentTimeMillis();
			int rowId = mDatabase.delete(table, whereClause, whereArgs);
			if (mLog) {
				printTiming(time, String.format(Locale.US,
						"delete from %s with %s ==> %d", new Object[] { table,
								whereClause, Integer.valueOf(rowId) }));
			}
			return rowId;
		}

		public void endTransaction() {
			long endTime = 0L;
			long startTime = 0L;
			if (mLog) {
				startTime = ((sTransactionDepth.get()).pop()).longValue();
				endTime = System.currentTimeMillis();
			}
			mDatabase.endTransaction();
			if (mLog) {
				printTiming(endTime, String.format(
						Locale.US,
						">>> endTransaction (total for this transaction: %d)",
						new Object[] { Long.valueOf(System.currentTimeMillis()
								- startTime) }));
			}
		}

		public void execSQL(String sql) {
			long time = 0L;
			if (mLog)
				time = System.currentTimeMillis();
			mDatabase.execSQL(sql);
			if (mLog) {
				printTiming(time, String.format(Locale.US, "execSQL %s",
						new Object[] { sql }));
			}
		}

		public SQLiteDatabase getDatabase() {
			return mDatabase;
		}

		public long insert(String table, String nullColumnHack,
				ContentValues values) {
			long time = 0L;
			if (mLog)
				time = System.currentTimeMillis();
			long rowId = mDatabase.insert(table, nullColumnHack, values);
			if (mLog) {
				printTiming(time, String.format(Locale.US, "insert to %s",
						new Object[] { table }));
			}
			return rowId;
		}

		public void insertWithOnConflict(String table, String nullColumnHack,
				ContentValues values, int conflictAlgorithm) {
			long time = 0L;
			if (mLog)
				time = System.currentTimeMillis();
			mDatabase.insertWithOnConflict(table, nullColumnHack, values,
					conflictAlgorithm);
			if (mLog) {
				printTiming(time, String.format(Locale.US,
						"insertWithOnConflict with ", new Object[] { table }));
			}
		}

		public Cursor query(String table, String[] columns, String selection,
				String[] selectionArgs, String groupBy, String having,
				String orderBy) {
			return query(table, columns, selection, selectionArgs, groupBy,
					having, orderBy, null);
		}

		public Cursor query(String table, String[] columns, String selection,
				String[] selectionArgs, String groupBy, String having,
				String orderBy, String limit) {
			if (EsDatabaseHelper.sExplainQueryPlanRegexp != null) {
				SQLiteQueryBuilder sql = new SQLiteQueryBuilder();
				sql.setTables(table);
				explainQueryPlan(sql, mDatabase, columns, selection,
						selectionArgs, groupBy, having, orderBy, limit);
			}
			long time = 0L;
			if (mLog)
				time = System.currentTimeMillis();
			Cursor cursor = mDatabase.query(table, columns, selection,
					selectionArgs, groupBy, having, orderBy, limit);
			if (mLog) {
				printTiming(time,
						String.format(
								Locale.US,
								"query %s with %s ==> %d",
								new Object[] { table, selection,
										Integer.valueOf(cursor.getCount()) }));
			}
			return cursor;
		}

		public Cursor rawQuery(String sql, String[] selectionArgs) {
			long time = 0L;
			if (mLog)
				time = System.currentTimeMillis();
			Cursor cursor = mDatabase.rawQuery(sql, selectionArgs);
			if (mLog) {
				printTiming(time,
						String.format(
								Locale.US,
								"rawQuery %s ==> %d",
								new Object[] { sql,
										Integer.valueOf(cursor.getCount()) }));
			}
			return cursor;
		}

		public void setLocale(Locale locale) {
			mDatabase.setLocale(locale);
		}

		public void setTransactionSuccessful() {
			mDatabase.setTransactionSuccessful();
		}

		public int update(String table, ContentValues contentvalues,
				String whereClause, String whereArgs[]) {
			long time = 0L;
			if (mLog)
				time = System.currentTimeMillis();
			int rowId = mDatabase.update(table, contentvalues, whereClause,
					whereArgs);
			if (mLog) {
				printTiming(time, String.format(Locale.US,
						"update %s with %s ==> %d", new Object[] { table,
								whereClause, Integer.valueOf(rowId) }));
			}
			return rowId;
		}

		public void yieldTransaction() {
			long time = 0L;
			if (mLog)
				time = System.currentTimeMillis();
			if (mDatabase.yieldIfContendedSafely() && mLog)
				printTiming(time, "yieldTransaction");
		}
	}
}
