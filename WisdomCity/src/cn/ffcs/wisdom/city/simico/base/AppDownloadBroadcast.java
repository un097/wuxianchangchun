package cn.ffcs.wisdom.city.simico.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AppDownloadBroadcast extends BroadcastReceiver {

	public interface UpdateUIView {
		public void updateProgress(int v, long t);

		public void updateFinish();

		public void updateFail();
	}

	private UpdateUIView mUpdate;

	public AppDownloadBroadcast(UpdateUIView update) {
		this.mUpdate = update;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (!Constants.INTENT_ACTION_DOWNLOAD_APP.equals(intent.getAction()))
			return;
		int statu = intent.getIntExtra("statu", -1);
		switch (statu) {
		case AppDownloadService.UPDATE_PROGRESS:
			mUpdate.updateProgress(intent.getIntExtra("V", 0),
					intent.getLongExtra("T", 0));
			break;
		case AppDownloadService.DOWNLOAD_SUCCESS:
			mUpdate.updateFinish();
			break;
		case AppDownloadService.DOWNLOAD_FAIL:
			mUpdate.updateFail();
			break;
		}
	}
}
