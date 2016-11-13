package cn.ffcs.wisdom.tools;

import java.util.Collection;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import android.text.TextUtils;

/**
 * <p>Title: 字符串工具类         </p>
 * <p>Description: </p>
 *  <p>包括如下功能：</p>
 *  <p>1. 判断字符串是否为空</p>
 *  <p>2. 判断是否手机号码</p>
 *  <p>3. 判断密码是否可用</p>
 *  <p>4. 比较字符串是否相同</p>
 *  <p>5. 判断是否为纯数字</p>
 * 	<p>6. HTML 编码</p>
 *  <p>7. String转拼音</p>
 *  <p>8. 判断字符串是否为拼音</p>
 *  <p>9. 判断字符串是否包含中文</p>
 * <p>@author: Eric.wsd                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2012-6-13             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class StringUtil {

	public static String join(Collection<String> s, String delimiter) {
		if (s.size() == 0)
			return "";
		StringBuilder sb = new StringBuilder();
		for (String str : s) {
			sb.append(str).append(delimiter);
		}
		if (sb.length() > 0)
			sb.delete(sb.length() - 1, sb.length());
		return sb.toString();
	}

	/**
	 * 判断字符串是否为空
	 */
	public static boolean isEmpty(String s) {
		return TextUtils.isEmpty(s);
	}

	/**
	 * 是否手机号码
	 * @param mobiles
	 * @return
	 */
