package cn.ffcs.wisdom.city.setting.share;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Message;
import android.provider.ContactsContract;
import cn.ffcs.wisdom.tools.StringUtil;

/**
 * <p>Title:     获取联系人异步handler         </p>
 * <p>Description:                     </p>
 * <p>@author: zhangwsh                </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-9-11           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class ContactAsyncQueryHandler extends AsyncQueryHandler {

	private static List<Map<String, Object>> list;// 获取联系人单例List
	private static boolean isLoading = false;// 是否在加载联系人中
//	private static boolean isExistContacts = true;// 是否有联系人
	private static ContactAsyncQueryHandler queryHandler;
	private OnQueryContactsListener listener;
	private final int GET_FINISH = 0x11;

	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		switch (msg.what) {
		case GET_FINISH:
			if (listener != null) {
				listener.onQueryFinish(list);
			}
			break;
		}
	}

	private ContactAsyncQueryHandler(ContentResolver cr) {
		super(cr);
	}

	public static ContactAsyncQueryHandler getInstance(ContentResolver cr) {
		if (queryHandler == null) {
			queryHandler = new ContactAsyncQueryHandler(cr);
		}
		return queryHandler;
	}

	@Override
	protected void onQueryComplete(int token, Object cookie, final Cursor cursor) {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				if (cursor != null && cursor.getCount() > 0) {
					list = new ArrayList<Map<String, Object>>();
					cursor.moveToFirst();
					for (int i = 0; i < cursor.getCount(); i++) {
						Map<String, Object> map = new HashMap<String, Object>();
						cursor.moveToPosition(i);
						String name = cursor.getString(1);
						String number = cursor.getString(2);
						String sortKey = cursor.getString(3);

						if (!StringUtil.isEmpty(number)) {
							if (number.startsWith("+86")) {
								number = number.substring(3);
							}
							name = name.replaceAll("-", "").trim();
							number = number.replaceAll("-", "").trim();
							map.put("phone", number);
							map.put("checked", Boolean.FALSE);
							map.put("is_sure_checked", Boolean.FALSE);
							map.put("name", name);
							String pyPhone = name + "-" + number;
							map.put("toFirstSpell", sortKey);
							pyPhone = StringUtil.toPinYin(pyPhone, true);
							map.put("pyPhone", pyPhone);
							list.add(map);
						}
					}
				} else {
					list = Collections.emptyList();
				}
				isLoading = false;
				sendMessage(obtainMessage(GET_FINISH));
			}
		});
		t.start();
	}

	/**
	 * 获取是否在加载联系人中
	 * @return
	 */
	public boolean getIsLoading() {
		return isLoading;
	}

	/**
	 * 
	 * 获取到联系人的信息
	 */
	public void getList(OnQueryContactsListener listener) {
		this.listener = listener;
		if (list == null) {
			startGetContacts();
		} else {
			if (listener != null) {
				listener.onQueryFinish(list);
			}
		}
	}

	/**
	 * 开始获取联系人呢
	 */
	public void startGetContacts() {
		if (listener != null) {
			listener.onQueryStart();
		}
		if (!isLoading) {
			isLoading = true;
			//"content://com.android.contacts/data/phones"
			Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
			String[] projection = { ContactsContract.CommonDataKinds.Phone._ID,
					ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
					ContactsContract.CommonDataKinds.Phone.NUMBER, "sort_key" };
			queryHandler.startQuery(0, null, uri, projection, null, null,
					"sort_key COLLATE LOCALIZED asc");
		}
	}

	/**
	 * 
	 * 初始化联系人
	 */
	public void initContacts() {
		if (list == null) {
			return;
		}
		int size = list.size();
		for (int i = 0; i < size; i++) {
			Map<String, Object> map = list.get(i);
			map.put("checked", Boolean.FALSE);
			map.put("is_sure_checked", Boolean.FALSE);
		}
		listener = null;
	}

	/**
	 * 确认选中联系人
	 */
	public void sureContacts() {
		if (list == null) {
			return;
		}
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map = list.get(i);
			if ((Boolean) map.get("checked")) {
				map.put("is_sure_checked", Boolean.TRUE);
			}
		}
	}

	public void cancelContacts() {
		if (list == null) {
			return;
		}
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map = list.get(i);
			if ((Boolean) map.get("checked") && !(Boolean) map.get("is_sure_checked")) {
				map.put("checked", Boolean.FALSE);
			}
		}
	}
}