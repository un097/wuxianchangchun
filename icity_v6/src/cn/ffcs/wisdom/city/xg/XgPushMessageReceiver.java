package cn.ffcs.wisdom.city.xg;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;

import org.json.JSONException;
import org.json.JSONObject;

import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.sqlite.service.NotificationInfoService;
import cn.ffcs.wisdom.notify.*;

/**
 * Created by Administrator on 2016/3/15.
 */
public class XgPushMessageReceiver extends XGPushBaseReceiver {
    private static final String TAG = "XgPushMessageReceiver";
    public static final String ACTIVITY = "activity";
    public static final String WAP = "wap";
    public static final String IS_LOGIN = "isLogin";
    public static final String NEWS_PUSH_ACTION_NAME_CC = "RECEIVED_PUSH_NEWS_CC";

    private void show(Context context, String text) {
        Log.i("XGPushBaseReceiver ", text);
    }

    @Override
    public void onNotifactionShowedResult(Context context, XGPushShowedResult result) {
        Log.e(TAG, "XGPushShowedResult=======" + result.getCustomContent());
        if (null == result || null == context) {
            return;
        }
        dispatchMsg(context, result);
        show(context, "您有1条新消息, 通知被展示:" + result.toString());
    }


    private void dispatchMsg(Context context, XGPushShowedResult msg){
        try {
            String activity = "";
            String wap = "";
            String isLogin = "";
            String customContent = msg.getCustomContent();
            if (customContent != null && customContent.trim().length() != 0) {
                JSONObject obj = new JSONObject(customContent);
                if (!obj.isNull(ACTIVITY)) {
                    activity = obj.getString(ACTIVITY);
                }
                if (!obj.isNull(WAP)) {
                    wap = obj.getString(WAP);
                }
                if (!obj.isNull(IS_LOGIN)) {
                    isLogin = obj.getString(IS_LOGIN);
                }
                MsgEntity entity = new MsgEntity();
                entity.setTitle(msg.getContent());
                String timestamp = System.currentTimeMillis() + "";
                timestamp = timestamp.substring(4);
                entity.setIdmMsgId(timestamp);
                MsgEntity.Content mContent = entity.new Content();
                mContent.setMsgId(timestamp);
                mContent.setMsgContent(msg.getContent());
                mContent.setMsgContent(System.currentTimeMillis() + "");   //保存时间
                mContent.setWap(wap);
                mContent.setActivity(activity);
                mContent.setIsLogin(isLogin);
                mContent.setDesc(wap);
                entity.setContent(mContent);
                NotificationInfoService service = NotificationInfoService.getInstance(context);
                service.save(context, entity);
                NotificationInfoService.getInstance(context).updateNewById(timestamp);

//                Intent intent = new Intent(NEWS_PUSH_ACTION_NAME_CC)
//                        .putExtra(Key.K_XG_NOTIFICATION_TITLE, msg.getTitle())
//                        .putExtra(Key.K_XG_NOTIFICATION_CONTENT, msg.getContent())
//                        .putExtra(Key.K_XG_NOTIFICATION_WAP, wap)
//                        .putExtra(Key.K_XG_NOTIFICATION_URL,url)
//                        .putExtra(Key.K_XG_NOTIFICATION_IS_LOGIN, isLogin);
//                context.sendBroadcast(intent);
//                XGPushManager.clearLocalNotifications(context);
            }
        } catch (Exception e) {
            Log.w(getClass().getName(), "处理消息错误:" + msg, e);
        }
    }




    @Override
    public void onRegisterResult(Context context, int i, XGPushRegisterResult result) {
        if (null == context || null == result) {
            return;
        }
        String text = "";
        if (i == XGPushBaseReceiver.SUCCESS) {
            text = result + "###注册成功";
            // 在这里拿token
            String token = result.getToken();
        } else {
            text = result + "注册失败，错误码：" + i;
        }
        Log.e(TAG, "onRegisterResult=========" + text);
    }

    @Override
    public void onUnregisterResult(Context context, int i) {
        if (context == null) {
            return;
        }
        String text = "";
        if (i == XGPushBaseReceiver.SUCCESS) {
            text = "反注册成功";
        } else {
            text = "反注册失败" + i;
        }
        Log.e(TAG, "onUnregisterResult=========" + text);
    }

    @Override
    public void onSetTagResult(Context context, int i, String s) {
        if (context == null) {
            return;
        }
        String text = "";
        if (i == XGPushBaseReceiver.SUCCESS) {
            text = "\"" + s + "\"设置成功";
        } else {
            text = "\"" + s + "\"设置失败,错误码：" + i;
        }
        Log.e(TAG, "onSetTagResult=========" + text);
    }

    @Override
    public void onDeleteTagResult(Context context, int i, String s) {
        if (context == null) {
            return;
        }
        String text = "";
        if (i == XGPushBaseReceiver.SUCCESS) {
            text = "\"" + s + "\"删除成功";
        } else {
            text = "\"" + s + "\"删除失败,错误码：" + i;
        }
        Log.e(TAG, "onDeleteTagResult======" + text);
    }

    @Override
    public void onTextMessage(Context context, XGPushTextMessage xgPushTextMessage) {
        if (context == null || xgPushTextMessage == null) {
            return;
        }
        String text = "收到消息:" + xgPushTextMessage.toString();
        // 获取自定义key-value
        String customContent = xgPushTextMessage.getCustomContent();
        String value = null;
        if (customContent != null && customContent.length() != 0) {
            try {
                JSONObject obj = new JSONObject(customContent);
                // key1为前台配置的key
                if (!obj.isNull("key")) {
                    value = obj.getString("key");
                    Log.e(TAG, "get custom value:=====" + value);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // APP自主处理消息的过程...
        Log.e(TAG, "onTextMessage========" + text);
    }

    @Override
    public void onNotifactionClickedResult(Context context, XGPushClickedResult msg) {
        if (msg == null) {
            return;
        }

        String text = "";
        if (msg.getActionType() == XGPushClickedResult.NOTIFACTION_CLICKED_TYPE) {
            // 通知在通知栏被点击啦。。。。。
            // APP自己处理点击的相关动作
            // 这个动作可以在activity的onResume也能监听，请看第3点相关内容
            text = "通知被打开 :" + msg;
        } else if (msg.getActionType() == XGPushClickedResult.NOTIFACTION_DELETED_TYPE) {
            // 通知被清除啦。。。。
            // APP自己处理通知被清除后的相关动作
            text = "通知被清除 :" + msg;
        }

        show(context, text);
    }
}
