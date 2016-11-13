package cn.ffcs.wisdom.city.xg;

import android.content.Context;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;

import cn.ffcs.wisdom.city.personcenter.datamgr.AccountMgr;
import cn.ffcs.wisdom.city.personcenter.entity.Account;
import cn.ffcs.wisdom.push.PushConstant;
import cn.ffcs.wisdom.push.PushUtil;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.SharedPreferencesUtil;
import cn.ffcs.wisdom.tools.StringUtil;

/**
 * Created by Administrator on 2016/3/14.
 */
public class XgManager {
    private static final String TAG = "fmj";


    /**
     * 信鸽注册
     * @param ctx
     */
    private static void register(Context ctx){
        XGPushManager.registerPush(ctx, new XGIOperateCallback() {
            @Override
            public void onSuccess(Object arg0, int arg1) {
                android.util.Log.e(TAG,"===============信鸽注册成功");
            }

            @Override
            public void onFail(Object arg0, int arg1, String arg2) {
                android.util.Log.e(TAG,"===============信鸽注册失败");
            }
        });
    }

    /**
     * 启动并注册APP，同时绑定账号
     * （推荐有帐号体系的APP使用）
     * @param ctx
     */
    public static void xg_register(Context ctx){
        enableDebug(ctx);
        try{
            if(!PushUtil.getPushEnabled(ctx)){
                return;
            }
            Account account = AccountMgr.getInstance().getAccount_1(ctx);
            if(null == account || null == account.getData() || StringUtil.isEmpty(account.getData().getMobile())){
                register(ctx);
                return;
            }
            XGPushManager.registerPush(ctx, account.getData().getMobile(), new XGIOperateCallback(){
                @Override
                public void onSuccess(Object o, int i) {
                    android.util.Log.e(TAG,"===============信鸽绑定账号注册成功");
                }

                @Override
                public void onFail(Object o, int i, String s) {
                    android.util.Log.e(TAG,"===============信鸽绑定账号注册失败");
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void xg_register(Context ctx, String mobile){
        try{
            if(StringUtil.isEmpty(mobile)){
                xg_register(ctx);
                return;
            }
            XGPushManager.registerPush(ctx, mobile, new XGIOperateCallback(){
                @Override
                public void onSuccess(Object o, int i) {
                    android.util.Log.e(TAG,"===============信鸽绑定账号注册成功");
                }

                @Override
                public void onFail(Object o, int i, String s) {
                    android.util.Log.e(TAG,"===============信鸽绑定账号注册失败");
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private static void enableDebug(Context ctx){
        XGPushConfig.enableDebug(ctx, false);
    }


    /**
     * 反注册，建议在不需要接收推送的时候调用
     * @param ctx
     */
    public static void unregisterPush(Context ctx){
        XGPushManager.unregisterPush(ctx);
    }
}
