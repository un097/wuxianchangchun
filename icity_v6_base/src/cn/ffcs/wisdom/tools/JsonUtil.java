package cn.ffcs.wisdom.tools;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

/**
 * <p>Title: JSON工具类       </p>
 * <p>Description: 
 * JSON工具类  
 * </p>
 * <p>@author: Eric.wsd                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2012-6-26             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class JsonUtil {

	/**
	 * 对象转Json字符串
	 * @param entity 需要转换的对象实体
	 * @return
	 */
	public static String toJson(Object entity) {
		if(entity == null) {
			return "";
		}
		try {
			Gson gson = new Gson();
			return gson.toJson(entity);
		}catch(Exception e) {
			Log.e("toJson 异常。", e);
			return "";
		}
	}

	/**
	 * Json字符串转对象
	 * @param <T>
	 * @param result	Json字符串
	 * @return
	 */
	public static <T> T toObject(String result, Class<T> clazz) {
		if(clazz == null || StringUtil.isEmpty(result)) {
			return null;
		}
		
		try {
			Gson gson = new Gson();
			return gson.fromJson(result, clazz);
		}catch(Exception e) {
			Log.e("JSON 转换异常！"+ e + "     result==" + result + "            clazz===" + clazz.toString());
			try {
				return clazz.newInstance();
			} catch (IllegalAccessException e1) {
				Log.e("toObject IllegalAccessException 实例化异常", e1);
			} catch (InstantiationException e1) {
				Log.e("toObject IllegalAccessException 实例化异常", e1);
			}
		}
		return null;
	}

	/**
	 * 转换字符串为Json对象
	 * @param json json字符串
	 * @return
	 */
	public static JSONObject parseJSON(String json) {
		try {
			return new JSONObject(json);
		} catch (JSONException e) {
			Log.e("JSONException :" + e);
		}
		return new JSONObject();
	}
}
