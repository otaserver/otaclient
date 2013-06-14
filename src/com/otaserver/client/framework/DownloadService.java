package com.otaserver.client.framework;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;

import com.otaserver.client.R;
import com.otaserver.client.OtaActivity;
import com.otaserver.client.ProgressInter;
import com.otaserver.client.utils.Constants;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;

public class DownloadService extends Service{
	public static final String Tag = "DownloadService" ;
	/**
	 * NOTWORK 下载工作没有被触发
	 */
	public static final int NOTWORK = 1000 ;
	
	/**
	 * START  描述了一个下载时的状态，表示开始下载
	 */

	public static final int START = 1001 ;
	
	/**
	 * DOWNLOADING  描述了一个下载时的状态，表示正在下载
	 */

	public static final int DOWNLOADING = 1002 ;
	
	/**
	 * PAUSE  描述了一个下载时的状态，表示暂停下载
	 */

	public static final int PAUSE = 1003 ;
	
	/**
	 * COMPLETE  描述了一个下载时的状态，表示完成下载
	 */

	public static final int COMPLETE = 1004 ;
	
	/**
	 * FAILURE  描述了一个下载时的状态，表示下载失败
	 */
	
	public static final int FAILURE = 1005 ;
	
	
	/**
	 * STOP  停止下载
	 */
	
	public static final int STOP = 1007 ;
	
