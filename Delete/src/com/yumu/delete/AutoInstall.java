package com.yumu.delete;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;

public class AutoInstall extends Activity {
	private Uri packageUri;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        packageUri = getPackageUri();
       
        install();
    }
    
    private void install()
    {
    	try {
    		Class<?> pmService;
    		Class<?> activityTherad;
    		Method method;
    		
    		activityTherad = Class.forName("android.app.ActivityThread");
    		Class<?> paramTypes[] = getParamTypes(activityTherad , "getPackageManager");
    		method = activityTherad.getMethod("getPackageManager", paramTypes);
    		Object PackageManagerService = method.invoke(activityTherad);
    		
    		pmService = PackageManagerService.getClass();
    		
    		Class<?> paramTypes1[] = getParamTypes(pmService , "installPackage");
    		method = pmService.getMethod("installPackage", paramTypes1);
    		method.invoke(PackageManagerService , packageUri , null , 0 , null);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
    }
    
    private Class<?>[] getParamTypes(Class<?> cls, String mName) {
		Class<?> cs[] = null;

		Method[] mtd = cls.getMethods();

		for (int i = 0; i < mtd.length; i++) {
			if (!mtd[i].getName().equals(mName)) {
				continue;
			}
			cs = mtd[i].getParameterTypes();
		}
		return cs;
	}
    
    private Uri getPackageUri()
    {
    	File file = new File("/storage/sdcard0/download/1.apk");
    	return Uri.fromFile(file);
    }
}

