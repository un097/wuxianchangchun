package cn.ffcs.wisdom.city.simico.kit.util;

import java.util.UUID;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class UrlUtils {

	public static String getNameFromUrl(String url) {
		if (url != null) {
			if (url.equalsIgnoreCase("")) {
				return UUID.randomUUID().toString();
			} else {
				return UUID.nameUUIDFromBytes(url.getBytes()).toString();
			}
		} else {
			return UUID.randomUUID().toString();
		}
	}

	public static String getUrlByUri(Context context, Uri uri) {
		String s = uri.getScheme();
		String url = null;
		if ("file".equals(s))
			url = uri.getPath();
		else if ("content".equals(s)) {
			Cursor cursor = null;
			try {
				cursor = context.getContentResolver().query(uri,
						new String[] { "_data" }, null, null, null);
				int data = cursor.getColumnIndexOrThrow("_data");
				cursor.moveToFirst();
				url = cursor.getString(data);
				// cursor.close();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (cursor != null) {
					cursor.close();
				}
			}
		} else {
			url = uri.toString();
		}
		return url;
	}
}
