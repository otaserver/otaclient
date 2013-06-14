package com.otaserver.client;

import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

public class GetSDStatus {
	private static final String Tag = "getSDStatus" ;
	private static final String StartWith1 = "/mnt/sdcard" ;
	private static final String StartWith2 = "/sdcard" ;
	public static String getSDPath(){
		File SdPath = null ;
		SdPath = Environment.getExternalStorageDirectory() ;
		Log.d(Tag, "SdPath is :" + SdPath) ;
		return SdPath.getPath() ;
	}
	//判断Sd是否存在
	public static boolean isSdExist(){
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			String path = Environment.getExternalStorageDirectory().getPath() ;
			if(path.startsWith(StartWith1)||path.startsWith(StartWith2)) 
				return true ;
		}
		return true ;
	}
	//获取Sd可用空间大小
	public static float getSdAvailableBlocks(){
		File SdPath = null ;
		float availCount  = 0 ;
		SdPath = Environment.getExternalStorageDirectory() ;
		StatFs statfs = new StatFs(SdPath.getPath()) ;
		long blockSize = statfs.getBlockSize() ;
		long AvailBlocks = statfs.getAvailableBlocks() ;
		availCount = (AvailBlocks*blockSize)/1024 ;
		Log.d(Tag, "AvailableBlocks is :" + availCount) ;

		return availCount ;
	}


}
