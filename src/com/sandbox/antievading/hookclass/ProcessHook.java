package com.sandbox.antievading.hookclass;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.Binder;

import de.robv.android.xposed.XC_MethodHook.MethodHookParam;

public class ProcessHook extends MethodHook {
	private Methods mMethod;

	private static final String mClassName = "java.lang.ProcessManager$ProcessImpl";

	private ProcessHook(Methods method) {
		super( mClassName, method.name());
		mMethod = method;
	}



	// @formatter:off

	// public Object getSystemService(String name)
	// frameworks/base/core/java/android/app/Activity.java

	// @formatter:on

	private enum Methods {
		getInputStream
	};

	@SuppressLint("InlinedApi")
	public static List<MethodHook> getMethodHookList() {
		List<MethodHook> methodHookList = new ArrayList<MethodHook>();
		methodHookList.add(new ProcessHook(Methods.getInputStream));

		return methodHookList;
	}

	@Override
	public void after(MethodHookParam param) throws Throwable {
		int uid = Binder.getCallingUid();
		if(uid > 10000 && uid < 99999){
			if(mMethod == Methods.getInputStream){
				InputStream inputStream = new FileInputStream("/system/fake-build.prop");
				param.setResult(inputStream);
			}
		}
	}
}
