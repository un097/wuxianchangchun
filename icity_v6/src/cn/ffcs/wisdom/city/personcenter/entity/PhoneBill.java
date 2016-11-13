package cn.ffcs.wisdom.city.personcenter.entity;

public class PhoneBill {
	/**
	 * 余额
	 */
	private double balance;
	/**
	 * 欠费
	 */
	private double arrearage;
	
	/**
     *详细话费url
	 */
	private String url;

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public double getArrearage() {
		return arrearage;
	}

	public void setArrearage(double arrearage) {
		this.arrearage = arrearage;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}


	
	

}
