package com.sandbox.antievading.hookclass;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.Binder;

import de.robv.android.xposed.XC_MethodHook.MethodHookParam;

public class BuildHook extends MethodHook {
	private Methods mMethod;

	private static final String mClassName = "android.os.Build";

	private BuildHook(Methods method) {
		super( mClassName, method.name());
		mMethod = method;
	}



	// @formatter:off

	// public Object getSystemService(String name)
	// frameworks/base/core/java/android/app/Activity.java

	// @formatter:on

	private enum Methods {
		getString
	};

	@SuppressLint("InlinedApi")
	public static List<MethodHook> getMethodHookList() {
		List<MethodHook> methodHookList = new ArrayList<MethodHook>();
		methodHookList.add(new BuildHook(Methods.getString));

		return methodHookList;
	}

	@Override
	public void after(MethodHookParam param) throws Throwable {
		if(mMethod == Methods.getString){
			if(param.args.length >= 1 && param.args[0] != null){
				String key = (String) param.args[0];
				if(key.equals("ro.product.brand"))
					param.setResult("google");
			}
		}
	}
}
