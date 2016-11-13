package cn.ffcs.wisdom.http;

public class BaseResp implements Response {

	public static final String SUCCESS = "0";
	public static final String ERROR = "-1";
	public static final String NETWORK_ERROR = "-2";

	private String status;
	private String desc;
	private String result;
	private Object obj;	//Json转换后的对象
	private String detailDesc;
	private String data;
	private String timestamp;	//时间戳

	public BaseResp() {
		this.status = SUCCESS;
	}

	public BaseResp(String error) {
		this.status = error;
	}

	@Override
	public String getHttpResult() {
		if(result == null) {
			return "";
		}
		return result;
	}

	@Override
	public boolean isSuccess() {
		return SUCCESS.equals(status);
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	public String getDetailDesc() {
		return detailDesc;
	}

	public void setDetailDesc(String detailDesc) {
		this.detailDesc = detailDesc;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public Object getObj() {
		return obj;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

}
