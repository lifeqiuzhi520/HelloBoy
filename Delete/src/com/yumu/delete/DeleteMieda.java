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
 * 删除SD卡中的音频和视频和图片文件
 * @author 谢志华
 *
 */
public class DeleteMieda {
	
	private static String TAG = "DeleteMieda";
	private static Uri[] URI = new Uri[]{ MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,//存放音频文件的Uri
		MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
		MediaStore.Images.Media.EXTERNAL_CONTENT_URI,//存放图片文件的Uri
		MediaStore.Video.Media.EXTERNAL_CONTENT_URI  //存放视频文件的Uri
		}; 
	/**
	 * 删除SD卡中的音频，视频和图片文件
	 * @param context 应用程序上下文
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
	 * 根据多媒体文件的uri，删除其全部数据
	 * @param 多媒体文件所指向的uri
	 */
	private void delete(Context context, Uri uri){
		ContentResolver resolver = context.getContentResolver();
		resolver.delete(uri, "1", null);
	}
}
