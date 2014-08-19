package com.emulator.antidetect.hookclass;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;

import de.robv.android.xposed.XC_MethodHook.MethodHookParam;

public class ReflectMethodHook extends MethodHook {
	private Methods mMethod;

	private static final String mClassName = "java.lang.reflect.Method";

	private ReflectMethodHook(Methods method) {
		super( mClassName, method.name());
		mMethod = method;
	}

	private enum Methods {
		invoke
	};

	@SuppressLint("InlinedApi")
	public static List<MethodHook> getMethodHookList() {
		List<MethodHook> methodHookList = new ArrayList<MethodHook>();
		methodHookList.add(new ReflectMethodHook(Methods.invoke));

		return methodHookList;
	}

	@Override
	public void after(MethodHookParam param) throws Throwable {
		if(mMethod == Methods.invoke){
			if(param.args.length >= 1 && param.args[0] != null){
				Class<?> targetClass = (Class<?>) param.args[0];
				String methodName = ((Method)param.thisObject).getName();
				String className = targetClass.getName();
				if(className.equals("android.os.SystemProperties") &&
					methodName.equals("get")){
						String key = (String) ((Object[])param.args[1])[0];
						if(key != null && key.equals("ro.product.name"))
							param.setResult("google");
					}
			}
		}
	}
}
