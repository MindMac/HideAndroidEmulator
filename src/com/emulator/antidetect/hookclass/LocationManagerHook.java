package com.emulator.antidetect.hookclass;

import java.util.ArrayList;
import java.util.List;


import android.Manifest;
import android.location.Location;
import android.location.LocationManager;


import de.robv.android.xposed.XC_MethodHook.MethodHookParam;

public class LocationManagerHook extends MethodHook {
	private Methods mMethod = null;
	
	private LocationManagerHook(String className, Methods method) {
		super(className, method.name());
		mMethod = method;
	}


	private enum Methods {
		getLastKnownLocation
	};

	public static List<MethodHook> getMethodHookList(Object instance) {
		String className = instance.getClass().getName();

		List<MethodHook> methodHookList = new ArrayList<MethodHook>();
		
		methodHookList.add(new LocationManagerHook(className, Methods.getLastKnownLocation));

		return methodHookList;
	}
	

	@Override
	public void after(MethodHookParam param) throws Throwable {
		if (mMethod == Methods.getLastKnownLocation) {
			Location location = new Location(LocationManager.GPS_PROVIDER);
			location.setLatitude(10.03);
			location.setLongitude(25.05);
			param.setResult(location);
		}
	}
}
