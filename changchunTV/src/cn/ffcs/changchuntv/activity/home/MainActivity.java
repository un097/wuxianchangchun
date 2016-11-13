package cn.ffcs.changchuntv.activity.home;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushManager;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import cn.ffcs.changchun_base.activity.BaseTabActivity;
import cn.ffcs.changchuntv.R;
import cn.ffcs.changchuntv.activity.explore.ExploreActivity1;
import cn.ffcs.changchuntv.activity.personal.PersonalActivity;
import cn.ffcs.ui.tools.AlertBaseHelper;
import cn.ffcs.wisdom.city.bo.LogReportBo;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.datamgr.LogReportMgr;
import cn.ffcs.wisdom.city.personcenter.bo.LoginBo;
import cn.ffcs.wisdom.city.personcenter.datamgr.AccountMgr;
import cn.ffcs.wisdom.city.personcenter.entity.Account;
import cn.ffcs.wisdom.city.personcenter.entity.Account.AccountData;
import cn.ffcs.wisdom.city.setting.about.VersionCheckBo;
import cn.ffcs.wisdom.city.setting.about.VersionResp;
import cn.ffcs.wisdom.city.setting.service.ListenNetStateService;
import cn.ffcs.wisdom.city.sqlite.model.LogItem;
import cn.ffcs.wisdom.city.sqlite.service.NotificationInfoService;
import cn.ffcs.wisdom.city.utils.AppUtil;
import cn.ffcs.wisdom.city.utils.Callback;
import cn.ffcs.wisdom.city.utils.LogReportUtil;
import cn.ffcs.wisdom.city.utils.TrafficStatisticsFlowUtils;
import cn.ffcs.wisdom.city.web.BrowserActivity;
import cn.ffcs.wisdom.city.xg.XgManager;
import cn.ffcs.wisdom.city.xg.XgPushMessageReceiver;
import cn.ffcs.wisdom.city.xg.XgReceiver;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.lbs.LBSLocationClient;
import cn.ffcs.wisdom.notify.MsgEntity;
import cn.ffcs.wisdom.push.PushUtil;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.CrytoUtils;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.SharedPreferencesUtil;
import cn.ffcs.wisdom.tools.StringUtil;

/**
 * 主页面
 *
 * @author Administrator
 */
public class MainActivity extends BaseTabActivity {
    private TabHost mTabHost = null;
    private String textArray[] = {"首页", "发现", "个人中心"};
    private Class fragmentArray[] = {HomeActivity.class,
            ExploreActivity1.class, PersonalActivity.class};
    private int iconArray[] = {R.drawable.tab_home, R.drawable.tab_explore,
            R.drawable.tab_personal};
    private long exitTime = 0;
    private boolean isFirstSendTraffic = true;// 是否第一次发送流量统计
    private boolean first = true;
    // 版本升级
    public static final String NORMAL = "1"; // 普通
    public static final String IMPORTANT = "2"; // 重要
    public static final String EXIGENCE = "3"; // 紧急
    String url = "";
    String activity = "";
    String isLogin = "";


