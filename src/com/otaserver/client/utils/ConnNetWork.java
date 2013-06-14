package com.otaserver.client.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnNetWork {
	
	public static final int WIFI_ON = 200 ;
	public static final int WIFI_OFF = 201 ;
	public static final int MOBILE_ON = 202 ;
	public static final int Not_Connect = 203 ;
	
	//检查网络--WIFI或MOBILE有一种可用返回true
	public static boolean isConnNetWork(Context context) {  
		ConnectivityManager connectivityManager = (ConnectivityManager)context   
	            .getSystemService(Context.CONNECTIVITY_SERVICE);  
	    NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();  
	     if (activeNetInfo != null  
	             && (activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI || activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE)) {  
	        return true;  
	     }  
	     return false;  
	}
	
	
	//根据应用设置检查网络连接
	public static int isWiFiOnly(Context context){
		SharedPreferences mSP = context.getSharedPreferences(Constants.SP_Settings, Context.MODE_PRIVATE) ;
		boolean isWifiOnly = mSP.getBoolean(Constants.SP_Settings_WifiOnly, true) ;
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if(isWifiOnly){
			if(activeNetInfo != null && activeNetInfo.getType()==ConnectivityManager.TYPE_WIFI)
				return WIFI_ON ;
			else
				return WIFI_OFF ;
		}else{
			if(activeNetInfo != null && activeNetInfo.getType()==ConnectivityManager.TYPE_WIFI){
				return WIFI_ON ;
			}else if(activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE){
				return MOBILE_ON ;
			}
		}
		return Not_Connect ;
	}
}
