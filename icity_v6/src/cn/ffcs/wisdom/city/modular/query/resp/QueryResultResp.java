package cn.ffcs.wisdom.city.modular.query.resp;

import cn.ffcs.wisdom.http.BaseResp;

public class QueryResultResp extends BaseResp {

	private String resultCode;

	private String url;

	@Override
	public boolean isSuccess() {
		return SUCCESS.equals(resultCode);
	}

	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
