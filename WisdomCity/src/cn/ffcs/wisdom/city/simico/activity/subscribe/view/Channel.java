package cn.ffcs.wisdom.city.simico.activity.subscribe.view;

import cn.ffcs.wisdom.city.simico.kit.util.StringUtils;

public class Channel {

	public final int a;
	public final String b;
	public String c;
	public final String d;
	public final String e;
	public final int f;
	public final String g;
	public boolean hasIcon;
	public boolean i;
	public int j;
	public boolean k;
	public boolean isMy;
	public boolean m;

	public Channel(int i1, String s, String s1, String s2, String s3, int j1) {
		this(i1, s, s1, s2, s3, j1, null);
	}

	public Channel(int i1, String s, String s1, String s2, String s3, int j1,
			String s4) {
		hasIcon = false;
		i = false;
		j = -1;
		k = false;
		isMy = false;
		m = false;
		a = i1;
		b = s;
		c = s1;
		d = s2;
		e = s3;
		f = j1;
		g = s4;
	}

	public Channel(String s, String s1, String s2, String s3, int i1) {
		this(4, s, s1, s2, s3, i1);
	}

	public boolean a() {
		boolean flag = true;
		if (!StringUtils.isEmpty(b)) {
			switch (a) {
			case 2: // '\002'
			default:
				flag = false;
				break;

			case 5: // '\005'
				if (StringUtils.isEmpty(g))
					flag = false;
				break;

			case 1: // '\001'
			case 3: // '\003'
			case 4: // '\004'
				break;
			}
		} else {
			flag = false;
		}// goto _L2; else goto _L1
		return flag;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Channel))
			return false;
		return this.a == ((Channel) o).a;
	}
}
