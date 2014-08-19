package com.emulator.antidetect.hookclass;

import de.robv.android.xposed.XC_MethodHook.MethodHookParam;


/***
 * 
 * @author Wenjun Hu
 * 
 * Abstract base class of hooked method
 *
 ***/


public abstract class MethodHook {
	private String mClassName;
	private String mMethodName;
	
	protected MethodHook(String className, String methodName){
		mClassName = className;
		mMethodName = methodName;
	}
	
	
	public String getClassName(){
		return mClassName;
	}
	
	public String getMethodName(){
		return mMethodName;
	}

	
	public void before(MethodHookParam param) throws Throwable {
		// Do nothing
	}

	public void after(MethodHookParam param) throws Throwable {
		// Do nothing
	}

}
