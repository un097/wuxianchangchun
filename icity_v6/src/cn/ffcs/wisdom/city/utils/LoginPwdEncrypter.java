package cn.ffcs.wisdom.city.utils;

import java.net.URLEncoder;

import cn.ffcs.wisdom.tools.DesEncrypter;
//import cn.ffcs.pay.utils.DesEncrypter;
import cn.ffcs.wisdom.tools.Log;

public class LoginPwdEncrypter {
	
	private static final String DES_KEY = "b5eefe0437d945b98e82f46fbff8d3552c2ff6f7f8acd8de";
	
	public static String EncryPwd(String password) {
		String sign = "";
		try {
			sign = DesEncrypter.encodeBy3DES(DES_KEY, password);
			sign = URLEncoder.encode(sign, "UTF-8");
		} catch (Exception e) {
			Log.e("密码加密异常，异常信息：" + e);
		}
		return sign;
	}

}
