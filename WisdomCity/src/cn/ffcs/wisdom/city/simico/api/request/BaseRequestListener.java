package cn.ffcs.wisdom.city.simico.api.request;

import org.json.JSONObject;

import android.app.Activity;
import android.text.TextUtils;
import cn.ffcs.wisdom.city.R;
import cn.ffcs.wisdom.city.simico.api.model.ApiResponse;
import cn.ffcs.wisdom.city.simico.base.Application;
import cn.ffcs.wisdom.city.simico.kit.application.BaseApplication;
import cn.ffcs.wisdom.city.simico.kit.util.TDevice;

import com.android.volley.Response.Listener;
import com.android.volley.VolleyLog;

public abstract class BaseRequestListener implements Listener<JSONObject> {
    
    public static String getErrorMessage(ApiResponse response) {
        if (response == null || !response.isOk()) {
            if (response == null) {
                return "你的网络不给力啊";
            } else {
                if (!TextUtils.isEmpty(response.getMessage())) {
                    return response.getMessage();
                } else {
                    return "你的网络不给力啊";
                }
            }
        }
        return "";
    }

    public static void showErrorMessage(ApiResponse response) {
        if (!TDevice.hasInternet()) {
            Application.showToast(R.string.empty_network);
        } else {
            if (response == null || !response.isOk()) {
                if (response == null) {
                    Application.showToast(R.string.empty_error);
                } else {
                    if (!TextUtils.isEmpty(response.getMessage())) {
                        BaseApplication.showToast(response.getMessage());
                    } else {
                        Application.showToast(R.string.empty_error);
                    }
                }
            }else{
                Application.showToast(R.string.empty_error);
            }
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        ApiResponse resp = new ApiResponse(response);
        try {
            if (response != null) {
                boolean isOk = response.optInt("result_code")==0;
                //response.optBoolean("is_ok");
                if (isOk) {
                    onRequestSuccess(resp);
                } else {
                    onRequestFaile(resp, new Exception("response is not ok!"));
                }
            } else {
                onRequestFaile(resp, new Exception("response is null!"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            onRequestFaile(resp, e);
            VolleyLog.e("request error:", e.getMessage());
        } finally {
            onRequestFinish();
        }
    }

    protected Activity getCurrentActivity() {
        return null;
    }

    /**
     * 服务端请求成功
     * 
     * @param json
     */
    public abstract void onRequestSuccess(ApiResponse json) throws Exception;

    /**
     * 服务端请求失败
     * 
     * @param json
     * @param e
     */
    public abstract void onRequestFaile(ApiResponse json, Exception e);

    public abstract void onRequestFinish();
}
