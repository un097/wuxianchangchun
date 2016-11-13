package cn.ffcs.changchuntv.request;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;

import org.json.JSONObject;

import java.util.Map;

import cn.ffcs.wisdom.base.CommonStandardTask;
import cn.ffcs.wisdom.city.R;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.simico.api.request.CityPostRequest;
import cn.ffcs.wisdom.city.simico.base.Application;

/**
 * Created by Admin on 2015/11/18.
 */
public class RegisterPostRequest extends CityPostRequest {
    private Map<String, String> mParams;
    public static final String serverRoot = Config.GET_SERVER_ROOT_URL();

    public static final String REGISTER_RUL = serverRoot + "icity-api-client-major/icity/service/user/mobileregister/encryptmobileregister";

    public RegisterPostRequest(Map<String, String> mParams, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(REGISTER_RUL, listener, errorListener);
        this.mParams=mParams;
    }

    /**
     * Client(产品标示;系统标示;设备标示;分辨率[;其他扩增信息]) 产品标示=主产品标示[-子产品标示]/版本号 系统标示=系统标示/版本号
     * 设备标示=iphone4|iphone5|xiaomi|... 分辨率=宽*高
     */
    public void setAgent() {
        try {
            String agent = CommonStandardTask.getUserAgent(Application.context(), Application.context().getString(R.string.version_name_update));
            getHeaders().put("User-Agent", agent);
        } catch (AuthFailureError e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, String> getParams() {
        return mParams;
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Map getParamsV2() {
        return this.getParams();
    }

    @Override
    public String getBodyContentType() {
        return "application/json";
    }
}
