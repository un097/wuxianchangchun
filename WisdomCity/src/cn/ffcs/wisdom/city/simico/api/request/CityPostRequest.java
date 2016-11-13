package cn.ffcs.wisdom.city.simico.api.request;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.ffcs.wisdom.city.simico.kit.log.TLog;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;

public abstract class CityPostRequest extends Request<JSONObject> {

	private Listener<JSONObject> mListener;

	public CityPostRequest(String url, Listener<JSONObject> listener, ErrorListener errorListener) {
		super(Method.POST, url, errorListener);
		mListener = listener;
	}

	@Override
	public byte[] getBody() throws AuthFailureError {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			JSONArray array = getJSONArray();
			if (array != null) {
				String jsonString = array.toString();
				bos.write(jsonString.getBytes());
				return bos.toByteArray();
			}
			JSONObject json = new JSONObject();
			@SuppressWarnings("rawtypes")
			Map params = getParamsV2();
			if (params == null) {
				params = getParams();
			}
			if (params != null) {
				@SuppressWarnings("unchecked")
				Iterator<String> itr = params.keySet().iterator();
				while (itr.hasNext()) {
					String key = itr.next();
					Object val = params.get(key);
					if (val instanceof File) {
						throw new RuntimeException("CityPostRequest unsupport upload file");
					}
					if (val != null && (!(val instanceof JSONArray || val instanceof String))) {
						val = val.toString();
					}
					json.put(key, val);
				}
				String jsonString = json.toString();
				bos.write(jsonString.getBytes());
				return bos.toByteArray();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return super.getBody();
	}

	@Override
	public String getBodyContentType() {
		return super.getBodyContentType();
	}

	public JSONArray getJSONArray() {
		return null;
	}

	public abstract Map<String, String> getParams();

	/**
	 * 获取请求参数
	 * Value可选为String | JSONObject
	 */
	public Map<String, Object> getParamsV2() {
		return null;
	}

	@Override
	protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
		try {
			String jsonString = new String(response.data,
					HttpHeaderParser.parseCharset(response.headers));
			TLog.log("result:"+jsonString);
			return Response.success(new JSONObject(jsonString),
					HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JSONException je) {
			return Response.error(new ParseError(je));
		}
	}

	@Override
	protected void deliverResponse(JSONObject response) {
		if (mListener != null) {
			mListener.onResponse(response);
		}
	}
}
