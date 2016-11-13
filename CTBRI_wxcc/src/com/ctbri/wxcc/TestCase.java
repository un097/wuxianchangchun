package com.ctbri.wxcc;

import java.util.HashMap;
import java.util.List;

import com.ctbri.wwcc.greenrobot.HotLine;
import com.ctbri.wxcc.db.DBHelper;

import android.test.AndroidTestCase;
import android.util.Log;

public class TestCase extends AndroidTestCase {

	public void testAdd(){
		DBHelper instance = DBHelper.getInstance(getContext());
		for (int i = 0; i < 20; i++) {
			int group_id;
			String group_name = "";
			if(i >= 0 && i< 5){
				group_id = 1;
				group_name = "我的收藏";
			} else if (i >= 5 && i<= 12){
				group_id = 2;
				group_name = "公益电话";
			} else {
				group_id = 3;
				group_name = "民生电话";
			}
			HotLine hotLine = null;// new HotLine(i, 1, "电话号码", "sasdadasd", "123", group_id, group_name);
			instance.addTel(hotLine);
		}
	}
	
	public void testClear(){
		DBHelper instance = DBHelper.getInstance(getContext());
		instance.clearAll();
	}
	
	
	public void testReload(){
		DBHelper instance = DBHelper.getInstance(getContext());
		HashMap<String, Object> reload = instance.reload();
	}
}
