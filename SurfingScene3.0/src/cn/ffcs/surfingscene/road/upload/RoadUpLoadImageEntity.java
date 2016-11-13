package cn.ffcs.surfingscene.road.upload;

import java.util.List;

public class RoadUpLoadImageEntity {
	public String status;
	public String desc;
	public List<UpLoadImage> relist;

	public class UpLoadImage {
		public String shareWapUrl;
		public String imageUrl;
	}
}