	private static final int NF_DID = 0x12 ;
	private static final int NF_CID = 0x13 ;
	private static Notification Dnotify = null; 
	private static Notification Cnotify = null;
	private static NotificationManager manager = null;
	private static Context mContext = null ;
	private boolean mIsBound = false ;
	private String downloadUrl = null ;
	private String md5 = null ;
	private long fileSize = 0 ;
	private DownloadUpdatePackage mDownloadUpdatePackage = null ;
	private boolean isShowNotify = false ;
	private Intent boundService = null ;
	private MyBinder mMyBinder = new MyBinder() ;
	private Handler mHandler = null ;
	private ProgressInter mProgressInter = null ;
	private SharedPreferences  SP = null ;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		Log.d(Tag, "DownloadService is onBind!!!") ;
		cancelNotify(NF_DID) ;
		stopForeground(true);
		return mMyBinder;
		
	}
	
	public class MyBinder extends Binder{
		public DownloadService getService(Handler handler){
			mHandler = handler ;
			return DownloadService.this ;
		}
		
	}
		
	
	public int getStatus(){
		int status = 0 ;
		if(mDownloadUpdatePackage!=null)
			status = mDownloadUpdatePackage.getDownloadStatus() ;
		else
			status = NOTWORK ;
		return status ;
	}

	public void setProgressInter(ProgressInter in){
		mProgressInter = in ;
	}
	
	
	public void showNotify(){
		isShowNotify = true ;
		showDNotify() ;
		startForeground(NF_DID, Dnotify);
		mDownloadUpdatePackage.new callBackThread().start() ;
	}
	
	public void cancelNotifyD(){
		Log.d(Tag, "cancelNotifyD") ;
		isShowNotify = false ;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		init() ;
		
		Log.d(Tag, "DownloadService is onCreate!!!") ;
		
	}

	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		if(!mIsBound){
			//显示下载Notify
			Log.d(Tag, "DownloadService is onStartCommand!!!") ;
			mDownloadUpdatePackage.startDownload() ;
			mIsBound = true ;
		}
		return START_NOT_STICKY;
	}
	
	
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mDownloadUpdatePackage = null ;
		Log.d(Tag, "DownloadService is onDestroy!!!") ;
	}
	
	/**
	 * 初始化download对象
	 */
	
	private void init(){
		mDownloadUpdatePackage = new DownloadUpdatePackage() ;
		SP = this.getSharedPreferences(Constants.SP_Name, Service.MODE_PRIVATE) ;
		
	}
	
	/**
	 * 开始下载服务
	 */
	public void doBindService(){
		boundService = new Intent(mContext,DownloadService.class) ;
		if(!mIsBound){
			Log.d(Tag, "DownloadService is doBindService!!!!!!!!!!!!!!!!!!!!!") ;
			startService(boundService) ;
			
		}
	}
			
	
	
	/**
	 * 停止服务
	 */
	
	public void mStopService(){
		if(mIsBound){
//			stopSelf() ;
			stopService(boundService) ;
			mIsBound = false ;
		}
	}
			
		
	//显示下载完成Notification
	private void showCNotify() {
		if(Cnotify==null){
			Cnotify = new Notification(R.drawable.icon, "下载完成",
					System.currentTimeMillis());
			Cnotify.icon = R.drawable.completeicon ;
			Cnotify.flags = Notification.FLAG_AUTO_CANCEL ;
//			Cnotify.contentView = new RemoteViews(this
//					.getPackageName(), R.layout.notify_complete_content);
			manager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
			Intent completeIntent = new Intent() ;
			completeIntent.setClass(this, OtaActivity.class) ;
			completeIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP) ;
			Cnotify.contentIntent = PendingIntent.getActivity(this, 0,
					completeIntent, 0);
			Cnotify.setLatestEventInfo(mContext, "固件下载", "下载完成！", Cnotify.contentIntent) ;
		}
			
		manager.notify(NF_CID, Cnotify) ;
	}
	//显示正在下载Notification
	private void showDNotify() {
		if(Dnotify==null){
			Dnotify = new Notification(R.drawable.icon, "正在下载",
					System.currentTimeMillis());
			manager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
			Dnotify.contentView = new RemoteViews(this.getPackageName(), R.layout.notify_downloading_content);
			Dnotify.contentView.setImageViewResource(R.id.imageView1, R.drawable.downloadicon) ;
			Intent notifyIntent = new Intent() ;
			notifyIntent.setClass(this, OtaActivity.class) ;
			notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP) ;
			Dnotify.contentIntent = PendingIntent.getActivity(this, 0,
					notifyIntent, 0);
			
		}
		manager.notify(NF_DID, Dnotify);
		
	}
	
	public void cancelNotify(int id){
		manager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
		manager.cancel(id) ;
	}
	
	

	
	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		super.onLowMemory();
		Log.d(Tag, "onLowMemory low low low low") ;
	}

	/**
	 * 下载广播
	 * @author jiangpeijun
	 *
	 */
	
	public class downloadReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			mContext = context ;
			
			String action = intent.getAction() ;
			if(null != action){
				if(action.equals(Constants.DOWNLOAD_START)){
					downloadUrl = SP.getString(Constants.DownloadUrl, "");
					md5 = SP.getString(Constants.SP_MD5, "") ;
					fileSize = SP.getLong(Constants.FileSize, 0) ;
					doBindService() ;
					
				}else if(action.equals(Constants.DOWNLOAD_PAUSE)){
					if(mIsBound){
						mDownloadUpdatePackage.setPause() ;
						mStopService();
					}
						
				}else if(action.equals(Constants.DOWNLOAD_STOP)){
					if(mIsBound){
						mDownloadUpdatePackage.setStop() ;
						mStopService() ;
					}
						
				}
			}
		}
		
	}
					
	
	
	/**
	 * 下载内部类
	 */
	
	public class DownloadUpdatePackage {
		private String filelocal = Constants.FilePath + Constants.UpdateZipPackageFileName ;
		private static final int DloadComplete = 2 ;
		private static final int DloadFailure = 3 ;
		private long CompleteLength = 0;
		private int _progrss = 0 ;
		private HttpURLConnection connection = null ;
		private DownloadThread mDownloadThread = null ;
		private InputStream inputstream = null ;
		private RandomAccessFile randomAccessFile = null ;
		private boolean downloadThreadIsRunning = false ;
		public DownloadUpdatePackage(){
			
		}
		
		
		/**
		 * 该函数暴露给上层应用，暂停暂停下载任务
		 */
		
		public void setPause(){
			mDownloadThread.Status = PAUSE ;
			downloadThreadIsRunning = false ;	
			Log.d(Tag, "Pause !!! ") ;
		}
		
		/**
		 * 该函数暴露给上层应用，停止下载任务
		 */
		
		public void setStop(){
			mDownloadThread.Status = STOP ;			
			downloadThreadIsRunning = false ;	
			Log.d(Tag, "Stop !!! ") ;
		}
		
		/**
		 * 得到下载的状态
		 */
		public int getDownloadStatus(){
			if(mDownloadThread!=null){
				return mDownloadThread.Status ;
			}
			return NOTWORK ;	
		}
					
		
		/**
		 * 该函数暴露给上层应用，用于执行下载动作，其中会判断是否有下载线程正在执行，如果有直接return，如果没有会开始一个新的下载线程执行下载任务
		 * @param mDownloadMonitor  下载类的内部接口对象，用于通知上层关于下载的状态、进度和下载完成后对升级包的校验结果
		 */
		public void startDownload() {
		
			if (downloadThreadIsRunning) {
				Log.d(Tag, "任务正在执行") ;
				return;
			}

			initDownloadInfo();
			
			File file = new File(filelocal);
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					mStopService() ;
					e.printStackTrace();
				}
			}

			CompleteLength = file.length();
			if(CompleteLength<fileSize){

				
				//初始化progress进度条进度值
				int _progrss = (int)(((float)CompleteLength/(float)fileSize)*100) ;	
				mProgressInter.progress(_progrss, CompleteLength) ;

				mDownloadThread = new DownloadThread(downloadUrl);
				mDownloadThread.Status = START ;
				//启动下载线程
				mDownloadThread.start();
				
				
				
				
				Log.d(Tag, "CompleteLength = " + CompleteLength);
			}else
				mStopService() ;

		}
		
		/**
		 * 断点续传时校验欲下载的安装包与已下载的部分的文件的下载源是否一致，如果一致进行断点续传，如果不一致，删除已下载的部分文件，重新下载新包
		 */
		private void initDownloadInfo() {

			boolean UpdInfoIsNew = SP.getBoolean(Constants.SP_Tag_UpInfoIsNew,
					false);
			// 断点续传时校验欲下载的安装包与已下载的部分的文件的下载源是否一致，如果一致进行断点续传，如果不一致，删除已下载的部分文件，重新下载新包
			if (UpdInfoIsNew) {
				SP.edit().putBoolean(Constants.SP_Tag_UpInfoIsNew, false)
						.commit();
				File packagefile = new File(filelocal);
				if (packagefile.exists()) {
					packagefile.delete();
					Log.d(Tag, "UpdInfoIsNew=" + UpdInfoIsNew
							+ " packagefile is deleted");
				}

			}

		}
		
		/**
		 * 下载线程，实现下载功能，断点续传功能，并会告知上层应用关于下载的状态结果
		 * @author root
		 *
		 */
		
		private class DownloadThread extends Thread {
			/**
			 * Status  下载的状态
			 */
			
			private int Status = NOTWORK ;
			String urlstr = null ;
			public DownloadThread(String url){
				this.urlstr = url ;
			}
			@Override
			public  void run() {
				// TODO Auto-generated method stub
				download() ;
					
			}

			private synchronized void download(){
				Status = DOWNLOADING;
				downloadThreadIsRunning = true ;
				
				try{
					HttpClient client = new DefaultHttpClient() ;
					client.getParams().setParameter(Constants.http_socket_timeout, 30000) ;
					client.getParams().setParameter(Constants.http_connection_timeout, 5000) ;
					HttpGet get = new HttpGet(urlstr) ;
					
					//设置下载的数据位置XX字节到XX字节  
					Header header_size = new BasicHeader("Range", "bytes=" + CompleteLength + "-"  + fileSize); 
					get.addHeader(header_size) ;

					HttpResponse Response = client.execute(get);
					randomAccessFile = new RandomAccessFile(filelocal, "rwd");
					
					//从文件的size以后的位置开始写入，其实也不用，直接往后写就可以。有时候多线程下载需要用 
					randomAccessFile.seek(CompleteLength);
					inputstream = Response.getEntity().getContent();
					if (null == inputstream) {
						throw new RuntimeException("downloadinstream is null");
					}

					byte[] buffer = new byte[4096];
					do {
						int numread = inputstream.read(buffer);
						if (numread <= 0) {
							break;
						}
						randomAccessFile.write(buffer, 0, numread);
						CompleteLength += numread;
						_progrss = (int)(((float)CompleteLength/(float)fileSize)*100) ;	
						mProgressInter.progress(_progrss, CompleteLength);
						if (CompleteLength == fileSize) {
							Status = COMPLETE;
							Log.d(Tag, "Status = " + Status);
//							if (isShowNotify) {
//								Dnotify.contentView
//										.setProgressBar(R.id.progressBar1, 100,
//												_progrss, false);
//								Dnotify.contentView.setTextViewText(
//										R.id.textView1, CompleteLength / 1000
//												+ "kb" + "/" + fileSize / 1000
//												+ "kb");
//								manager.notify(NF_DID, Dnotify);
//								cancelNotify(NF_DID);
//								stopForeground(true);
//							}
							showCNotify();
							Message msg = mHandler.obtainMessage(DloadComplete,
									md5);
							mHandler.sendMessage(msg);
							downloadThreadIsRunning = false;
						}
						
						//停止下载时删除已经下载的文件
						if(Status == STOP){
							File PkgFile = new File(filelocal) ;
							if(PkgFile.exists()) 
								PkgFile.delete() ;
						}
					} while (downloadThreadIsRunning);
					inputstream.close();
					randomAccessFile.close() ;
					mStopService() ;		

				}
				catch(Exception e){
					Log.d(Tag, "Exception in downloadThread ") ;
					Status = FAILURE ;
					downloadThreadIsRunning = false ;
					mHandler.sendEmptyMessage(DloadFailure) ;
					if(isShowNotify){
						cancelNotify(NF_DID) ;
						stopForeground(true) ;
					}
					
					//停止下载时删除已经下载的文件
					if(Status == STOP){
						File PkgFile = new File(filelocal) ;
						if(PkgFile.exists()) 
							PkgFile.delete() ;
					}
					
					mStopService() ;
					e.printStackTrace() ;
				}
				
				Log.d(Tag, "DownloadThread is interrupted") ;
			}				
					
		}
		
		/**
		 * 提供Notification中progressbar的进度值
		 * @author jiangpeijun
		 *
		 */
		private boolean callBackThreadisRunning = false ; 
		private class callBackThread extends Thread {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(callBackThreadisRunning){
					Log.d(Tag, "callBackThread is running") ;
					return ;
				}
				callBackThreadisRunning = true ;

				do{
					try {
						sleep(2000) ;
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						
					}
					callback() ;
					
				}while(downloadThreadIsRunning&&isShowNotify) ;
				cancelNotify(NF_DID) ;
				stopForeground(true);
				callBackThreadisRunning = false ;
				Log.d(Tag, "callBackThread is interrupted") ;
			}
			
			private void callback(){
				if (mDownloadThread.Status == DOWNLOADING) {

					Log.d(Tag, "CompleteLength =" + CompleteLength);

					Dnotify.contentView.setProgressBar(R.id.progressBar1, 100,
							_progrss, false);
					Dnotify.contentView.setTextViewText(R.id.textView1,
							CompleteLength / 1024 + "kb" + "/" + fileSize
									/ 1024 + "kb");
					manager.notify(NF_DID, Dnotify);

				}
			}
			
			
		}
			
	}

}
