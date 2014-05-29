package com.sandbox.antievading;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import com.sandbox.antievading.hookclass.ActivityHook;
import com.sandbox.antievading.hookclass.BuildHook;
import com.sandbox.antievading.hookclass.ClassHook;
import com.sandbox.antievading.hookclass.ContextImplHook;
import com.sandbox.antievading.hookclass.FileHook;
import com.sandbox.antievading.hookclass.IoBridgeHook;
import com.sandbox.antievading.hookclass.MethodHook;
import com.sandbox.antievading.hookclass.ProcessHook;
import com.sandbox.antievading.hookclass.ReflectMethodHook;
import com.sandbox.antievading.hookclass.SystemPropertiesHook;
import com.sandbox.antievading.hookclass.TelephonyManagerHook;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
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
		
		// SystemProperties
		//hookAll(SystemPropertiesHook.getMethodHookList());
		
		// Process
		hookAll(ProcessHook.getMethodHookList());
		
		// Build
		hookAll(BuildHook.getMethodHookList());
		
		// Method
		hookAll(ReflectMethodHook.getMethodHookList());
		
		// IoBridge
		hookAll(IoBridgeHook.getMethodHookList());
		
		// Class
		hookAll(ClassHook.getMethodHookList());
		
		// File
		hookAll(FileHook.getMethodHookList());
		
	}
	
	
	private void changeFieldModifier(){
		Build build = new Build();
		try {
			Field device = Build.class.getDeclaredField("DEVICE");
			
			Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField.setInt(device, device.getModifiers() & ~Modifier.FINAL);
			
			
			device.setAccessible(true);
			device.set(build, "google");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
		}
	}

	private static void hookAll(List<MethodHook> methodHookList) {
		for (MethodHook methodHook : methodHookList)
			hook(methodHook);
	}

	private static void hookAll(List<MethodHook> methodHookList, ClassLoader classLoader) {
		for (MethodHook methodHook : methodHookList)
			hook(methodHook, classLoader);
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
