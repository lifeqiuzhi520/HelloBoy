package com.yumu.delete;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.provider.OpenableColumns;
import android.util.Log;

/**
 *  删除SD卡中的非默认的文件
 * @author 谢志华
 *
 */
public class DeleteDir {
	private static String TAG ="DeleteDir";
	 private static String BACK_UP_FILE = "YuMuBackUp";//对SD卡进行备份时在sd卡的根目录中的文件名
	 private static String RESTORE_FILE = BACK_UP_FILE;//当对SD卡进行还原时，源数据的文件夹名字
	/**;
	 * 删除SD卡中的非默认文件夹
	 * @param SDdefaultFolder存放在SD卡上的默认文件名数组
	 * @return true删除成功，false删除失败
	 * 
	 */
	public boolean deleteDir(String[] SDdefaultFolder){
		List<String > defaultFile = new ArrayList<String>();
		for(String file:SDdefaultFolder){
			defaultFile.add(file);
		}
		return deleteDir(defaultFile);
	}
	/**
	 * 删除SD卡中的非默认及其子文件夹
	 * @param SDdefaultFolder存放在SD卡上的默认文件夹列表
	 * @return true删除成功，false删除失败
	 * 
	 */
	public  boolean deleteDir(List<String>SDdefaultFolder) {
		Log.i(TAG, "start delete the SD card directory");
		
		//找到外部存储器中的非默认文件夹
		List<String>noDefaultFile = findNoDefaultFile(SDdefaultFolder);
		if(noDefaultFile == null)
		{
			return false;
		}
		File externFile = Environment.getExternalStorageDirectory();
		for(String noDefault: noDefaultFile){
			
			File file = new File(externFile,noDefault);
			delete(file);
		}
		Log.i(TAG,"delete the SD card directory is successfull...");
		return true;
	}
	
	/**
	 * 
	 * @param defaultFile 默认文件夹中的文件列表
	 * @return null外部存储器不可用
	 */
	private List<String> findNoDefaultFile(List<String>defaultFile){
		Log.i(TAG,"start find the undefault file");
		if(false == externalMemoryAvailable())//判断外部存储器是否可用
		{
			return null;
		}
		File externFile = Environment.getExternalStorageDirectory();
		List<String> noDefaultFiles = new ArrayList<String>();
		String[] allFileLists = externFile.list(null);
		HashSet<String>allFileSets = new HashSet<String>();
		allFileSets.addAll(defaultFile);
		//循环寻找非默认的文件
		for(String foundFile:allFileLists){
			if(false == allFileSets.contains(foundFile)){
				noDefaultFiles.add(foundFile);
			}
		}
		return noDefaultFiles;
	}
	/**
	 * 遍历指定的文件夹，并打印其文件名字
	 * 
	 * @param file需要遍历查看的文件
	 */
			
	private void traversalFile(File file){
		if(file.isFile()){
			System.out.println(file.getName());
			return;
		}
		if(file.isDirectory()){
			File []childFiles = file.listFiles();
			if(childFiles == null || childFiles.length == 0){
				System.out.println(file.getName());
				return;
			}
			for(File childFile:childFiles){
				traversalFile(childFile);
			}
			System.out.println(file.getName());
		}
	}

	/**
	 * 删除文件及其子文件夹
	 * @param file-需要删除的文件
	 */
    public static void delete(File file) {  
        if (file.isFile()) {  
           file.delete();  
           return;  
      }  
        if(file.isDirectory()){  
          File[] childFiles = file.listFiles();  
            if (childFiles == null || childFiles.length == 0) {  
                file.delete();  
              return;  
           }  
      
            for (int i = 0; i < childFiles.length; i++) {  
               delete(childFiles[i]);  
           }  
          file.delete();  
        }  
   } 
	
	/**
	 * 
	 * @true当前外部存储设备可用，否则为不可用
	 */
	private boolean externalMemoryAvailable(){
		return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
	}
	
