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
 *  ɾ��SD���еķ�Ĭ�ϵ��ļ�
 * @author л־��
 *
 */
public class DeleteDir {
	private static String TAG ="DeleteDir";
	 private static String BACK_UP_FILE = "YuMuBackUp";//��SD�����б���ʱ��sd���ĸ�Ŀ¼�е��ļ���
	 private static String RESTORE_FILE = BACK_UP_FILE;//����SD�����л�ԭʱ��Դ���ݵ��ļ�������
	/**;
	 * ɾ��SD���еķ�Ĭ���ļ���
	 * @param SDdefaultFolder�����SD���ϵ�Ĭ���ļ�������
	 * @return trueɾ���ɹ���falseɾ��ʧ��
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
	 * ɾ��SD���еķ�Ĭ�ϼ������ļ���
	 * @param SDdefaultFolder�����SD���ϵ�Ĭ���ļ����б�
	 * @return trueɾ���ɹ���falseɾ��ʧ��
	 * 
	 */
	public  boolean deleteDir(List<String>SDdefaultFolder) {
		Log.i(TAG, "start delete the SD card directory");
		
		//�ҵ��ⲿ�洢���еķ�Ĭ���ļ���
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
	 * @param defaultFile Ĭ���ļ����е��ļ��б�
	 * @return null�ⲿ�洢��������
	 */
	private List<String> findNoDefaultFile(List<String>defaultFile){
		Log.i(TAG,"start find the undefault file");
		if(false == externalMemoryAvailable())//�ж��ⲿ�洢���Ƿ����
		{
			return null;
		}
		File externFile = Environment.getExternalStorageDirectory();
		List<String> noDefaultFiles = new ArrayList<String>();
		String[] allFileLists = externFile.list(null);
		HashSet<String>allFileSets = new HashSet<String>();
		allFileSets.addAll(defaultFile);
		//ѭ��Ѱ�ҷ�Ĭ�ϵ��ļ�
		for(String foundFile:allFileLists){
			if(false == allFileSets.contains(foundFile)){
				noDefaultFiles.add(foundFile);
			}
		}
		return noDefaultFiles;
	}
	/**
	 * ����ָ�����ļ��У�����ӡ���ļ�����
	 * 
	 * @param file��Ҫ�����鿴���ļ�
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
	 * ɾ���ļ��������ļ���
	 * @param file-��Ҫɾ�����ļ�
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
	 * @true��ǰ�ⲿ�洢�豸���ã�����Ϊ������
	 */
	private boolean externalMemoryAvailable(){
		return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
	}
	
	/**
	 * ��SD���е����ݽ��л�ԭ����
	 * @return  true-��SD���е����ݽ��л�ԭ�ɹ����������ݻ�ԭʧ��
	 */
	public boolean sdRestore(){
		Log.i(TAG,"start restore sdCard....");
		if(false == externalMemoryAvailable()){//�����ⲿ�洢���Ƿ����,��������ã���ֱ�ӷ���
			Log.i(TAG,"the external memory is not available...");
			return false;
		}
		
	    //�ڻ�ԭ֮ǰ��������Ҫ��SD���е����ݽ������.
		File sdCardFile = Environment.getExternalStorageDirectory();
		File restoreFile = new File(sdCardFile,RESTORE_FILE);
		if(!restoreFile.exists()||restoreFile.isFile()||!restoreFile.canRead()){
			Log.i(TAG,"�޷��ҵ������ļ����޷��������ûָ�");
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
		if(freeSpace.longValue() < storeFileSpace){//���洢�ռ��Ƿ��㹻
			return false;
		}
		Log.i(TAG,"SDĿ¼�е��ļ��Ѿ�ɾ��");
		for(File restoreSubFile:restoreFile.listFiles()){
			Log.i(TAG,"��ǰ�Ѿ���ԭ���ļ�"+restoreSubFile.getName());
			if(!restoreSubFile.canRead())
			{
				continue;
			}
			//�������Ŀ¼�е����ļ���һ���ļ�����ֱ�ӿ����ļ�/
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
				//��ǰ����Ŀ¼�е����ļ�ʱһ���ļ���
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
	 * ��SD���е����ݽ��б��ݴ���
	 * @return true���ݱ��ݳɹ����������ݱ���ʧ��
	 */
	public boolean  backUp(){
		Log.i(TAG,"start back Up....");
		if(false == externalMemoryAvailable()){
			return false;
		}
		File sdCardFile = Environment.getExternalStorageDirectory();
		File backFile = new File(sdCardFile,BACK_UP_FILE);
		if(!backFile.exists()){//�ж��Ƿ��Ѿ����ݹ�һ�Σ�����б��ݹ�һ�Σ���ֱ�Ӱ�ɾ�����еı����ļ���Ȼ����½��������ļ���
			backFile.mkdir();
		}
		else
		{
			delete(backFile);
			backFile.mkdir();
		}
		Long freeSpace = sdCardFile.getFreeSpace();
		long totalSpace = sdCardFile.getTotalSpace();
		if(freeSpace*2 <= totalSpace){//�ж�ʣ��ռ��Ƿ��㹻��ű����ļ�
			return false;
		}
	    File[] sdcardFiles = sdCardFile.listFiles();
	    for(int i = 0 ; i < sdcardFiles.length;i++){
	    	if(false == sdcardFiles[i].canRead()){//����ļ����ɶ��Ļ�����ֱ�����������ø��ƴ��ļ�
	    		continue;
	    	}
	    	if(BACK_UP_FILE.equals(sdcardFiles[i].getName()))
	    	{
	    		continue;
	    	}
	    	else
	    	{
	    		Log.i(TAG,"��ʼ����"+sdcardFiles[i].getName());
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
	 * �����ļ��е�����
	 * @param sourceFileԴ�ļ�
	 * @param targetFileĿ���ļ�
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
	     * ����Դ�ļ��м������ļ����е����ݵ���Ŀ���ļ���
	     * @param sourceDir��Ҫ������Դ�ļ�������
	     * @param targetDir��������Ŀ���ļ�������
	     */
	    public static void copyDirectiory(String sourceDir,String targetDir){
	    	new File(targetDir).mkdirs();
	    	//��ȡԴ�ļ��µ�ǰ���ļ���Ŀ¼
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
