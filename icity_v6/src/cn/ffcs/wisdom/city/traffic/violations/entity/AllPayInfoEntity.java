package cn.ffcs.wisdom.city.traffic.violations.entity;

public class AllPayInfoEntity {
	public String car_no;
	public String car_last_code;
	public int violate_count;// 违章次数
	public String violate_marking;// 违章扣分
	public double penalty_amount;// 处罚金额
	public double total_count;// 总额（处罚金额+代办费）
	public double agency_fees;// 总代办费
}
