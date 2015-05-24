package com.yumu.delete;



import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import cn.trinea.android.common.util.PackageUtils;

import android.app.ActivityThread;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.RecoverySystem;
import android.util.Log;

/**
 * 删除非默认的应用
 * @author 谢志华
 * 
 *
 */
public class DeleteApp {
	private final static String TAG = "DeleteApp1";
	/**
	 * 通过指定的默认的应用程序包名，卸载所有非默认程序的数据
	 * @param context -应用程序运行的上下文环境
	 * @param defaultPkgName 默认的应用程序包名数组
	 */
	public void deleteApp(Context context ,String[] defaultPkgName){
		List<String>str = new ArrayList<String>();
	    for(String defaultName:defaultPkgName){
	    	str.add(defaultName);
	    }
		Log.i(TAG,"start delete the no defualt app.....");
		PackageManager pkgMng = context.getPackageManager();
		List<String>nonDeaultPackageName = new ArrayList<String>();
		List<ApplicationInfo> pkgInfos = pkgMng.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
		//查找到非默认的安装程序
		for(ApplicationInfo info:pkgInfos){
			System.out.println("application package name= "+info.packageName);
			if(false == str.contains(info.packageName)){
				nonDeaultPackageName.add(info.packageName);
				//excuteSuCMD("pm uninstall "+info.packageName);
				PackageUtils.uninstallSilent(context, info.packageName);
			}
		}
		
		
		
	}
	
	private  int excuteSuCMD(String cmd) {
		try {
				Process process = Runtime.getRuntime().exec("su");
				DataOutputStream dos = new DataOutputStream(process.getOutputStream());
				// 部分手机Root之后Library path 丢失，导入library path可解决该问题
				dos.writeBytes((String) "export LD_LIBRARY_PATH=/vendor/lib:/system/lib");
				cmd = String.valueOf(cmd);
				dos.writeBytes((String) (cmd + "\n"));
				dos.flush();
				dos.writeBytes("exit\n");
				dos.flush();
				process.waitFor();
				int result = process.exitValue();
				return (Integer) result;
		} catch (Exception localException) {
				localException.printStackTrace();
		    return -1;
		  }
		}
	/**
	 * 根据给定的APP包名列表，卸载所有的非默认的APP应用
	 * @param context 程序运行的上下文环境
	 * @param defalutApp 默认的App包名列表
	 */
	public void deleteApp(Context context,List<String> defalutApp){
		String []defalutPkgName = new String[defalutApp.size()];
		int index = 0;
		for(String packageName:defalutApp){
			defalutPkgName[index] = packageName;
		}
		deleteApp(context, defalutPkgName);
	}
	
	 private void uninstall(String packageName){
		    try {
				Class<?> pmService;
				Class<?> activityTherad;
				Method method;
				activityTherad = Class.forName("android.app.ActivityThread");
				Class<?> paramTypes[] = getParamTypes(activityTherad,
						"getPackageManager");
				method = activityTherad.getMethod("getPackageManager", paramTypes);
				Object PackageManagerService = method.invoke(activityTherad);
				pmService = PackageManagerService.getClass();
				Class<?> paramTypes1[] = getParamTypes(pmService, "deletePackage");
				method = pmService.getMethod("deletePackage", paramTypes1);
				method.invoke(PackageManagerService, packageName, null, 0);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	    }
	    private Class<?>[] getParamTypes(Class<?>cls,String name){
	    	Class<?>cs[] = null;
	    	Method[]mtds = cls.getMethods();
	       for(Method mtd:mtds){
	    	   if(!mtd.getName().equals(name)){
	    		   continue;
	    	   }
	    	   cs = mtd.getParameterTypes();
	       }
	       return cs;
	    }
	
}
