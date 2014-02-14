package com.han5i5j.copyviewdemo;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

	private LinearLayout root;
	private TextView textView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		root = (LinearLayout) findViewById(R.id.root);
		textView = (TextView) findViewById(R.id.txt);

		View copyView = copyView(textView);
		if (copyView != null) {
			LayoutParams layoutParams = new ViewGroup.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			root.addView(copyView, layoutParams);
		}
	}

	private View copyView(View sourceView) {
		try {
			Class<? extends View> sourceClass = sourceView.getClass();

			Constructor<? extends View> constructor = sourceClass
					.getConstructor(Context.class);
			View copyView = constructor.newInstance(MainActivity.this);

			/*
			 * Field[] declaredFields = sourceClass.getDeclaredFields(); for
			 * (int i = 0; i < declaredFields.length; i++) { Field field =
			 * declaredFields[i]; field.setAccessible(true); Object sourceObj =
			 * field.get(sourceView); field.set(copyView, sourceObj); }
			 */

			copyField(sourceClass, sourceView, copyView);

			return copyView;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	private void copyField(Class<?> sourceClass, View sourceView, View copyView) {
		
		
		Field[] declaredFields = sourceClass.getDeclaredFields();
		for (int i = 0; i < declaredFields.length; i++) {
			try {
				Field field = declaredFields[i];
				if ("mParent".equals(field.getName()) && "android.view.View".equals(sourceClass.getName())) {
					continue;
				}
				field.setAccessible(true);
				Object sourceObj = field.get(sourceView);
				
				if (sourceObj instanceof Drawable) {
					sourceObj = ((Drawable)sourceObj).mutate();
				}
				
				field.set(copyView, sourceObj);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		Class<?> superclass = sourceClass.getSuperclass();
		if (!"android.view.View".equals(sourceClass.getName())
				&& superclass != null) {
			copyField(superclass, sourceView, copyView);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
