package cn.ffcs.wisdom.city.personcenter.bo;

import java.util.List;

import android.content.Context;
import cn.ffcs.wisdom.city.personcenter.entity.MyRelevanceEntity.MyRelevance.MyRelevanceGroup;
import cn.ffcs.wisdom.city.personcenter.entity.MyRelevanceEntity.MyRelevance.MyRelevanceGroup.MyRelevanceDetail;
import cn.ffcs.wisdom.city.personcenter.task.GetQueryRelevanceTask;
import cn.ffcs.wisdom.city.resp.QueryRelevanceResp;
import cn.ffcs.wisdom.city.sqlite.service.TrafficViolationsService;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.StringUtil;

public class QueryRelevanceBo {
	private List<MyRelevanceGroup> list;

	private Context mContext;

	public QueryRelevanceBo(Context context) {
		this.mContext = context;
	}

	public void startGetWzRelevance() {
		GetQueryRelevanceTask task = new GetQueryRelevanceTask(mContext,
				new GetWzRelevanceCallBack());
		task.execute("交通");
	}

	/**
	 * 获取违章关联回调
	 */
	class GetWzRelevanceCallBack implements HttpCallBack<QueryRelevanceResp> {

		@Override
		public void call(QueryRelevanceResp resp) {
			if (resp.isSuccess()) {
				list = resp.getList();
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							TrafficViolationsService service = TrafficViolationsService
									.getInstance(mContext);
							if (list != null) {
								for (int j = 0; j < list.size(); j++) {
									MyRelevanceGroup myRelevanceGroup = list.get(j);
//								WzCarInfo info = new WzCarInfo();
									String carNo = null;
									String carLastCodes = null;
									List<MyRelevanceDetail> detailList = myRelevanceGroup
											.getKeyList();
									for (int i = 0; i < detailList.size(); i++) {
										MyRelevanceDetail detail = detailList.get(i);
										String keyName = detail.getKeyName();
										if (keyName.equals("violCarNo")) {
											carNo = detail.getKeyValue();
										} else if (keyName.equals("violCarLastCodes")) {
											carLastCodes = detail.getKeyValue();
										}
//									else if (keyName.equals("violCarType")) {
//										info.setCarType(detail.getKeyValue());
//									}
									}
									if (!StringUtil.isEmpty(carNo)
											&& !StringUtil.isEmpty(carLastCodes)) {
										boolean isRelevlace = service.isExist(carNo, carLastCodes);
										if (!isRelevlace) {
											service.saveEmptyList(carNo, carLastCodes);
										}
									}

//								if (!isRelevlace) {
//									String carNo = info.getCarNo();
//									String last4Code = info.getCarLast4Code();
//									String type = info.getCarType();
//									if (!StringUtil.isEmpty(carNo)
//											&& !StringUtil.isEmpty(last4Code)
//											&& !StringUtil.isEmpty(type)) {
//										service.save(info);
//									}
//								} else {
//									service.updateRelevance(info);
//								}
								}
							}
						} catch (Exception e) {
							Log.e(e.getMessage(), e);
						}
					}
				}).start();
			}
		}

		@Override
		public void progress(Object... obj) {

		}

		@Override
		public void onNetWorkError() {
		}
	}

}
