package cn.ffcs.wisdom.city.simico.kit.util;

public class StringUtils {

	public static boolean isEmpty(String str) {
		boolean flag;
		if (str == null || str.length() == 0)
			flag = true;
		else
			flag = false;
		return flag;
	}

	public static boolean isEqual(String str1, String str2) {
		boolean flag;
		if (isEmpty(str1) && isEmpty(str2) || str1 != null && str1.equals(str2))
			flag = true;
		else
			flag = false;
		return flag;
	}
}
