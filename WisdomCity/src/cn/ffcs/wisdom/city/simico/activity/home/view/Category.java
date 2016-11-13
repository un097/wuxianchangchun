package cn.ffcs.wisdom.city.simico.activity.home.view;


//import com.ss.android.common.i.az;

public class Category {

	public final int a;
	public final String mValue;
	public String mName;
	public final String d;
	public final String e;
	public final int f;
	public final String g;
	public boolean h;
	public boolean i;
	public int j;
	public boolean k;
	public boolean l;
	public boolean m;

	public Category(int i1, String value, String name, String s2, String s3, int j1) {
		this(i1, value, name, s2, s3, j1, null);
	}

	public Category(int i1, String value, String name, String s2, String s3, int j1,
			String s4) {
		h = false;
		i = false;
		j = -1;
		k = false;
		l = false;
		m = false;
		a = i1;
		mValue = value;
		mName = name;
		d = s2;
		e = s3;
		f = j1;
		g = s4;
	}

	public Category(String value, String name, String s2, String s3, int i1) {
		this(4, value, name, s2, s3, i1);
	}

	public boolean a() {
		// boolean flag = true;
		// if(!az.a(b)) goto _L2; else goto _L1
		// _L1:
		// flag = false;
		// _L4:
		// return flag;
		// _L2:
		// switch(a)
		// {
		// case 2: // '\002'
		// default:
		// flag = false;
		// break;
		//
		// case 5: // '\005'
		// if(az.a(g))
		// flag = false;
		// break;
		//
		// case 1: // '\001'
		// case 3: // '\003'
		// case 4: // '\004'
		// break;
		// }
		// if(true) goto _L4; else goto _L3
		// _L3:
		return false;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Category))
			return false;
		return this.a == ((Category) o).a;
	}
}
