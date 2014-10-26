package com.emulator.antidetect;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import com.emulator.antidetect.hookclass.ActivityHook;
import com.emulator.antidetect.hookclass.ActivityManagerHook;
import com.emulator.antidetect.hookclass.BuildHook;
import com.emulator.antidetect.hookclass.ClassHook;
import com.emulator.antidetect.hookclass.ContextImplHook;
import com.emulator.antidetect.hookclass.FileHook;
import com.emulator.antidetect.hookclass.IntentHook;
import com.emulator.antidetect.hookclass.IoBridgeHook;
import com.emulator.antidetect.hookclass.MethodHook;
import com.emulator.antidetect.hookclass.ProcessHook;
import com.emulator.antidetect.hookclass.ReflectMethodHook;
import com.emulator.antidetect.hookclass.SystemPropertiesHook;
import com.emulator.antidetect.hookclass.TelephonyManagerHook;
import com.emulator.antidetect.hookclass.WifiInfoHook;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Process;
import android.util.Log;


import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.robv.android.xposed.XC_MethodHook;
import static de.robv.android.xposed.XposedHelpers.findClass;

// This class will be called by Xposed
@SuppressLint("DefaultLocale")
public class HookLauncher implements IXposedHookLoadPackage, IXposedHookZygoteInit {
	
	// System services
	private static boolean mTelephonyManagerHooked = false;
	private static boolean mLocationManagerHooked = false;

	
	// Call when zygote initialize
	public void initZygote(StartupParam startupParam) throws Throwable {
		hookNonSystemServices();

	}

	// Hook methods not provided by system services
	private void hookNonSystemServices(){
		// Activity
		hookAll(ActivityHook.getMethodHookList());
		
		// ContextImp
		hookAll(ContextImplHook.getMethodHookList());
				
		// Intent
		hookAll(IntentHook.getMethodHookList());
		
		// WifiInfo
		hookAll(WifiInfoHook.getMethodHookList());
		
		// SystemProperties
		hookAll(SystemPropertiesHook.getMethodHookList());
		
		// Method
		//hookAll(ReflectMethodHook.getMethodHookList());
				
		// IoBridge
		hookAll(IoBridgeHook.getMethodHookList());
				
		// ActivityManager
		hookAll(ActivityManagerHook.getMethodHookList());
		
		// File
		hookAll(FileHook.getMethodHookList());
		
		// Process
		hookAll(ProcessHook.getMethodHookList());
		
		// Build
		hookAll(BuildHook.getMethodHookList());
		
		// Class
		hookAll(ClassHook.getMethodHookList());
		
	}
	

	
	// Call when package loaded
	public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
	}



	public static void hookSystemServices(MethodHook methodHook, String serviceName, Object instance) {	
		if (serviceName.equals(Context.TELEPHONY_SERVICE)) {
			// Telephony manager
			if (!mTelephonyManagerHooked) {
				hookAll(TelephonyManagerHook.getMethodHookList(instance));
				mTelephonyManagerHooked = true;
			}
		}else if(serviceName.equals(Context.LOCATION_SERVICE)){
			if(!mLocationManagerHooked){
				
			}
		}
	}

	private static void hookAll(List<MethodHook> methodHookList) {
		for (MethodHook methodHook : methodHookList)
			hook(methodHook);
	}


	private static void hook(MethodHook methodHook) {
		hook(methodHook, null);
	}


	private static void hook(final MethodHook methodHook, ClassLoader classLoader) {
		try {
			
			// Create hook method
			XC_MethodHook xcMethodHook = new XC_MethodHook() {
				@Override
				protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
					try {
						if (Process.myUid() <= 0)
							return;
						methodHook.before(param);
					} catch (Throwable ex) {
						throw ex;
					}
				}

				@Override
				protected void afterHookedMethod(MethodHookParam param) throws Throwable {
					if (!param.hasThrowable())
						try {
							if (Process.myUid() <= 0)
								return;
							methodHook.after(param);
						} catch (Throwable ex) {
							throw ex;
						}
				}
			};
			
			// Find hook class
			Class<?> hookClass = findClass(methodHook.getClassName(), classLoader);
			if (hookClass == null) {
				String message = String.format("Hook-Class not found: %s", methodHook.getClassName());
				Log.i("SandBoxAnti", message);
				return;
			}

			// Add hook
			if (methodHook.getMethodName() == null) {
				for (Constructor<?> constructor : hookClass.getDeclaredConstructors()){
					XposedBridge.hookMethod(constructor, xcMethodHook);
				}
			} else{
				for (Method method : hookClass.getDeclaredMethods())
					if (method.getName().equals(methodHook.getMethodName()))
						XposedBridge.hookMethod(method, xcMethodHook);
			}
			
		} catch (Throwable ex) {
			
		}
	}
}