//	public static boolean isMobile(String mobiles) {
//		if (StringUtil.isEmpty(mobiles)) {
//			return false;
//		}
//		Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
//		Matcher m = p.matcher(mobiles);
//		return m.matches();
//	}

	/**
	 * 密码是否可用
	 * 密码最长为20位    panxd
	 * @param pwd
	 * @return
	 */
	public static boolean isPwdValid(String pwd) {
		Pattern p = Pattern.compile("[a-zA-Z0-9]{0,20}");
		Matcher m = p.matcher(pwd);

		return m.matches();
	}

	/**
	 * 比较字符串是否相同
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean equals(CharSequence a, CharSequence b) {
		return TextUtils.equals(a, b);
	}

	/**
	 * Returns whether the given CharSequence contains only digits
	 * @param str
	 * @return
	 */
	public static boolean isDigitsOnly(CharSequence str) {
		return TextUtils.isDigitsOnly(str);
	}

	/**
	 * html encode
	 * @param s
	 * @return
	 */
	public static String htmlEncode(String s) {
		return TextUtils.htmlEncode(s);
	}

	/**
	 * 由全角转半角
	 * @param input
	 * @return
	 */
	public static String toSBC(String s) {
		if (StringUtil.isEmpty(s)) {
			return "";
		}
		char[] c = s.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 32) {
				c[i] = (char) 12288;
				continue;
			}
			if (c[i] < 127 && c[i] > 32)
				c[i] = (char) (c[i] + 65248);
		}
		return new String(c);
	}
	
	/**
	 * 将所有的数字、字母及标点全部转为全角字符，使它们与汉字同占两个字节
	 * @param input
	 * @return
	 */
	public static String ToDBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 12288) {
				c[i] = (char) 32;
				continue;
			}
			if (c[i] > 65280 && c[i] < 65375)
				c[i] = (char) (c[i] - 65248);
		}
		return new String(c);
	}

	/**
	 * char转拼音
	 * @param c
	 * @return
	 */
	public static String toPinYin(char c) {
		HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
		format.setCaseType(HanyuPinyinCaseType.LOWERCASE); //设置大小写
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE); //不输出音调
		format.setVCharType(HanyuPinyinVCharType.WITH_V); //拼音中的u输出为v 例如lv

		try {
			String[] result = PinyinHelper.toHanyuPinyinStringArray(c, format);
			if (result == null) {
				return null; //如果传入的不是汉字，例如A，则返回数组为null
			} else {
				return result[0];
			}
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * String转拼音
	 * @param str
	 * @param split 是否逗号分割
	 * @return
	 */
	public static String toPinYin(String str, boolean split) {
		StringBuilder sb = new StringBuilder();
		if (split) {
			for (int i = 0; i < str.length(); i++) {
				char c = str.charAt(i);
				if (toPinYin(c) == null) {
					sb.append(c);
					sb.append(",");
				} else {
					sb.append(toPinYin(c));
					sb.append(",");
				}
			}
		} else {
			for (int i = 0; i < str.length(); i++) {
				char c = str.charAt(i);
				if (toPinYin(c) == null) {
					sb.append(c);
				} else {
					sb.append(toPinYin(c));
				}
			}
		}
		return sb.toString();
	}
	
    /**  
     * 汉字 转为 汉语拼音首字母，英文字符不变  
     * @param chines 汉字  
     * @return 拼音  
     */     
	public static String toFirstSpell(String chines) {
		String pinyinName = "";
		char[] nameChar = chines.toCharArray();
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		for (int i = 0; i < nameChar.length; i++) {
			if (nameChar[i] > 128) {
				try {
					pinyinName += PinyinHelper.toHanyuPinyinStringArray(
							nameChar[i], defaultFormat)[0].charAt(0);
				} catch (BadHanyuPinyinOutputFormatCombination e) {
					e.printStackTrace();
				}
			} else {
				pinyinName += nameChar[i];
			}
		}
		return pinyinName;
	}
	
	/**
	 * 是否为拼音字符串
	 * @param str
	 * @return
	 */
	public static boolean isPinYin(String str) {
		Pattern pattern = Pattern.compile("[a-zA-Z]*");
		return pattern.matcher(str).matches();
	}
	
	/**
	 * 是否包含中文
	 * @param str
	 * @return
	 */
	public static boolean containCn(String str) {
		Pattern pattern = Pattern.compile("[\\u4e00-\\u9fa5]");
		return pattern.matcher(str).find();
	}
	
	/**
	 * 获取每个汉字的首字母
	 * @param str 包含多个汉字的字符串
	 * @return 这个汉字首字母大写的拼音，如：哈尔滨-->heb
	 */
	public static String getAllFirstPinyin(String str) {
		StringBuilder allFirstPinyinSb = new StringBuilder();
		int len = str.length();
		for (int i = 0; i < len; i++) {
			String firstChar = str.substring(i, i+1);// 哈
			String pinyin = toPinYin(firstChar, false);// ha
			String fChar = pinyin.substring(0, 1);
			allFirstPinyinSb.append(fChar);
		}
		Log.i("--获取每个汉字的首字母--:" + allFirstPinyinSb.toString());
		return allFirstPinyinSb.toString();
	}
	
	/**
	 * 得到格式如：FuJian的拼音
	 * 
	 * @param provinceName
	 * @return
	 */
	public static String getFormatPinyin(String name) {
		String pinyin = "";
		if (StringUtil.isEmpty(name)) {
			return pinyin;
		}
		int len = name.length();
		for (int i = 0; i < len; i++) {
			String singleChar = name.substring(i, i + 1);
			pinyin += getUpperPinyin(singleChar);
		}

		// 对一些多读音特殊情况的处理
		if (name.contains("重庆")) {
			pinyin = "ChongQing";
		} else if (name.contains("厦门")) {
			pinyin = "XiaMen";
		} else if (name.contains("长寿")) {
			pinyin = "ChangShou";
		}
		return pinyin;
	}

	/**
	 * 获取汉字首字母大写的拼音
	 * 
	 * @param str
	 *            单个汉字
	 * @return 这个汉字首字母大写的拼音，如：福-->Fu
	 */
	public static String getUpperPinyin(String str) {
		if (str.length() < 1) {
			return "";
		}
		String firstChar = str.substring(0, 1);// 福
		String pinyin = StringUtil.toPinYin(firstChar, false);// fu
		String oldFirstChar = pinyin.substring(0, 1);// f
		String newFirstChar = oldFirstChar.toUpperCase(Locale.getDefault());// F
		pinyin = newFirstChar + pinyin.substring(1);
		return pinyin;
	}
}
