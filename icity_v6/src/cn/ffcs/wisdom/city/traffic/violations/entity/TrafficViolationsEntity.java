package cn.ffcs.wisdom.city.traffic.violations.entity;

import java.io.Serializable;
import java.util.List;

/**
 * <p>Title:     违章请求实体     </p>
 * <p>Description:                     </p>
 * <p>@author: zhangwsh                </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-8-30           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class TrafficViolationsEntity {

	private List<TrafficViolationsInfo> CarWZInfo;

	@SuppressWarnings("serial")
	public class TrafficViolationsInfo implements Serializable{

//		private String WzAddress;
//
//		private String Kfz;
//
//		private String Wfxx;
//
//		private String Wztzdh;
//
//		private String WzTime;
//
//		private String Fkje;
//
//		public String getWzAddress() {
//			return WzAddress;
//		}
//
//		public void setWzAddress(String wzAddress) {
//			WzAddress = wzAddress;
//		}
//
//		public String getKfz() {
//			return Kfz;
//		}
//
//		public void setKfz(String kfz) {
//			Kfz = kfz;
//		}
//
//		public String getWfxx() {
//			return Wfxx;
//		}
//
//		public void setWfxx(String wfxx) {
//			Wfxx = wfxx;
//		}
//
//		public String getWztzdh() {
//			return Wztzdh;
//		}
//
//		public void setWztzdh(String wztzdh) {
//			Wztzdh = wztzdh;
//		}
//
//		public String getWzTime() {
//			return WzTime;
//		}
//
//		public void setWzTime(String wzTime) {
//			WzTime = wzTime;
//		}
//
//		public String getFkje() {
//			return Fkje;
//		}
//
//		public void setFkje(String fkje) {
//			Fkje = fkje;
//		}
		private int id;
		private String carNum;
		private String carLastNum;
		private String illegalTime;
		private String illegalAddr;
		private String illegalInfo;
		private float amount;
		private String score;
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public String getCarNum() {
			return carNum;
		}
		public void setCarNum(String carNum) {
			this.carNum = carNum;
		}
		public String getCarLastNum() {
			return carLastNum;
		}
		public void setCarLastNum(String carLastNum) {
			this.carLastNum = carLastNum;
		}
		public String getIllegalTime() {
			return illegalTime;
		}
		public void setIllegalTime(String illegalTime) {
			this.illegalTime = illegalTime;
		}
		public String getIllegalAddr() {
			return illegalAddr;
		}
		public void setIllegalAddr(String illegalAddr) {
			this.illegalAddr = illegalAddr;
		}
		public String getIllegalInfo() {
			return illegalInfo;
		}
		public void setIllegalInfo(String illegalInfo) {
			this.illegalInfo = illegalInfo;
		}
		public float getAmount() {
			return amount;
		}
		public void setAmount(float amount) {
			this.amount = amount;
		}
		public String getScore() {
			return score;
		}
		public void setScore(String score) {
			this.score = score;
		}
	}

	public List<TrafficViolationsInfo> getCarWZInfo() {
		return CarWZInfo;
	}

	public void setCarWZInfo(List<TrafficViolationsInfo> carWZInfo) {
		CarWZInfo = carWZInfo;
	}
}
