package com.emulator.antidetect.hookclass;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.Binder;

import de.robv.android.xposed.XC_MethodHook.MethodHookParam;

public class IoBridgeHook extends MethodHook {
	private Methods mMethod;

	private static final String mClassName = "libcore.io.IoBridge";

	private IoBridgeHook(Methods method) {
		super( mClassName, method.name());
		mMethod = method;
	}

	private enum Methods {
		open
	};

	@SuppressLint("InlinedApi")
	public static List<MethodHook> getMethodHookList() {
		List<MethodHook> methodHookList = new ArrayList<MethodHook>();
		methodHookList.add(new IoBridgeHook(Methods.open));

		return methodHookList;
	}

	@Override
	public void before(MethodHookParam param) throws Throwable {
		int uid = Binder.getCallingUid();
		if(uid > 10000 && uid < 99999){
			if(mMethod == Methods.open)
				if(param.args.length >= 1 && param.args[0] != null){
					String path = (String) param.args[0];
					if(path.equals("/system/build.prop"))
						param.args[0] = "/data/local/tmp/fake-build.prop";
				}
		}
	}
}
