package cn.ffcs.wisdom.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;

/**
 * <p>Title: 联系人工具类         </p>
 * <p>Description: 
 * 通过访问android内置联系人数据库表来访问联系人数据，返回数据格式：List and Array
 * </p>
 * <p>@author: Eric.wsd                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2012-6-1            </p>
 * <p>@author:    zhangws              </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class ContactsUtil {
	/**
	 * 获取联系人单例List
	 */
	private static List<String> mContactsList;
	
	/**
	 * 是否在加载联系人中
	 */
	private static boolean isLoading = false;

	/**
	 * 获取联系人数组
	 * @param context
	 * @return
	 */
	public static String[] getContacts(Context context) {
		if (context != null) {
			List<String> list = getContactsList(context);

			return (String[]) list.toArray(new String[list.size()]);
		}
		return new String[] {};
	}

	/**
	 * 获取多选联系人列表
	 * @param context
	 * @return
	 */
	public static List<Map<String, Object>> getContactsMapList(Context context) {
		List<Map<String, Object>> phoneList = null;
		if (phoneList == null) {
			if (context != null) {
				phoneList = new ArrayList<Map<String, Object>>();
				isLoading = true;
				List<String> listString = getContactsList(context);
				for (int i = 0; i < listString.size(); i++) {
					Map<String, Object> map = new HashMap<String, Object>();
					String pyPhone = StringUtil.toPinYin(listString.get(i), true);
					map.put("phone", listString.get(i));
					map.put("checked", Boolean.FALSE);
					map.put("pyPhone", pyPhone);
					map.put("pyKeyPhone", pyPhone.replaceAll(",", ""));
					map.put("toFirstSpell", StringUtil.toFirstSpell(pyPhone).charAt(0));
					phoneList.add(map);
				}
				isLoading = false;
			}
		}
		return phoneList;
	}

	/**
	 * 获取是否在加载联系人中
	 * @return
	 */
	public static boolean getIsLoading() {
		return isLoading;
	}

	/**
	 * 获取到的联系人列表
	 * @param context
	 * @return
	 */
	public static List<String> getContactsList(Context context) {
		if (mContactsList == null) {
			mContactsList = new ArrayList<String>();
			mContactsList.addAll(getContactsListBySys(context));
		}
		return mContactsList;
	}

	/**
	 * 从系统数据库获取到联系人列表
	 * @param context
	 * @return
	 */
	private static List<String> getContactsListBySys(Context context) {
		List<String> contactsList = new ArrayList<String>();
		Cursor cursor = null;
		try {
			cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
					null, null, null, null);

			if (cursor != null && cursor.moveToFirst()) {
				int idIndex = cursor.getColumnIndex(Contacts._ID); // ID索引
				int displayNameIndex = cursor.getColumnIndex(Contacts.DISPLAY_NAME); // 名称索引
				int phoneNumberIndex = cursor.getColumnIndex(Contacts.HAS_PHONE_NUMBER); // 手机号码索引

				do {
					// 获取联系人ID
					String id = cursor.getString(idIndex);
					// 获取联系人名称
					String name = cursor.getString(displayNameIndex);
					// 查看该联系人有多个号码，如果没有返回0
					int phoneCount = cursor.getInt(phoneNumberIndex);

					if (phoneCount > 0) {
						Cursor phones = context.getContentResolver().query(
								ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
								ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + id, null,
								null);
						if (phones != null && phones.moveToFirst()) {
							do {
								// 遍历所有号码
								String phoneNumber = phones
										.getString(phones
												.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
								// 添加到名称列表中：
								contactsList.add(name.replaceAll("-", "").trim() + "-"
										+ phoneNumber.replaceAll("-", "").trim());

							} while (phones.moveToNext());
							phones.close();
						}
					}
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("Exception:" + e);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		PinYinComparator cmp = new PinYinComparator();
		Collections.sort(contactsList, cmp);
		return contactsList;
	}

	/**
	 * 拼音首字母排序
	 */
	public static class PinYinComparator implements Comparator<String> {
		@Override
		public int compare(String lhs, String rhs) {
			String pinYinLhs = StringUtil.toPinYin(lhs, false);
			String pinYinRhs = StringUtil.toPinYin(rhs, false);
			return pinYinLhs.compareToIgnoreCase(pinYinRhs);
		}
	}
}
