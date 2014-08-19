package com.emulator.antidetect.hookclass;

import java.util.ArrayList;
import java.util.List;

import com.emulator.antidetect.HookLauncher;

import android.annotation.SuppressLint;

import de.robv.android.xposed.XC_MethodHook.MethodHookParam;

public class ActivityManagerHook extends MethodHook {
	private Methods mMethod;

	private static final String mClassName = "android.app.ActivityManager";

	private ActivityManagerHook(Methods method) {
		super( mClassName, method.name());
		mMethod = method;
	}

	private enum Methods {
		isUserAMonkey
	};

	public static List<MethodHook> getMethodHookList() {
		List<MethodHook> methodHookList = new ArrayList<MethodHook>();
		methodHookList.add(new ActivityManagerHook(Methods.isUserAMonkey));

		return methodHookList;
	}

	@Override
	public void after(MethodHookParam param) throws Throwable {
		if (mMethod == Methods.isUserAMonkey) 
			param.setResult(false);
	}
}
