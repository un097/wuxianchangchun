package cn.ffcs.wisdom.tools;

import java.io.Closeable;

public final class StreamUtil {

	public static void closeSilently(Closeable c) {
		if (c == null)
			return;
		try {
			c.close();
		} catch (Throwable t) {
		}
	}
}
