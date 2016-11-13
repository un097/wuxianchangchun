package cn.ffcs.wisdom.city.simico.kit.util;

import android.os.Parcel;
import android.os.Parcelable;
import cn.ffcs.wisdom.city.simico.kit.log.TLog;

public class OnDownloadListener implements Parcelable {

	private int i;

	public OnDownloadListener() {
	}

	public OnDownloadListener(Parcel source) {
		TLog.log("OnDownloadListener readFromParcel");
		i = source.readInt();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		TLog.log("OnDownloadListener writeToParcel");
		dest.writeInt(i);
	}

	public void onError(String url) {
		
	}

	public void onDownload(String url, int readLen, long copyLen, long fileLen) {
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Parcelable.Creator<OnDownloadListener> CREATOR = new Parcelable.Creator<OnDownloadListener>() {

		@Override
		public OnDownloadListener createFromParcel(Parcel source) {
			return new OnDownloadListener(source);
		}

		@Override
		public OnDownloadListener[] newArray(int size) {
			return new OnDownloadListener[size];
		}
	};
}
