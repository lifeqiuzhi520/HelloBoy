package com.yumu.delete;


import org.xml.sax.Parser;

import android.content.ContentProvider;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

/**
 * ɾ��SD���е���Ƶ����Ƶ��ͼƬ�ļ�
 * @author л־��
 *
 */
public class DeleteMieda {
	
	private static String TAG = "DeleteMieda";
	private static Uri[] URI = new Uri[]{ MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,//�����Ƶ�ļ���Uri
		MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
		MediaStore.Images.Media.EXTERNAL_CONTENT_URI,//���ͼƬ�ļ���Uri
		MediaStore.Video.Media.EXTERNAL_CONTENT_URI  //�����Ƶ�ļ���Uri
		}; 
	/**
	 * ɾ��SD���е���Ƶ����Ƶ��ͼƬ�ļ�
	 * @param context Ӧ�ó���������
	 * 
	 */
	public void deleteMieda(Context context){
		
		Log.i(TAG,"start deleteMieda file...");
		for(Uri mediaUri:URI){
			delete(context,mediaUri);
		}
		Log.i(TAG,"delete media successfull.");
	}
	/**
	 * ���ݶ�ý���ļ���uri��ɾ����ȫ������
	 * @param ��ý���ļ���ָ���uri
	 */
	private void delete(Context context, Uri uri){
		ContentResolver resolver = context.getContentResolver();
		resolver.delete(uri, "1", null);
	}
}
