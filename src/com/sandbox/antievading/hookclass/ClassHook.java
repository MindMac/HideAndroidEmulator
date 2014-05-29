package com.sandbox.antievading.hookclass;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.Binder;

import de.robv.android.xposed.XC_MethodHook.MethodHookParam;

public class ClassHook extends MethodHook {
	private Methods mMethod;

	private static final String mClassName = "java.lang.Class";

	private ClassHook(Methods method) {
		super( mClassName, method.name());
		mMethod = method;
	}



	// @formatter:off

	// public Object getSystemService(String name)
	// frameworks/base/core/java/android/app/Activity.java

	// @formatter:on

	private enum Methods {
		forName
	};

	@SuppressLint("InlinedApi")
	public static List<MethodHook> getMethodHookList() {
		List<MethodHook> methodHookList = new ArrayList<MethodHook>();
		methodHookList.add(new ClassHook(Methods.forName));

		return methodHookList;
	}

	@Override
	public void after(MethodHookParam param) throws Throwable {
		if(param.args.length >= 1 && param.args[0] != null){
			String className = (String) param.args[0];
			if(className.equals("dalvik.system.Taint"))
				param.setThrowable(new ClassNotFoundException());
		}
	}
}
