package com.ctbri.wxcc.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;

import com.ctbri.wwcc.greenrobot.DaoMaster;
import com.ctbri.wwcc.greenrobot.DaoSession;
import com.ctbri.wwcc.greenrobot.HotLine;
import com.ctbri.wwcc.greenrobot.HotLineDao;
import com.ctbri.wwcc.greenrobot.HotLineDao.Properties;

import de.greenrobot.dao.query.Query;

public class DBHelper {

	
	private Context context;
	private HotLineDao hotLineDao;
	
	
	public DBHelper(Context context) {
		this.context = context;
		DaoMaster daoMaster = DBManager.getDaoMaster(context);
		DaoSession daoSession = DBManager.getDaoSession(daoMaster, context);
		hotLineDao = daoSession.getHotLineDao();
	}
	private static DBHelper instance = null;
	
	public synchronized static DBHelper getInstance(Context context){
		if(instance == null) {
			instance = new DBHelper(context);
		}
		return instance;
	}
	
	
	public void addTel(HotLine entity){
		hotLineDao.insert(entity);
	}


	public void clearAll() {
		// TODO Auto-generated method stub
		hotLineDao.deleteAll();
	}
	
	public HashMap<String, Object> reload(){
		ArrayList<HotLine> groupList = new ArrayList<HotLine>();
		ArrayList<List<HotLine>> childList = new ArrayList<List<HotLine>>();
		Query<HotLine> qb = hotLineDao.queryRawCreate(" GROUP BY GROUP_ID ORDER BY _ID");
		for (HotLine item : qb.list()) {
			groupList.add(new HotLine("0", null, null, null, null, item.getGroup_id(), item.getGroup_name()));
			List<HotLine> list = hotLineDao.queryBuilder().where(Properties.Group_id.eq(item.getGroup_id())).orderAsc(Properties.Id).list();
			childList.add(list);
		}
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("group", groupList);
		data.put("child", childList);
		return data;
	}


	public List<HotLine> queryTel(String string) {
		List<HotLine> list = null;
		if(TextUtils.isEmpty(string)) {
			list = new ArrayList<HotLine>();
		} else {
			list = hotLineDao.queryBuilder().where(Properties.Num_name.like("%" + string + "%"), Properties.Group_name.notEq("常用电话")).list();
		}
		return list;
	}
}
