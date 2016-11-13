//package cn.ffcs.wisdom.city.push;
//
//import java.util.ArrayList;
//
//import org.json.JSONObject;
//
//import cn.ffcs.wisdom.notify.MsgEntity;
//import cn.ffcs.wisdom.tools.JsonUtil;
//import cn.ffcs.wisdom.tools.Log;
//
//import com.ffcs.android.mc.MCBaseIntentService;
//
//public class MCIntentService extends MCBaseIntentService {
//
//	private PushMsgBo pushMsgBo;
//
//	@Override
//	public void handleMessage(ArrayList<JSONObject> messages) {
//		for (int i = 0; i < messages.size(); i++) {
//			JSONObject msgObj = messages.get(i);
//			try {
//				String msg = msgObj.getString("msg");// 消息内容
//				String idmMsgId = msgObj.getString("mid");// 爱动漫消息id--回执用
////				String report = msgObj.getString("rp");// 是否回执标志
//
//				Log.i("=============使用gson解析开始================");
//				Log.i("--收到消息msg--:" + msg);
//				MsgEntity entity = JsonUtil.toObject(msg, MsgEntity.class);
//				Log.i("--收到消息id--：" + entity.getContent().getMsgId());
//				Log.i("--收到消息titile--：" + entity.getTitle());
//				Log.i("--收到消息content--：" + entity.getContent().getMsgContent());
//				Log.i("--收到消息传递过来的参数-pkg--：" + entity.getContent().getParam().getAndroidPak());
//				Log.i("--收到消息传递过来的参数-cls--：" + entity.getContent().getParam().getAndroidClass());
//				entity.setIdmMsgId(idmMsgId);
//				Log.i("--爱动漫消息id--：" + entity.getIdmMsgId());
//				Log.i("=============使用gson解析结束=================");
//
//				if (pushMsgBo == null) {
//					pushMsgBo = new PushMsgBo(getApplicationContext());
//				}
//				pushMsgBo.showMsgInNoticeBar(entity);
//
//			} catch (Exception e) {
//				Log.e("--应用消息处理过程中解析消息失败！--" + e.getMessage());
//			}
//		}
//
//	}
//
//	@Override
//	public void handleSMSReceipt(String arg0, String arg1) {
//		Log.i("--arg0:" + arg0 + "--arg1:--" + arg1);
//	}
//
//
//}
