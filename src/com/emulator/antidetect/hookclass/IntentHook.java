package com.emulator.antidetect.hookclass;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.BatteryManager;

import de.robv.android.xposed.XC_MethodHook.MethodHookParam;

public class IntentHook extends MethodHook {
	private Methods mMethod;

	private static final String mClassName = "android.cotent.Intent";

	private IntentHook(Methods method) {
		super( mClassName, method.name());
		mMethod = method;
	}

	private enum Methods {
		getIntExtra
	};

	@SuppressLint("InlinedApi")
	public static List<MethodHook> getMethodHookList() {
		List<MethodHook> methodHookList = new ArrayList<MethodHook>();
		methodHookList.add(new IntentHook(Methods.getIntExtra));

		return methodHookList;
	}

	@Override
	public void after(MethodHookParam param) throws Throwable {
		if (mMethod == Methods.getIntExtra) {
			Intent intent = (Intent) param.thisObject;
			if(intent != null && intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)){
				String key = (String) param.args[0];
				if(key.equals(BatteryManager.EXTRA_LEVEL))
					param.setResult(78);
			}
		}
	}
}
