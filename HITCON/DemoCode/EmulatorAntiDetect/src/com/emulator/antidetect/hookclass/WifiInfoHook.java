package com.emulator.antidetect.hookclass;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.BatteryManager;

import de.robv.android.xposed.XC_MethodHook.MethodHookParam;

public class WifiInfoHook extends MethodHook {
	private Methods mMethod;

	private static final String mClassName = "android.net.wifi.WifiInfo";

	private WifiInfoHook(Methods method) {
		super( mClassName, method.name());
		mMethod = method;
	}

	private enum Methods {
		getMacAddress
	};

	@SuppressLint("InlinedApi")
	public static List<MethodHook> getMethodHookList() {
		List<MethodHook> methodHookList = new ArrayList<MethodHook>();
		methodHookList.add(new WifiInfoHook(Methods.getMacAddress));

		return methodHookList;
	}

	@Override
	public void after(MethodHookParam param) throws Throwable {
		if (mMethod == Methods.getMacAddress) {
			String fakeMac = "00:26:37:17:3C:71";
			param.setResult(fakeMac);
		}
		
	}
}
