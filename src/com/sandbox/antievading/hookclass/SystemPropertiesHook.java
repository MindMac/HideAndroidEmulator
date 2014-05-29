package com.sandbox.antievading.hookclass;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.Binder;

import de.robv.android.xposed.XC_MethodHook.MethodHookParam;

public class SystemPropertiesHook extends MethodHook {
	private Methods mMethod;

	private static final String mClassName = "android.os.SystemProperties";

	private SystemPropertiesHook(Methods method) {
		super( mClassName, method.name());
		mMethod = method;
	}



	// @formatter:off

	// public Object getSystemService(String name)
	// frameworks/base/core/java/android/app/Activity.java

	// @formatter:on

	private enum Methods {
		get
	};

	@SuppressLint("InlinedApi")
	public static List<MethodHook> getMethodHookList() {
		List<MethodHook> methodHookList = new ArrayList<MethodHook>();
		methodHookList.add(new SystemPropertiesHook(Methods.get));

		return methodHookList;
	}

	@Override
	public void after(MethodHookParam param) throws Throwable {
//		int uid = Binder.getCallingUid();
//		if(uid > 10000 && uid < 99999){
//			if(mMethod == Methods.get){
//				param.setResult("hello");
//			}
//		}
		if(mMethod == Methods.get){
			if(param.args.length >= 1 && param.args[0] != null){
				String key = (String) param.args[0];
				if(key.equals("ro.kernel.qemu")){
					param.setResult("0");
				}
			}
		}
	}
}
