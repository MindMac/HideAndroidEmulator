package com.emulator.antidetect.hookclass;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;

import de.robv.android.xposed.XC_MethodHook.MethodHookParam;

public class ClassHook extends MethodHook {
	private Methods mMethod;

	private static final String mClassName = "java.lang.Class";

	private ClassHook(Methods method) {
		super( mClassName, method.name());
		mMethod = method;
	}

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
