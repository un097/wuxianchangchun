package cn.ffcs.wisdom.city.xg;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.personcenter.entity.Account;
import cn.ffcs.wisdom.city.utils.AppUtil;
import cn.ffcs.wisdom.city.utils.Callback;
import cn.ffcs.wisdom.city.web.BrowserActivity;
import cn.ffcs.wisdom.tools.StringUtil;
import cn.ffcs.wisdom.city.v6.R;


/**
 * Created by echo on 2016/1/28.
 */
public class XgReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (context == null || intent == null) return;
        MsgDialog msgDialog = new MsgDialog(context,
                intent.getStringExtra(Key.K_XG_NOTIFICATION_TITLE),
                intent.getStringExtra(Key.K_XG_NOTIFICATION_CONTENT))
                .setOkButtonText("查看")
                .setCancelButtonText("忽略");

        msgDialog.setCallback(new Callback<String>() {
            @Override
            public boolean onData(String data) {
               final String wap = intent.getStringExtra(Key.K_XG_NOTIFICATION_WAP);
                String url = intent.getStringExtra(Key.K_XG_NOTIFICATION_URL);
                String isLogin = intent.getStringExtra(Key.K_XG_NOTIFICATION_IS_LOGIN);
                if(!StringUtil.isEmpty(url)){
                  Intent it = new Intent(context, BrowserActivity.class);
                    Intent intent = new Intent(context, BrowserActivity.class);
                    intent.putExtra(Key.U_BROWSER_URL, url);
                    intent.putExtra(Key.K_RETURN_TITLE, context.getString(R.string.home_name));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(it);
                    return false;
                }
                if(!StringUtil.isEmpty(wap)){
                    if(Boolean.parseBoolean(isLogin)){
                        AppUtil.getCurrentUser(context, new Callback<Account>() {
                            @Override
                            public boolean onData(Account data) {
                                Intent intent = new Intent();
                                intent.setClassName(context, wap);
                                context.startActivity(intent);
                                return false;
                            }
                        });
                    }
                }
                return false;
            }
        });
        msgDialog.show();
    }
}