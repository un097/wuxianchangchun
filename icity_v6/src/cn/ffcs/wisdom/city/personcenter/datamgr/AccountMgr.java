package cn.ffcs.wisdom.city.personcenter.datamgr;

import android.content.Context;
import cn.ffcs.wisdom.base.DataManager;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.personcenter.entity.Account;
import cn.ffcs.wisdom.city.personcenter.utils.AccountUtil;
import cn.ffcs.wisdom.tools.AppHelper;
import cn.ffcs.wisdom.tools.SharedPreferencesUtil;

/**
 * <p>Title:账号管理          </p>
 * <p>Description: 
 * 违章信息部分尚未完整
 * </p>
 * <p>@author: Leo                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-2-27             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class AccountMgr extends DataManager {

	private static AccountMgr mInstance = new AccountMgr();

	private Account mAccount;
	static final Object sInstanceSync = new Object();

	private AccountMgr() {
	}

	public static AccountMgr getInstance() {
		synchronized (sInstanceSync) { // 规避多线程导致重复创建对象
			if (mInstance == null)
				mInstance = new AccountMgr();
		}

		return mInstance;
	}

	public void refresh(Context context, Account account) {
		if (account == null)
			return;

		this.mAccount = account;
		// 保存到缓存中，供新闻网使用，判断是否登陆
		AccountUtil.saveAccountInfo(context, account);
		SharedPreferencesUtil.setValue(context, Key.K_IS_LOGIN, "true");
		SharedPreferencesUtil.setValue(context, Key.K_PHONE_NUMBER, account.getData().getMobile());// 保存电话

		notifyDataSetChanged();
	}

	/**
	 * logout
	 * clear local account info
	 */
	public void loginOut(Context context) {
		// 保存本地用户信息，是否登录，用登录标识判断  modify by caijj 2013-3-16
		// AccountUtil.removeAccountInfo(context); // 移除缓存本地帐号信息

		// 厦门网账户绑定去除。
		SharedPreferencesUtil.setBoolean(context, Key.K_BIND_NEWS_ACCOUNT, false);

		// 修改登录标志
		SharedPreferencesUtil.setValue(context, Key.K_IS_LOGIN, "false");

		// 把手机号码设置为空
		SharedPreferencesUtil.setValue(context, Key.K_PHONE_NUMBER, "");

		mAccount = null;
		notifyDataSetChanged();
		// 删除违章信息
		// WzCarInfoService.getInstance(context).deleteAllWz();
	}

	public String getMobile(Context context) {
		mAccount = getAccount(context);
		if (mAccount != null) {
			if (mAccount.getData() != null) {
				return mAccount.getData().getMobile();
			}
		}
		return "";
	}

	public int getUserId(Context context) {
		mAccount = getAccount(context);
		if (mAccount.getData() != null) {
			return mAccount.getData().getUserId();
		}
		return -1;
	}

	public Account getAccount(Context context) {
		// 登录状态，才返回信息  modify by caijj 2013-03-18
		if (isLogin(context)) {
			if(mAccount == null){
				mAccount = AccountUtil.readAccountInfo(context);
			}
			return mAccount;
		}
		return new Account();
	}

	public Account getAccount_1(Context context) {
		// 登录状态，才返回信息  modify by caijj 2013-03-18
		if (isLogin(context)) {
			if(mAccount == null){
				mAccount = AccountUtil.readAccountInfo(context);
			}
			return mAccount;
		}
		return null;
	}

	// add by caijj 获取用户本地信息
	// 用途：应对自动登录接口无返回头像地址，等级名称。这种情况下，从本地获取头像地址等信息
	// 2013-3-18
	public Account getAccountLocal(Context context) {
		return AccountUtil.readAccountInfo(context);
	}
	
	// add by caijj 获取用户本地电话号码
	// 用途：向平台注册token时，用到的参数。
	// 2013-3-28
	public String getMobileLocal(Context context) {
		Account account = getAccountLocal(context);
		if (account != null && account.getData() != null) {
			return account.getData().getMobile();
		}
		return "";
	}

	public String getImsi(Context context) {
		return AppHelper.getMobileIMSI(context);
	}

	public boolean isLogin(Context context) {
		String login = SharedPreferencesUtil.getValue(context, Key.K_IS_LOGIN);
		return "true".equals(login);
	}
}
