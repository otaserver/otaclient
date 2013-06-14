package com.otaserver.client.framework;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.otaserver.client.DialogClass;
import com.otaserver.client.utils.Constants;
import com.otaserver.client.utils.updateFile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.os.RecoverySystem;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class UpdatePackage {
	private static final String Tag = "UpdatePackage" ;
	private static final int verifyPkgFail = 0 ;
	private static final int verifyPkgProgress = 1 ;
	private static int curbatteryLevel ;
	private String UsablePath = null ;
	private volatile boolean mVerifyPkgThreadIsRunning = false ;
	private int intLevel ;
	private int intScale; 
	private batteryListener mbatteryListener = null ;
	private Context context = null ;
	private String mPkgPath = Constants.FilePath + Constants.UpdateZipPackageFileName ; ;
	
	/**
	 * 升级类的构造函数,升级包为默认路径：Constants.FilePath + Constants.UpdateZipPackageFileName
	 * @param context  Context对象
	 */
	public UpdatePackage(Context context) {
		this.context = context;
	}
	
	
	/**
	 * 升级类的构造函数,可自携带升级包路径。现该方法为SD卡本地升级所准备。
	 * @param context  context  Context对象
	 * @param PkgPath  升级包路径
	 */
	
	public UpdatePackage(Context context,String PkgPath) {
		mPkgPath = PkgPath ;
		this.context = context;
	}
	
    /**
     * 外层应用调用的升级函数，该函数会注册电量的监听，电量的值允许升级，执行升级动作，电量的值
     * 不允许升级，回调接口函数通知调用者
     * @param mbatteryListener  实现电量不足回调的接口对象
     */

	public void Installpackage(batteryListener mbatteryListener) {
		this.mbatteryListener = mbatteryListener ;
		DialogClass.showProgressDialog_h(context, "正在验证") ;
		UsablePath = getUsablePath(mPkgPath) ;
		verifyPkg(UsablePath,new verifyPkgListener() ) ;

	}

	/**
	 * 监听当前电量的广播
	 */
	private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
				intLevel = intent.getIntExtra("level", 0);
				intScale = intent.getIntExtra("scale", 100);
				onBatteryInfoReceiver(intLevel, intScale);
			}
		}
	};

	/**
	 * 得到电量值，判断是否升级
	 * @param intLevel
	 * @param intScale
	 */
	private void onBatteryInfoReceiver(int intLevel, int intScale) {
		Log.d(Tag, "Level is" + intLevel + "Scale is" + intScale + "L/S is"
				+ (intLevel * 100) / intScale);
		curbatteryLevel = (intLevel * 100) / intScale;
		if (curbatteryLevel > 30) {
			Log.d(Tag, mPkgPath);
			installpackage(mPkgPath);
		}else{
			context.unregisterReceiver(mBatInfoReceiver) ;
			mbatteryListener.shortageBattery(curbatteryLevel) ;
		}
	}
	
	
	private void verifyPkg(String PkgPath,RecoverySystem.ProgressListener listener){
    	final String pathname = PkgPath ;
    	final RecoverySystem.ProgressListener Listener = listener ;
    	new Thread(new Runnable(){

			@Override
			public synchronized void run() {
				// TODO Auto-generated method stub
				if(mVerifyPkgThreadIsRunning){
					Log.d(Tag, "mVerifyPkgThreadIsRunning is true,The Thread is return!!!") ;
					return ;
				}
				mVerifyPkgThreadIsRunning = true ;
				try{
					RecoverySystem.verifyPackage(getpackageFile(pathname), Listener, null) ;
					
				}catch(Exception e){
					handler.sendEmptyMessage(verifyPkgFail) ;
				}
				mVerifyPkgThreadIsRunning = false ;
			}
    		
    	}).start() ;
	} 
	
	
	
	
	/**
	 * 调用Android升级函数，执行升级动作
	 * @param path 升级包文件地址
	 */
	private void installpackage(String path) {
		if (UsablePath == null)
			UsablePath = getUsablePath(path);

		try {
			RecoverySystem.installPackage(context, getpackageFile(UsablePath));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d(Tag, "InstallPackage is error = " + e.getMessage());
		}

	}
    
	
	
    /**
     * 得到OTA升级文件
     * @param path 升级包文件地址
     * @return  返回OTA升级文件
     */
	
	
	private File getpackageFile(String path){
    	updateFile packagefile = new updateFile(path) ;
    	try {
			String Canonicalpath = packagefile.getCanonicalPath() ;
			Log.d(Tag, "getCanonicalPath = " +Canonicalpath) ;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	if(!packagefile.exists()){
    		Log.d(Tag, "FILE---------NULL") ;
    		return null ;
    	}
    	
    	return packagefile ;
    }
	
	
	
	
	/**
	 * 接收pkg验证的结果，成功或失败
	 */
	
	
	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
			case verifyPkgFail:
				DialogClass.cancelProgressDialog_h() ;
//				Toast.makeText(context, "校验失败", Toast.LENGTH_SHORT).show() ;
				DialogClass.showAlertDialog(context, "验证失败", "升级包验证失败，继续升级可能存在风险，是否继续?","确定" ,"取消" ,
									new View.OnClickListener() {
										
										@Override
										public void onClick(View v) {
											// TODO Auto-generated method stub
											DialogClass.CancelDialog() ;
											context.registerReceiver(mBatInfoReceiver, new IntentFilter(
													Intent.ACTION_BATTERY_CHANGED));
										}
									}, new View.OnClickListener(){

										@Override
										public void onClick(View v) {
											// TODO Auto-generated method stub
											DialogClass.CancelDialog() ;
										}});
				break ;
			case verifyPkgProgress :
				int progress_value = (Integer)msg.obj ;
				DialogClass.setProgress(progress_value) ;
				if(progress_value==100){
					DialogClass.cancelProgressDialog_h() ;
					context.registerReceiver(mBatInfoReceiver, new IntentFilter(
							Intent.ACTION_BATTERY_CHANGED));
				}
				break ;
				
			}
		}
		
	};
					
	
	
	
	 /**
	  * 适配"/mnt/"
	  */
	 
	private String getUsablePath(String path) {
		if (path.startsWith("/mnt")) 
			path = path.substring(4);
		if(path.startsWith("/emmc")){
			path = path.replace("/emmc", "/sdcard") ;
		}
		return path;
	}
	


	
	/**
	 * 实现RecoverySystem.ProgressListener接口，用于得到验证进度
	 * @author jiangpeijun
	 *
	 */
	
	class verifyPkgListener implements RecoverySystem.ProgressListener {

		@Override
		public void onProgress(int progress) {
			// TODO Auto-generated method stub
			Message msg = handler.obtainMessage(verifyPkgProgress, progress) ;
			handler.sendMessage(msg) ;
		}
		
	}
	
	
	
	
	/**
	 * 判断电量不足的接口，用于通知调用者电量不足，无法升级
	 * @author root
	 *
	 */
	
	public interface batteryListener{
		void shortageBattery(int batteryLevel) ;
	}
}
