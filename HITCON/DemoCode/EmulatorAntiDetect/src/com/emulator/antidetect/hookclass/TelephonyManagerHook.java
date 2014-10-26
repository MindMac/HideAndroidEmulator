package com.emulator.antidetect.hookclass;

import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook.MethodHookParam;

public class TelephonyManagerHook extends MethodHook {
	private Methods mMethod = null;
	
	private TelephonyManagerHook(String className, Methods method) {
		super(className, method.name());
		mMethod = method;
	}


	private enum Methods {
		getDeviceId, getLine1Number, getNetworkOperatorName, getSimSerialNumber
	};

	public static List<MethodHook> getMethodHookList(Object instance) {
		String className = instance.getClass().getName();

		List<MethodHook> methodHookList = new ArrayList<MethodHook>();
		
		for(Methods method : Methods.values())
			methodHookList.add(new TelephonyManagerHook(className, method));

		return methodHookList;
	}
	
	@Override
	public void after(MethodHookParam param) throws Throwable {
		if(mMethod == Methods.getDeviceId){
			param.setResult("098767899076561");
		}else if(mMethod == Methods.getLine1Number)
			param.setResult("15802920458");
		else if(mMethod == Methods.getNetworkOperatorName)
			param.setResult("CMCC");
		else if(mMethod == Methods.getSimSerialNumber)
			param.setResult("89014103211118510799");

	}

}
