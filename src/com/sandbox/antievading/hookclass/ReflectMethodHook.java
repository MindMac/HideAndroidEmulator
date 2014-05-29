package com.sandbox.antievading.hookclass;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.Binder;

import de.robv.android.xposed.XC_MethodHook.MethodHookParam;

public class ReflectMethodHook extends MethodHook {
	private Methods mMethod;

	private static final String mClassName = "java.lang.reflect.Method";

	private ReflectMethodHook(Methods method) {
		super( mClassName, method.name());
		mMethod = method;
	}



	// @formatter:off

	// public Object getSystemService(String name)
	// frameworks/base/core/java/android/app/Activity.java

	// @formatter:on

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
				System.out.println("class:" + className + " method:" + methodName);
				if(targetClass.getName().equals("android.os.SystemProperties") &&
					methodName.equals("get")){
						String key = (String) ((Object[])param.args[1])[0];
						if(key != null && key.equals("ro.kernel.qemu"))
							param.setResult("0");
					}
			}
		}
	}
}
