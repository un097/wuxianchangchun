package cn.ffcs.wisdom.city.personcenter.utils;

import java.io.File;

import android.content.Context;
import cn.ffcs.wisdom.city.personcenter.entity.Account;
import cn.ffcs.wisdom.tools.FileUtils;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.StringUtil;
/**
 * <p>Title: 账户工具类         </p>
 * <p>Description: 包括
 *  1. 保存账户对象信息
 *  2. 删除账户对象信息
 *  3. 判断是否存在该账户信息
 *  4. 读取账户对象信息
 * </p>
 * <p>@author: Eric.wsd                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2012-6-3             </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class AccountUtil {

	public static final String ACCOUNT_PREFERENCE = "accountInfo";
	public static final String Account_PREFERENCE_NAME = "name";
	public static final String Account_PREFERENCE_MOBILE = "mobile";
	public static final String Account_PREFERENCE_PWD = "pwd";
	public static final String Account_PREFERENCE_EMAIL = "email";
	public static final String Account_PREFERENCE_ADDRESS = "address";
	public static final String Account_PREFERENCE_GENDER = "gender";

	/**
	 * 保存账户信息
	 * @param context
	 * @param account
	 */
	public static void saveAccountInfo(final Context context, final Account account) {
		if (context != null && account != null) {
			removeAccountInfo(context);
			FileUtils.write(context, ACCOUNT_PREFERENCE, account);
		}
	}

	/**
	 * 删除用户信息
	 * @param ctx
	 */
	public static void removeAccountInfo(Context ctx) {
		File accountFile = ctx.getFileStreamPath(ACCOUNT_PREFERENCE);
		if (accountFile != null && accountFile.exists()) {
			accountFile.delete();
		}
	}

	/**
	 * 获取用户信息
	 * @param context
	 * @return
	 */
	public static Account readAccountInfo(Context context) {
		Account mAccount = null;
		try {
			Object obj = FileUtils.read(context, ACCOUNT_PREFERENCE);
			if (obj != null) {
				mAccount = (Account) obj;
			}

			if (mAccount == null) {
				return new Account();
			}
		} catch (Exception e) {
			Log.e(e.getMessage());
			return new Account();
		}

		return mAccount;
	}

	/**
	 * 用户是否存在
	 * @param account
	 * @return
	 */
	public static boolean isAccountExist(Account account) {
		if (account == null) {
			return false;
		}
		if (!StringUtil.isEmpty(account.getData().getMobile())) {
			return true;
		}
		return false;
	}
}
