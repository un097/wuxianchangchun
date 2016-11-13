package cn.ffcs.wisdom.tools;

import java.text.DecimalFormat;

public class MathUtil {

	public static DecimalFormat percentFormat = new DecimalFormat("##%");

	public static String percent(int max, int min) {
		String format = "0%";// 接受百分比的值

		double dmax = max * 1.0;
		double dmin = min * 1.0;
		if (dmax == 0) {
			return format;
		}
		double fen = dmin / dmax;
		format = percentFormat.format(fen);
		return format;
	}

	public static int byte2kb(int bytes) {
		if (bytes >= 1024) {
			return bytes / 1024;
		}
		return bytes;
	}

	public static int byte2mb(int bytes) {
		if (bytes >= 1024) {
			return bytes / 1024;
		}
		return bytes;
	}
}
