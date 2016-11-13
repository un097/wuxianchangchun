//package cn.ffcs.wisdom.city.sqlite.contentprovider;
//
//import cn.ffcs.wisdom.city.v6.R;
//import android.content.ContentProvider;
//import android.content.ContentUris;
//import android.content.ContentValues;
//import android.content.UriMatcher;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.net.Uri;
//
///**
// * <p>Title:   城市列表contentProvider 仅实现了查询        </p>
// * <p>Description:                     </p>
// * <p>@author: zhangwsh                </p>
// * <p>Copyright: Copyright (c) 2014    </p>
// * <p>Company: ffcs Co., Ltd.          </p>
// * <p>Create Time: 2014-5-27           </p>
// * <p>@author:                         </p>
// * <p>Update Time:                     </p>
// * <p>Updater:                         </p>
// * <p>Update Comments:                 </p>
// */
//public class CityContentProvider extends ContentProvider {
//
//	private SQLiteDatabase database;
//
//	private static UriMatcher uriMatcher;
//	private final String tableName = "t_city_list_info";
//	private static final int CITIES = 1;
//	private String dbPath;
//
////	private static final int CITY_CODE = 2;
////	private static final int CITY_NAME = 3;
////	private static final int CITIES_IN_PROVINCE = 4;
//
//	@Override
//	public boolean onCreate() {
//		try {
//			uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
//			// content://cn.ffcs.city.contentprovider/cities
//			uriMatcher.addURI(
//					getContext().getResources().getString(R.string.content_provider_authorities),
//					"cities", CITIES);
////			//  content://cn.ffcs.city.contentprovider/code/3501
////			uriMatcher.addURI(AUTHORITY, "code/#", CITY_CODE);
////			//  content://cn.ffcs.city.contentprovider/name/北京
////			uriMatcher.addURI(AUTHORITY, "name/*", CITY_NAME);
////			//  content://cn.ffcs.city.contentprovider/cities_in_province/辽宁
////			uriMatcher.addURI(AUTHORITY, "cities_in_province/*", CITIES_IN_PROVINCE);
//
//			dbPath = getContext().getDatabasePath(getContext().getString(R.string.db_name)).getPath();
//			database = SQLiteDatabase.openDatabase(dbPath, null, 0);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return true;
//	}
//
//	@Override
//	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
//			String sortOrder) {
//		Cursor cursor = null;
//		try {
//			if (database == null) {
//				database = SQLiteDatabase.openDatabase(dbPath, null, 0);
//			}
//
//			switch (uriMatcher.match(uri)) {
//			case CITIES:
//				cursor = database.query(tableName, projection, selection, selectionArgs, null,
//						null, null);
//				break;
//			default:
//				throw new IllegalArgumentException("<" + uri + ">格式不正确.");
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return cursor;
//	}
//
//	@Override
//	public String getType(Uri uri) {
//		String type = null;
//		switch (uriMatcher.match(uri)) {
//		case CITIES:
//			type = "citys";
//			break;
//		default:
//			throw new IllegalArgumentException("<" + uri + ">格式不正确.");
//		}
//		return type;
//	}
//
//	@Override
//	public Uri insert(Uri uri, ContentValues values) {
//		if (database == null) {
//			database = SQLiteDatabase.openDatabase(dbPath, null, 0);
//		}
//
//		switch (uriMatcher.match(uri)) {
//		case CITIES:
//			long rowId = database.insert(tableName, null, values);
//			if (rowId > -1) {
//				uri = ContentUris.withAppendedId(uri, rowId);
//				getContext().getContentResolver().notifyChange(uri, null);
//				return uri;
//			}
//			break;
//		default:
//			throw new IllegalArgumentException("<" + uri + ">格式不正确.");
//		}
//		throw new IllegalArgumentException("insert error");
//	}
//
//	@Override
//	public int delete(Uri uri, String selection, String[] selectionArgs) {
//		if (database == null) {
//			database = SQLiteDatabase.openDatabase(dbPath, null, 0);
//		}
//		int count = 0;
//		switch (uriMatcher.match(uri)) {
//		case CITIES:
//			count = database.delete(tableName, selection, selectionArgs);
//			break;
//		default:
//			throw new IllegalArgumentException("<" + uri + ">格式不正确.");
//		}
//		getContext().getContentResolver().notifyChange(uri, null);
//		return count;
//	}
//
//	@Override
//	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
//		if (database == null) {
//			database = SQLiteDatabase.openDatabase(dbPath, null, 0);
//		}
//		int count = 0;
//		switch (uriMatcher.match(uri)) {
//		case CITIES:
//			count = database.update(tableName, values, selection, selectionArgs);
//			break;
//		default:
//			throw new IllegalArgumentException("<" + uri + ">格式不正确.");
//		}
//		getContext().getContentResolver().notifyChange(uri, null);
//		return count;
//	}
//}