	/**
	 * 对SD卡中的数据进行还原处理
	 * @return  true-对SD卡中的数据进行还原成功，否则数据还原失败
	 */
	public boolean sdRestore(){
		Log.i(TAG,"start restore sdCard....");
		if(false == externalMemoryAvailable()){//检验外部存储器是否可用,如果不可用，就直接返回
			Log.i(TAG,"the external memory is not available...");
			return false;
		}
		
	    //在还原之前，首先需要对SD卡中的数据进行清除.
		File sdCardFile = Environment.getExternalStorageDirectory();
		File restoreFile = new File(sdCardFile,RESTORE_FILE);
		if(!restoreFile.exists()||restoreFile.isFile()||!restoreFile.canRead()){
			Log.i(TAG,"无法找到备份文件，无法进行重置恢复");
			return false;
		}
		for(File sdCardSubFile:sdCardFile.listFiles())
		{
			if(RESTORE_FILE.equals(sdCardSubFile.getName())){
				continue;
			}
			if(sdCardSubFile.canWrite()){
				delete(sdCardSubFile);
			}
		}
		Long freeSpace = sdCardFile.getFreeSpace();
		long storeFileSpace = restoreFile.getTotalSpace() - restoreFile.getFreeSpace();
		if(freeSpace.longValue() < storeFileSpace){//检查存储空间是否足够
			return false;
		}
		Log.i(TAG,"SD目录中的文件已经删除");
		for(File restoreSubFile:restoreFile.listFiles()){
			Log.i(TAG,"当前已经还原了文件"+restoreSubFile.getName());
			if(!restoreSubFile.canRead())
			{
				continue;
			}
			//如果备份目录中的子文件是一个文件，则直接拷贝文件/
			if(restoreSubFile.isFile()){
				File sdRestoreFile = new File(sdCardFile,restoreSubFile.getName());
				try {
					sdRestoreFile.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				copyFile(restoreSubFile,sdRestoreFile);
			}//end if(restoreSubFile.isFile())
			else
			{
				//当前备份目录中的子文件时一个文件夹
				if(restoreSubFile.isDirectory()){
					copyDirectiory(restoreSubFile.getAbsolutePath(), 
							sdCardFile.getAbsolutePath()+File.separator+restoreSubFile.getName());
				}
			}
		}
		Log.i(TAG,"sd restore is complete");
		return true;
		
	}
	
	/**
	 * 对SD卡中的数据进行备份处理
	 * @return true数据备份成功，否则数据备份失败
	 */
	public boolean  backUp(){
		Log.i(TAG,"start back Up....");
		if(false == externalMemoryAvailable()){
			return false;
		}
		File sdCardFile = Environment.getExternalStorageDirectory();
		File backFile = new File(sdCardFile,BACK_UP_FILE);
		if(!backFile.exists()){//判断是否已经备份过一次，如果有备份过一次，则直接把删除所有的备份文件，然后从新建立备份文件夹
			backFile.mkdir();
		}
		else
		{
			delete(backFile);
			backFile.mkdir();
		}
		Long freeSpace = sdCardFile.getFreeSpace();
		long totalSpace = sdCardFile.getTotalSpace();
		if(freeSpace*2 <= totalSpace){//判断剩余空间是否足够存放备份文件
			return false;
		}
	    File[] sdcardFiles = sdCardFile.listFiles();
	    for(int i = 0 ; i < sdcardFiles.length;i++){
	    	if(false == sdcardFiles[i].canRead()){//如果文件不可读的话，则直接跳过，不用复制此文件
	    		continue;
	    	}
	    	if(BACK_UP_FILE.equals(sdcardFiles[i].getName()))
	    	{
	    		continue;
	    	}
	    	else
	    	{
	    		Log.i(TAG,"开始复制"+sdcardFiles[i].getName());
	    		if(sdcardFiles[i].isFile()){
	    		File targetFile = new File(backFile,sdcardFiles[i].getName());
			    try {
				   		targetFile.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block00
					e.printStackTrace();
				}
			    copyFile(sdcardFiles[i],targetFile);
		       }//end if(sdcardFiles[i].isFile
	    		else
	    		{
	    			if(sdcardFiles[i].isDirectory()){
	    				copyDirectiory(sdcardFiles[i].getAbsolutePath(),
	    						backFile.getAbsolutePath()+File.separator+sdcardFiles[i].getName());
	    			}
	    		}
	    	}
	    }
	    Log.i(TAG,"stop back Up....");
		return true;
	}
	/**
	 * 拷贝文件中的数据
	 * @param sourceFile源文件
	 * @param targetFile目标文件
	 */
	    public static void copyFile(File sourceFile,File targetFile){
	    try {
	    		byte[]buffer = new byte[1024*5];
				FileInputStream input = new FileInputStream(sourceFile);
			    BufferedInputStream inBuff = new BufferedInputStream(input);
				FileOutputStream output = new FileOutputStream(targetFile);
				BufferedOutputStream outBuff = new BufferedOutputStream(output);
				int len;
				while((len = inBuff.read(buffer))!= -1){
					outBuff.write(buffer, 0, len);
				}
				outBuff.flush();
				inBuff.close();
				outBuff.close();
				output.close();
				input.close();
	    	} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	    }
	    /**
	     * 拷贝源文件夹及其子文件夹中的数据到达目标文件夹
	     * @param sourceDir需要拷贝的源文件夹名字
	     * @param targetDir拷贝到的目标文件夹名字
	     */
	    public static void copyDirectiory(String sourceDir,String targetDir){
	    	new File(targetDir).mkdirs();
	    	//获取源文件下当前的文件或目录
	    	File[] file = new File(sourceDir).listFiles();
	    	for(int i = 0;i <file.length;i++){
	    		if(file[i].isFile()){
	    			File sourceFile = file[i];
	    			File targetFile = new File(new File(targetDir).getAbsolutePath()+File.separator+file[i].getName());
	    			copyFile(sourceFile, targetFile);
	    		}
	    		if(file[i].isDirectory()){
	    			String dir1 = sourceDir+File.separator+file[i].getName();
	    			String dir2 = targetDir+File.separator+file[i].getName();
	    			copyDirectiory(dir1, dir2);
	    		}
	    	}
	    }
	    

}
