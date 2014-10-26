package com.emulator.antidetect.hookclass;

import java.util.ArrayList;
import java.util.List;

import com.emulator.antidetect.HookLauncher;



import de.robv.android.xposed.XC_MethodHook.MethodHookParam;

public class ContextImplHook extends MethodHook {
	private Methods mMethod;
	private static final String mClassName = "android.app.ContextImpl";
	
	private ContextImplHook(Methods method) {
		super(mClassName, method.name());
		mMethod = method;
	}

	private enum Methods {
		getPackageManager, getSystemService
	};

	public static List<MethodHook> getMethodHookList() {
		List<MethodHook> methodHookList = new ArrayList<MethodHook>();
		methodHookList.add(new ContextImplHook(Methods.getPackageManager));
		methodHookList.add(new ContextImplHook(Methods.getSystemService));
		return methodHookList;
	}

	@Override
	public void after(MethodHookParam param) throws Throwable {
		if (mMethod == Methods.getPackageManager) {
			Object instance = param.getResult();
			if (instance != null)
				HookLauncher.hookSystemServices(this, "PackageManager", instance);
		} else if (mMethod == Methods.getSystemService) {
			if (param.args.length > 0 && param.args[0] != null) {
				String name = (String) param.args[0];
				Object instance = param.getResult();
				if (name != null && instance != null)
					HookLauncher.hookSystemServices(this, name, instance);
			}
		}
	}
}