    private XgReceiver receiver = new XgReceiver();
    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        xgPush();
    }

    private void xgPush(){
        XGPushClickedResult msg = (XGPushClickedResult)getIntent().getSerializableExtra("pushMsg");
        if(msg == null){
            return;
        }
        try {
            String customContent = msg.getCustomContent();
            if (customContent != null && customContent.trim().length() != 0) {
                JSONObject obj = new JSONObject(customContent);
                if (!obj.isNull(XgPushMessageReceiver.WAP)) {
                    url = obj.getString(XgPushMessageReceiver.WAP);
                }
                if (!obj.isNull(XgPushMessageReceiver.ACTIVITY)) {
                    activity = obj.getString(XgPushMessageReceiver.ACTIVITY);
                }
                if (!obj.isNull(XgPushMessageReceiver.IS_LOGIN)) {
                    isLogin = obj.getString(XgPushMessageReceiver.IS_LOGIN);
                }
                if (!StringUtil.isEmpty(url)) {
                    Intent intent = new Intent(mActivity, BrowserActivity.class);
                    intent.putExtra(Key.U_BROWSER_URL, url);
                    intent.putExtra(Key.K_RETURN_TITLE, mActivity.getString(R.string.home_name));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mActivity.startActivity(intent);
                    return;
                }
                if(!StringUtil.isEmpty(activity)){
                    if(Boolean.parseBoolean(isLogin)){
                        AppUtil.getCurrentUser(mContext, new Callback<Account>() {
                            @Override
                            public boolean onData(Account data) {
                                Intent intent = new Intent();
                                intent.setClassName(mActivity, activity);
                                mActivity.startActivity(intent);
                                return false;
                            }
                        });
                        return;
                    }else{
                        Intent intent = new Intent();
                        intent.setClassName(mActivity, activity);
                        mActivity.startActivity(intent);
                    }
                }
            }
        } catch (Exception e) {
            android.util.Log.w(getClass().getName(), "处理消息错误:" + msg, e);
        }
    }

    @Override
    protected void init(Bundle savedInstanceState) {

        mTabHost = (TabHost) getTabHost();
        for (int i = 0; i < 3; i++) {
            TabSpec tabSpec = mTabHost.newTabSpec("" + i)
                    .setIndicator(getIndicatorView(textArray[i], iconArray[i]))
                    .setContent(new Intent(this, fragmentArray[i]));
            mTabHost.addTab(tabSpec);
        }
        boolean isLogin = AccountMgr.getInstance().isLogin(
                getApplicationContext());
        if (isLogin) {
            refreshAccount();
        }

        super.init(savedInstanceState);
        netStateWarning();
    }


    // 3G流量提醒
    private void netStateWarning() {
        if (SharedPreferencesUtil.getBoolean(mContext, Key.K_3G_SWITCH) == false) {
            Intent service = new Intent(mContext, ListenNetStateService.class);
            startService(service);
        }
    }

    private View getIndicatorView(String name, int imgId) {
        View v = getLayoutInflater().inflate(R.layout.tab, null);
        TextView tv = (TextView) v.findViewById(R.id.tab);
        Drawable drawable = getResources().getDrawable(imgId);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                drawable.getMinimumHeight());
        tv.setCompoundDrawables(null, drawable, null, null);
        tv.setText(name);
        return v;
    }

    private void refreshAccount() {
        AccountData data = AccountMgr.getInstance().getAccount(mContext)
                .getData();
        try {
            String userName = data.getMobile();
            String password = CrytoUtils.decodeBy3DES(CrytoUtils.DESKEY,
                    URLDecoder.decode(data.getPassword(), "UTF-8"));
            new LoginBo(new LoginCallback(), mActivity).login(userName,
                    password, mContext);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 登陆回调 （自动登陆）
    class LoginCallback implements HttpCallBack<BaseResp> {
        @Override
        public void call(BaseResp resp) {
            if (resp.isSuccess()) {
                Account account = (Account) resp.getObj();
                // 刷新帐号
                AccountMgr.getInstance().refresh(mActivity, account);
                XgManager.xg_register(mContext);
            }
        }

        @Override
        public void progress(Object... obj) {
        }

        @Override
        public void onNetWorkError() {
        }
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        registerReceiver(receiver, new IntentFilter(XgPushMessageReceiver.NEWS_PUSH_ACTION_NAME_CC));
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (first) {
            first = false;
            // 日志上报
            logReport();
            // 自动检测升级，caijj 2013-03-29
            autoCheckVersion();
        }
    }

    /**
     * 日志上传
     */
    private void logReport() {
        LogReportBo logBo;
        final List<LogItem> logs = LogReportMgr.getInstance().queryLogs(
                mContext);
        if (logs != null && logs.size() > 0) {
            if (CommonUtils.isNetConnectionAvailable(mContext)) {
                String jsonBody = LogReportUtil.logItemToJson(mContext, logs);
                if (!StringUtil.isEmpty(jsonBody)) {
                    logBo = new LogReportBo(mActivity,
                            new HttpCallBack<BaseResp>() {
                                @Override
                                public void call(BaseResp response) {
                                    if (response.isSuccess()) {// 上传成功，删除缓存
                                        LogReportMgr.getInstance().deleteLogs(
                                                mContext, logs);
                                    } else {
                                        Log.e("日志上传失败");
                                    }
                                }

                                @Override
                                public void progress(Object... obj) {

                                }

                                @Override
                                public void onNetWorkError() {

                                }
                            });
                    logBo.reportLogs(jsonBody);
                }
            }
        }
    }

    /**
     * 自动检测升级 add by caijj 2013-03-29
     */
    private void autoCheckVersion() {
        VersionCheckBo versionBo = new VersionCheckBo(mContext,
                new HttpCallBack<BaseResp>() {

                    @Override
                    public void progress(Object... obj) {
                    }

                    @Override
                    public void onNetWorkError() {
                    }

                    @Override
                    public void call(BaseResp response) {
                        if (response.isSuccess()) {
                            VersionResp verResp = (VersionResp) response
                                    .getObj();
                            boolean needUpdate = verResp.getNeedupdate();
                            String updateType = verResp.getUpdate_type();
                            if (needUpdate) { // 需升级
                                String downLoadUrl = verResp.getDownload_url();
                                if (EXIGENCE.equals(updateType)) {
                                    AlertBaseHelper
                                            .showConfirm(
                                                    mActivity,
                                                    getString(R.string.version_high_update),
                                                    "\n"
                                                            + verResp
                                                            .getUpdate_desc()
                                                            + "\n", "", "",
                                                    new HighUpdateVersion(
                                                            downLoadUrl),
                                                    new CancelOnclick());
                                } else {
                                    AlertBaseHelper.showConfirm(mActivity,
                                            getString(R.string.version_update),
                                            "\n" + verResp.getUpdate_desc()
                                                    + "\n", new UpdateVersion(
                                                    downLoadUrl));
                                }
                            }
                        }
                    }
                });
        versionBo.check();
    }

    /**
     * 强制升级 xzw 2013-04-01
     */
    private class HighUpdateVersion implements OnClickListener {

        private String downLoadUrl;

        public HighUpdateVersion(String downLoadUrl) {
            this.downLoadUrl = downLoadUrl;
        }

        @Override
        public void onClick(View v) {
            Uri uri = Uri.parse(downLoadUrl);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
            AlertBaseHelper.dismissAlert(mActivity);
            doExit();
        }
    }

    /**
     * 强制取消事件 xzw 2013-04-01
     */
    class CancelOnclick implements OnClickListener {

        @Override
        public void onClick(View v) {
            CommonUtils.showToast(mActivity,
                    getString(R.string.version_high_update_desc),
                    Toast.LENGTH_SHORT);
            doExit();
        }
    }

    private class UpdateVersion implements OnClickListener {

        private String downLoadUrl;

        public UpdateVersion(String downLoadUrl) {
            this.downLoadUrl = downLoadUrl;
        }

        @Override
        public void onClick(View v) {
            Uri uri = Uri.parse(downLoadUrl);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
            AlertBaseHelper.dismissAlert(mActivity);
        }
    }

    public void onBack() {
        if (mTabHost.getCurrentTab() != 0) {
            mTabHost.setCurrentTab(0);
        } else {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序",
                        Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                doExit();
            }
        }
    }

    public void onTab(int tab) {
        mTabHost.setCurrentTab(tab);
    }

    /**
     * 做退出操作
     */
    private void doExit() {
        sendTrafficStats();
        // 关闭定位服务
        LBSLocationClient mLBSLocationClient = LBSLocationClient
                .getInstance(mContext);
        mLBSLocationClient.stopLocationService();
        exitApp();
        Intent service = new Intent(mContext, ListenNetStateService.class);
        stopService(service);
    }

    /**
     * 上报流量统计
     */
    private void sendTrafficStats() {
        if (isFirstSendTraffic) {// 是否第一次发送流量统计
            TrafficStatisticsFlowUtils.stopTrafficStats(mContext);
            isFirstSendTraffic = false;
        } else {
            Log.d("本次应用关闭时已经统计过了");
        }
    }
}
