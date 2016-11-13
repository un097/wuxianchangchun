package cn.ffcs.wisdom.city.utils;

import android.content.Context;
import android.content.Intent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.ffcs.wisdom.city.personcenter.datamgr.AccountMgr;
import cn.ffcs.wisdom.city.personcenter.entity.Account;
import cn.ffcs.wisdom.city.personcenter.utils.AccountUtil;

/**
 * Created by Administrator on 2016/3/18.
 */
public class AppUtil {

    public final static Map dataMap = new ConcurrentHashMap();

    public static Account getCurrentUser(Context ctx){
        return AccountMgr.getInstance().getAccount_1(ctx);
    }

    public static Intent getCurrentUser(Context ctx, Callback<Account> callback) {
        //如果没有登录
        if (getCurrentUser(ctx) == null) {
            //设置回调
            Intent intent = new Intent(ctx, cn.ffcs.wisdom.city.personcenter.LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            putValue(cn.ffcs.wisdom.city.personcenter.LoginActivity.class.getName(), callback);
            ctx.startActivity(intent);
            return intent;
        } else {
            if (callback != null)
                callback.onData(getCurrentUser(ctx));
        }
        return null;
    }


    public static <T> T getValue(Object key){
        return (T) dataMap.remove(key);
    }

    public static <T> T putValue(Object key, Object newValue){
        return (T) dataMap.put(key, newValue);
    }




}
