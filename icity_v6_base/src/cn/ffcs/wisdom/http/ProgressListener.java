package cn.ffcs.wisdom.http;

import cn.ffcs.wisdom.http.entity.Progress;

public interface ProgressListener<T extends Progress> {
	
	void onPreExecute(T entity);

	void onProgressUpdate(T entity);

	void onPostExecute(T entity);

}
