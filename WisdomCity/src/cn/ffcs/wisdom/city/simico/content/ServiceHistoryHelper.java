package cn.ffcs.wisdom.city.simico.content;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import cn.ffcs.wisdom.city.sqlite.model.MenuItem;

public class ServiceHistoryHelper {

	private static final String TABLE_NAME = "service_history_table_2";

	private Context mContext;
	private EsDatabaseHelper.DatabaseWrapper mDb;

	public ServiceHistoryHelper(Context context) {
		mContext = context;
		mDb = null;
	}

	public Context getContext() {
		return mContext;
	}

	public void initialize() {
		if (mDb == null) {
			mDb = EsDatabaseHelper.getDatabaseHelper(mContext).getWritableDatabaseWrapper();
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

	// "icon":
	// "upload/3507/menu/img/400/icon_0_1365406508057_32046204189533488295669823.png",
	// "menuName": "母婴用品",
	// "menuId": 785539,
	// "menuPid": 427659,
	// "cityCode": "3507",
	// "menuType": "wap",
	// "package_": null,
	// "main": null,
	// "url": "http://218.5.102.205:8080/zhqz/ysyymyyp.do",
	// "appUrl": null,
	// "menudesc": null,
	// "mustLogin": "false",
	// "baseLine": "400",
	// "isApp": "0",
	// "v6Icon":
	// "upload/3507/menu/img/400/icon_4_1366334413822_22682276-7941570250990718315.png",
	// "recommend": "0",
	// "menuVer": 3643,
	// "appsize": "K",
	// "menuOrder": 3,
	// "recommendOrder": null,
	// "isNew": null,
	// "isRed": 0,
	// "commonOrder": null,
	// "parMapString": ""
	// frontMenuOrder:12
	// commonOrder:null
	// 插入一条新的使用记录
	public void addNewHistory(MenuItem menu, String mobile, String cityCode) {
		ContentValues values = new ContentValues();
		values.put("icon", menu.getIcon());
		values.put("menuName", menu.getMenuName());
		values.put("menuId", menu.getMenuId());
		values.put("menuPid", menu.getMenuPid());
		values.put("cityCode", menu.getCityCode());
		values.put("menuType", menu.getMenuType());
		values.put("package_", menu.getPackage_());
		values.put("main", menu.getMain());
		values.put("url", menu.getUrl());
		values.put("appUrl", menu.getAppUrl());
		values.put("menudesc", menu.getMenudesc());
		values.put("mustLogin", menu.isMustLogin() + "");
		values.put("baseLine", menu.getBaseLine());
		values.put("isApp", menu.getIsApp());
		values.put("v6Icon", menu.getV6Icon());
		values.put("recommend", menu.getRecommend());
		values.put("menuVer", menu.getMenuVer());
		values.put("appsize", menu.getAppsize());
		values.put("menuOrder", menu.getMenuOrder());
		values.put("recommendOrder", menu.getRecommendOrder());
		values.put("isNew", menu.getIsNew());
		values.put("isRed", menu.getIsRed());
		values.put("commonOrder", menu.getCommonOrder());
		values.put("parMapString", menu.getMap());
		values.put("frontMenuOrder", menu.getFrontMenuOrder());
		values.put("timestamp", System.currentTimeMillis() + "");
		values.put("mobile", mobile == null ? "" : mobile);
		values.put("city", cityCode == null ? "" : cityCode);
		values.put("shareContent", menu.getShareContent());
		values.put("shareType", menu.getShareType());
		mDb.insert(TABLE_NAME, null, values);
	}

	// 更新最近使用时间
	public void updateHistory(MenuItem service, String mobile, String cityCode) {
		if (mobile == null)
			mobile = "";
		if (cityCode == null)
			cityCode = "";
		ContentValues values = new ContentValues();
		values.put("timestamp", System.currentTimeMillis() + "");
		mDb.update(TABLE_NAME, values, "menuId=? and mobile=? and city=?",
				new String[] { service.getMenuId(), mobile, cityCode });
	}

	// 删除历史信息
	public int deleteHistoryById(int id) {
		return mDb.delete(TABLE_NAME, "_id=?", new String[] { String.valueOf(id) });
	}

	// "icon":
	// "upload/3507/menu/img/400/icon_0_1365406508057_32046204189533488295669823.png",
	// "menuName": "母婴用品",
	// "menuId": 785539,
	// "menuPid": 427659,
	// "cityCode": "3507",
	// "menuType": "wap",
	// "package_": null,
	// "main": null,
	// "url": "http://218.5.102.205:8080/zhqz/ysyymyyp.do",
	// "appUrl": null,
	// "menudesc": null,
	// "mustLogin": "false",
	// "baseLine": "400",
	// "isApp": "0",
	// "v6Icon":
	// "upload/3507/menu/img/400/icon_4_1366334413822_22682276-7941570250990718315.png",
	// "recommend": "0",
	// "menuVer": 3643,
	// "appsize": "K",
	// "menuOrder": 3,
	// "recommendOrder": null,
	// "isNew": null,
	// "isRed": 0,
	// "commonOrder": null,
	// "parMapString": ""
	// frontMenuOrder:12
	// commonOrder:null
	protected MenuItem cursorToMenuItem(Cursor cursor) {
		MenuItem menu = new MenuItem();
		menu.setIcon(cursor.getString(cursor.getColumnIndex("icon")));
		menu.setMenuName(cursor.getString(cursor.getColumnIndex("menuName")));
		menu.setMenuId(cursor.getString(cursor.getColumnIndex("menuId")));
		menu.setMenuPid(cursor.getString(cursor.getColumnIndex("menuPid")));
		menu.setCityCode(cursor.getString(cursor.getColumnIndex("cityCode")));
		menu.setMenuType(cursor.getString(cursor.getColumnIndex("menuType")));
		menu.setPackage_(cursor.getString(cursor.getColumnIndex("package_")));
		menu.setMain(cursor.getString(cursor.getColumnIndex("main")));
		menu.setUrl(cursor.getString(cursor.getColumnIndex("url")));
		menu.setMenudesc(cursor.getString(cursor.getColumnIndex("menudesc")));
		menu.setMustLogin(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex("mustLogin"))));
		menu.setBaseLine(cursor.getString(cursor.getColumnIndex("baseLine")));
		menu.setIsApp(cursor.getString(cursor.getColumnIndex("isApp")));
		menu.setV6Icon(cursor.getString(cursor.getColumnIndex("v6Icon")));
		menu.setRecommend(cursor.getString(cursor.getColumnIndex("recommend")));
		menu.setMenuVer(cursor.getString(cursor.getColumnIndex("menuVer")));
		menu.setAppsize(cursor.getString(cursor.getColumnIndex("appsize")));
		menu.setMenuOrder(Integer.parseInt(cursor.getString(cursor.getColumnIndex("menuOrder"))));
		menu.setRecommendOrder(Integer.parseInt(cursor.getString(cursor
				.getColumnIndex("recommendOrder"))));
		menu.setIsNew(cursor.getString(cursor.getColumnIndex("isNew")));
		menu.setIsRed(cursor.getString(cursor.getColumnIndex("isRed")));
		menu.setCommonOrder(Integer.parseInt(cursor.getString(cursor.getColumnIndex("commonOrder"))));
		menu.setMap(cursor.getString(cursor.getColumnIndex("parMapString")));
		menu.setFrontMenuOrder(Integer.parseInt(cursor.getString(cursor
				.getColumnIndex("frontMenuOrder"))));
		menu.setShareContent(cursor.getString(cursor.getColumnIndex("shareContent")));
		menu.setShareType(cursor.getString(cursor.getColumnIndex("shareType")));
		menu.setAppUrl(cursor.getString(cursor.getColumnIndex("appUrl")));
		return menu;
	}

	public MenuItem queryHistoryById(String id, String mobile, String cityCode) {
		if (mobile == null)
			mobile = "";
		if (cityCode == null)
			cityCode = "";
		Cursor cursor = null;
		try {
			cursor = mDb.query(TABLE_NAME, null, "menuId=? and mobile=? and city=?", new String[] {
					id, mobile, cityCode }, null, null, null);
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
	public ArrayList<MenuItem> queryHistories(String mobile, String cityCode) {
		if (mobile == null)
			mobile = "";
		if (cityCode == null)
			cityCode = "";
		ArrayList<MenuItem> events = new ArrayList<MenuItem>();
		Cursor cursor = null;
		try {
			cursor = mDb.query(TABLE_NAME, null, "mobile=? and city=?", new String[] { mobile,
					cityCode }, null, null, "timestamp desc", "8");
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
