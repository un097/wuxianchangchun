package cn.ffcs.wisdom.city.modular.query;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import android.content.Context;
import android.view.View;
import cn.ffcs.wisdom.city.modular.query.emuns.ViewType;

public class ViewFactory {

	@SuppressWarnings("rawtypes")
	public static View createView(Context context, ViewType type) {
		View view = null;
		try {

			Class<?> clazz = type.getClazz();
			Constructor constructor = clazz.getConstructor(Context.class);
			view = (View) constructor.newInstance(context);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return view;
	}

}
