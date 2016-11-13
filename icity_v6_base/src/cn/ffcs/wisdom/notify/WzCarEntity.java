package cn.ffcs.wisdom.notify;

import java.io.Serializable;
import java.util.List;

/**
 * <h3>违章查询实体类       </h3>
 * <p>违章查询实体类，临时用于违章推送，后期修改</p>
 */
public class WzCarEntity implements Serializable {
	private static final long serialVersionUID = -6392943753685777574L;
	private String status;
	private String desc;
	private String MSG;
	private String CarNo; // 车牌号码
	private String CarType; // 车辆类型
	private String CarWZInfoCount; // 车架后4位
	private List<WzCarItem> CarWZInfo; // 违章列表
	private boolean isExpandMore; // 是否展开更多栏目

	public class WzCarItem implements Serializable {
		private static final long serialVersionUID = 7942630795302686075L;
		private String Wztzdh; // 违章编号
		private String Wfdm; // 违章代码
		private String WzTime; // 违章时间
		private String WzAddress; // 违章地址
		private String Fkje; // 罚款金额
		private String Kfz; // 扣分
		private String Cf;
		private String Jz;
		private String Wfxx; // 违章描述
		private String Zacf;
		private String CL;

		public String getWztzdh() {
			return Wztzdh;
		}

		public void setWztzdh(String wztzdh) {
			Wztzdh = wztzdh;
		}

		public String getWfdm() {
			return Wfdm;
		}

		public void setWfdm(String wfdm) {
			Wfdm = wfdm;
		}

		public String getWzTime() {
			return WzTime;
		}

		public void setWzTime(String wzTime) {
			WzTime = wzTime;
		}

		public String getWzAddress() {
			return WzAddress;
		}

		public void setWzAddress(String wzAddress) {
			WzAddress = wzAddress;
		}

		public String getFkje() {
			return Fkje;
		}

		public void setFkje(String fkje) {
			Fkje = fkje;
		}

		public String getKfz() {
			return Kfz;
		}

		public void setKfz(String kfz) {
			Kfz = kfz;
		}

		public String getCf() {
			return Cf;
		}

		public void setCf(String cf) {
			Cf = cf;
		}

		public String getJz() {
			return Jz;
		}

		public void setJz(String jz) {
			Jz = jz;
		}

		public String getWfxx() {
			return Wfxx;
		}

		public void setWfxx(String wfxx) {
			Wfxx = wfxx;
		}

		public String getZacf() {
			return Zacf;
		}

		public void setZacf(String zacf) {
			Zacf = zacf;
		}

		public String getCL() {
			return CL;
		}

		public void setCL(String cL) {
			CL = cL;
		}
	}

	public String getMSG() {
		return MSG;
	}

	public void setMSG(String mSG) {
		MSG = mSG;
	}

	public String getCarNo() {
		return CarNo;
	}

	public void setCarNo(String carNo) {
		CarNo = carNo;
	}

	public String getCarType() {
		return CarType;
	}

	public void setCarType(String carType) {
		CarType = carType;
	}

	public String getCarWZInfoCount() {
		return CarWZInfoCount;
	}

	public void setCarWZInfoCount(String carWZInfoCount) {
		CarWZInfoCount = carWZInfoCount;
	}

	public List<WzCarItem> getCarWZInfo() {
		return CarWZInfo;
	}

	public void setCarWZInfo(List<WzCarItem> carWZInfo) {
		CarWZInfo = carWZInfo;
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

	public boolean isExpandMore() {
		return isExpandMore;
	}

	public void setExpandMore(boolean isExpandMore) {
		this.isExpandMore = isExpandMore;
	}

}
