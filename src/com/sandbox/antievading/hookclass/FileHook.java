package com.sandbox.antievading.hookclass;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.Binder;

import de.robv.android.xposed.XC_MethodHook.MethodHookParam;

public class FileHook extends MethodHook {
	private Methods mMethod;

	private static final String mClassName = "java.io.File";

	private FileHook(Methods method) {
		super( mClassName, method.name());
		mMethod = method;
	}



	// @formatter:off

	// public Object getSystemService(String name)
	// frameworks/base/core/java/android/app/Activity.java

	// @formatter:on

	private enum Methods {
		exists
	};

	@SuppressLint("InlinedApi")
	public static List<MethodHook> getMethodHookList() {
		List<MethodHook> methodHookList = new ArrayList<MethodHook>();
		methodHookList.add(new FileHook(Methods.exists));

		return methodHookList;
	}

	@Override
	public void after(MethodHookParam param) throws Throwable {
		if(mMethod == Methods.exists){
			System.out.println("Method exists");
			File file = (File) param.thisObject;
			String filePath = file.getAbsolutePath();
			System.out.println("File Path:" + filePath);
			if(filePath.equals("/dev/qemu_pipe"))
				param.setResult(false);
		}
	}
}
