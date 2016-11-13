package cn.ffcs.wisdom.http;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import cn.ffcs.wisdom.http.entity.BitmapProgress;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.StreamUtil;

/**
 * 异步线程，图片下载
 * 所有线程处理情况，均通过监听返回。
 * 
 * <p>构造方法：ImageGetter(String fileDir, String fileName) <br/>
 * 1、fileDir 文件路径
 * 2、fileName 文件名
 * </p>
 * 
 * <p>ProgressListener<BitmapProgress> listener 图片下载进度监听
 * 1、设置监听方法 setProgressListener(ProgressListener<BitmapProgress> l)
 * 
 * 2、onPostExecute(Bitmap result) 线程任务结束，调用该方法返回结果
 * </p>
 * @author  caijj
 * @version 1.00, 2012-4-6
 * @version 1.10, 2012-12-20
 */
public class ImageGetter extends ImageAsync<String, Integer, Bitmap> {

	private HttpURLConnection conn;
	private ProgressListener<BitmapProgress> listener;
	private BitmapProgress progress;

	private File mFile;

	public ImageGetter() {
		progress = new BitmapProgress();
	}
	
	public ImageGetter(String fileDir, String fileName) {
		this();
		init(fileName, fileDir);
	}

	private void init(String fileName, String fileDir) {
		File dir = new File(fileDir);

		if (!dir.exists()) {
			dir.mkdirs();
		}

		mFile = new File(fileDir, fileName);
	}

	@Override
	protected final Bitmap doInBackground(String... params) {

		if (params == null || params.length <= 0) {
			Log.e("url is null, download exit.");
			return null;
		}

		Bitmap bitmap = null;
		FileOutputStream f = null;

		String url = params[0];
		Log.d("url:" + url);
		try {
			URL u = new URL(url);
			conn = ((HttpURLConnection) u.openConnection());
			conn.setConnectTimeout(2000);
			// conn.setRequestMethod("GET");
			// conn.setDoOutput(true);
			// conn.setUseCaches(true);
			conn.connect();
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK)
				return null;

			InputStream in = conn.getInputStream();

			f = createOutputStream(mFile);
			if (f != null) {
				
				int total = conn.getContentLength();
				int fix = 0;

				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = in.read(buffer)) > 0) {
					f.write(buffer, 0, len);
					fix += len;
					publishProgress(total, fix);
				}

				bitmap = BitmapFactory.decodeFile(mFile.getAbsolutePath());
			} else {
				bitmap = BitmapFactory.decodeStream(in);
			}
		} catch (MalformedURLException e) {
			Log.e(e.getMessage(), e);
			deleteFile();
		} catch (IOException e) {
			Log.e(e.getMessage(), e);
			deleteFile();
		} finally {
			// Closes this connection.
			if (conn != null)
				conn.disconnect();
			if (f != null)
				StreamUtil.closeSilently(f);

			deleteEmptyFile();
		}
		return bitmap;
	}

	@Override
	protected final void onPostExecute(Bitmap result) {
		if (listener != null) {
			progress.setBitmap(result);
			listener.onPostExecute(progress);
		}
	}

	private FileOutputStream createOutputStream(File file) {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			Log.e(e.getMessage() + ", file absolutePath:" + file.getAbsolutePath(), e);
			// e.printStackTrace();
		}
		return out;
	}

	private void deleteFile() {
		if (mFile != null && mFile.exists()) {
			mFile.delete();
		}
	}

	private void deleteEmptyFile() {
		if (mFile != null && mFile.length() == 0) {
			mFile.delete();
		}
	}

	public void setProgressListener(ProgressListener<BitmapProgress> l) {
		listener = l;
	}
}
