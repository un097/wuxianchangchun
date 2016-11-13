package cn.ffcs.wisdom.http.entity;

/**
 * <p>Title: WisdomCity</p>
 * <p>Description: 下载对象</p>
 * <p>Author: tianya</p>
 * <p>CreateTime: 2012-4-23 下午01:37:27 </p>
 * <p>CopyRight: 4.0.2 </p>
 */
public class Progress {

	private String fileName; // 文件名
	private String fileDir; // 文件夹地址
	private String key;

	private int total; // 文件大小
	private int completeSize; // 已下载

	public Progress() {
	}

	public Progress(String fileName, String fileDir) {
		this.fileName = fileName;
		this.fileDir = fileDir;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileDir() {
		return fileDir;
	}

	public void setFileDir(String fileDir) {
		this.fileDir = fileDir;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getCompleteSize() {
		return completeSize;
	}

	public void setCompleteSize(int completeSize) {
		this.completeSize = completeSize;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

}
