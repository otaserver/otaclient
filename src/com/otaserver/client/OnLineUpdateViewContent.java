package com.otaserver.client;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import com.otaserver.client.R;
import com.otaserver.client.sdk.CheckUpdate.checkUpdateInter;
import com.otaserver.client.sdk.CheckUpdate;
import com.otaserver.client.sdk.MobileInfo;
import com.otaserver.client.sdk.SendUpdateReport;
import com.otaserver.client.framework.UpdatePackage;
import com.otaserver.client.framework.UpdatePackage.batteryListener;
import com.otaserver.client.utils.ConnNetWork;
import com.otaserver.client.utils.Constants;
import com.otaserver.client.utils.MD5Sum;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class OnLineUpdateViewContent implements checkUpdateInter,ProgressInter,batteryListener{
	private static final String Tag = "onLineUpdateViewContent" ;
	private static final int NOTWORK = 1000 ;
	private static final int START = 1001 ;
	private static final int DOWNLOADING = 1002 ;
	private static final int PAUSE = 1003 ;
	private static final int COMPLETE = 1004 ;
	private static final int update = 0 ;
	private static final int Unupdate = 1 ;
	private static final int DloadComplete = 2 ;
	private static final int DloadFailure = 3 ;
	private static final int install_NotDload = 4 ;
	private static final int progressTag = 5 ;
	private String targetVer = null ;
	private String UpdateMessage = null ;
	private String packageSize = null ;
	private String downloadUrl = null ;
	private String md5 = null ;
	private String curFirmwareVer = null ;
	private String IMEI = null ;
	private String deviceModel = null ;
	private ViewHolder mHolder = null ;
	private Context mContext = null ;
	private View mView = null ;
	private long fileSize = 0 ;
	private int status_download = -1 ;
	private SharedPreferences sp = null ;
	
	public OnLineUpdateViewContent(Context context,View view){
		
		//定义SharedPreferences
		mContext = context ;
		mView = view ;
		MobileInfo mMobileInfo = new MobileInfo(mContext) ;
		curFirmwareVer = mMobileInfo.getFirmwareType() ;
		IMEI = mMobileInfo.getImeiNumber() ;
		deviceModel = mMobileInfo.getDeviceModel() ;
	}
	
	public void startShowView(){
		sp = mContext.getSharedPreferences(Constants.SP_Name, Activity.MODE_PRIVATE) ;
		mHolder = new ViewHolder() ;
		initOnLineViewWidget(mView) ;
		initUpdateInfo() ;
	}
	
	private void initUpdateInfo(){
		this.targetVer = sp.getString(Constants.TargetVer, "") ;
		this.fileSize = sp.getLong(Constants.FileSize, 0) ;
		this.UpdateMessage = sp.getString(Constants.UpdateInfo, "") ;
	}
		
	
	private void initOnLineViewWidget(View convertView){
		//在这里可以初始化geller
		mHolder.check = (Button)convertView.findViewById(R.id.checkupdate) ;
		mHolder.download = (Button)convertView.findViewById(R.id.download) ;
		mHolder.Pause = (Button)convertView.findViewById(R.id.pause) ;
		mHolder.Stop = (Button)convertView.findViewById(R.id.stop) ;
		mHolder.mPauseButtonLayout = (LinearLayout)convertView.findViewById(R.id.pauseStopLayout) ;
		mHolder.buttonUpdate = (Button)convertView.findViewById(R.id.update1) ;
		mHolder.textTargetVer = (TextView) convertView.findViewById(R.id.targetver) ;
		mHolder.textPkgSize = (TextView) convertView.findViewById(R.id.pkgsize) ;
		mHolder.textDesc_info = (TextView) convertView.findViewById(R.id.desc_info) ;
		mHolder.mProgressBarLayout = (LinearLayout)convertView.findViewById(R.id.progressLinearLayout) ;
		mHolder.mProgressBar = (ProgressBar)convertView.findViewById(R.id.progressbar) ;
		mHolder.mPgbText = (TextView)convertView.findViewById(R.id.progressText) ;
	    mHolder.scrollview = (ScrollView)convertView.findViewById(R.id.online_scrollview) ;
		//查询点击事件
		mHolder.check.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(ConnNetWork.isConnNetWork(mContext)){
					if(GetSDStatus.isSdExist()){
						DialogClass.showProgressDialog(mContext,"正在查询，请稍后...") ;
						new Thread(new Runnable(){

							@Override
							public void run() {
								// TODO Auto-generated method stub
								CheckUpdate mCheckUpdate = new CheckUpdate(mContext,Constants.FilePath) ;
								mCheckUpdate.recordCheckTime(Constants.TimeRecordFilePath,Constants.TimeRecordFileName) ;
								mCheckUpdate.StartCheck(OnLineUpdateViewContent.this) ;
							}
								
						}).start() ;
							
					}else{
						Toast.makeText(mContext, "请插入SD卡", Toast.LENGTH_SHORT).show() ;
					}	
					
				}else{
					Toast.makeText(mContext, "请检查网络", Toast.LENGTH_SHORT).show() ;
				}
			}
		}) ;
        
        //下载点击事件 
		mHolder.download.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(ConnNetWork.isWiFiOnly(mContext)==ConnNetWork.WIFI_ON){
					
					goToDownload() ;
				
				}else if(ConnNetWork.isWiFiOnly(mContext)==ConnNetWork.WIFI_OFF){
					DialogClass.showAlertDialog(mContext, "下载提示", "仅WLAN设置下只能通过WLAN下载，是否开启WLAN?","设置","取消",
						new View.OnClickListener(){

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								DialogClass.CancelDialog() ;
								mContext.startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS)) ;
							}
						
					},new View.OnClickListener(){

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							DialogClass.CancelDialog() ;
						}
						
					} ) ;
				}else if(ConnNetWork.isWiFiOnly(mContext)==ConnNetWork.MOBILE_ON){
					DialogClass.showAlertDialog(mContext, "下载提示", "由于升级包较大，建议使用WIFI下载，是否继续？","继续下载" ,"设置网络",new View.OnClickListener(){

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							DialogClass.CancelDialog() ;
							goToDownload() ;
						}}, new View.OnClickListener(){

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								DialogClass.CancelDialog() ;
								mContext.startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS)) ;
							}}) ;
				}else
					Toast.makeText(mContext, "请检查网络", Toast.LENGTH_SHORT).show() ;
				
			}
		}) ;
        
        //暂停点击事件 
		mHolder.Pause.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mHolder.download.setVisibility(Button.VISIBLE) ;
				mHolder.mPauseButtonLayout.setVisibility(ViewGroup.INVISIBLE) ;
				Intent mPauseDownload = new Intent(Constants.DOWNLOAD_PAUSE) ;
				mContext.sendBroadcast(mPauseDownload) ;
				
			}
		}) ;
        
        //升级点击事件
		mHolder.buttonUpdate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DialogClass.showAlertDialog(mContext, "升级提示", "升级前会检验升级包，验证通过后会自动重启升级.", "确定","取消",new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						DialogClass.CancelDialog() ;
						mHolder.mProgressBarLayout.setVisibility(View.GONE) ;
						UpdatePackage mUpdatePackage = new UpdatePackage(mContext) ;
						mUpdatePackage.Installpackage(OnLineUpdateViewContent.this) ;

					}
				}, new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						DialogClass.CancelDialog() ;
					}
				}) ;
			}
		}) ;
        
        //停止点击事件
		mHolder.Stop.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DialogClass.showAlertDialog(mContext, "取消下载", "本操作将会删除已经下载的文件", "确定","取消",new View.OnClickListener(){

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent mStopDownload = new Intent(Constants.DOWNLOAD_STOP) ;
						mContext.sendBroadcast(mStopDownload) ;
						DialogClass.CancelDialog() ;
						((OtaActivity)mContext).finish() ;
					}
						

				}, new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						DialogClass.CancelDialog() ;
					}
				}) ;
			}
		}) ;
		
	}

	
	
	private void goToDownload(){
		if(GetSDStatus.isSdExist()){
			long completeFileSize = 0 ;
			File updateFile = new File(Constants.FilePath+Constants.UpdateZipPackageFileName) ;
			if(updateFile.exists()) completeFileSize = updateFile.length() ;
			float AvailableBlocks = GetSDStatus.getSdAvailableBlocks() ;
			float needBlocks = (fileSize-completeFileSize)/1024+50000 ;
			if(AvailableBlocks>=needBlocks){
				mHolder.download.setVisibility(Button.INVISIBLE);
				mHolder.mPauseButtonLayout.setVisibility(ViewGroup.VISIBLE) ;
				mHolder.mProgressBarLayout.setVisibility(View.VISIBLE) ;
//				Bundle mBundle = new Bundle() ;
//				mBundle.putString(Constants.Download_Start_Bundle_key_url, downloadUrl) ;
//				mBundle.putString(Constants.Download_Start_Bundle_key_md5, md5) ;
//				mBundle.putLong(Constants.Download_Start_Bundle_key_pkgSize, fileSize) ;
//				mStartDownload.putExtra(Constants.Download_Start_Bundle_Name , mBundle) ;
				
				Intent mStartDownload = new Intent(Constants.DOWNLOAD_START) ;
				mContext.sendBroadcast(mStartDownload) ;
			}else
				Toast.makeText(mContext, "SD卡空间不足，请至少释放"+(needBlocks-AvailableBlocks)+"KB的空间", Toast.LENGTH_SHORT).show() ;
		}else
			Toast.makeText(mContext, "请插入SD卡", Toast.LENGTH_SHORT).show() ;
	}
	

	
	public void checkDownloadStatus(int status){
		String updateinfo , targetVersion  = "" ;
		long packagesize = 0 ;
		switch(status){
		case START :
			mHolder.check.setVisibility(Button.INVISIBLE) ;
			//download.setVisibility(Button.INVISIBLE) ;
			mHolder.mPauseButtonLayout.setVisibility(ViewGroup.VISIBLE) ;
			updateinfo = sp.getString(Constants.UpdateInfo, "") ;
			targetVersion = sp.getString(Constants.TargetVer, "") ;
			packagesize = sp.getLong(Constants.FileSize, 0) ;
			mHolder.scrollview.setVisibility(View.VISIBLE) ;
			mHolder.textTargetVer.setText(targetVersion) ;
			mHolder.textPkgSize.setText("("+packagesize/1024+"KB"+")") ;
			mHolder.textDesc_info.setText(updateinfo) ;
			break;
		case DOWNLOADING :
			mHolder.mProgressBarLayout.setVisibility(View.VISIBLE) ;
			mHolder.check.setVisibility(Button.INVISIBLE) ;
			//download.setVisibility(Button.INVISIBLE) ;
			mHolder.mPauseButtonLayout.setVisibility(ViewGroup.VISIBLE) ;
			updateinfo = sp.getString(Constants.UpdateInfo, "") ;
			targetVersion = sp.getString(Constants.TargetVer, "") ;
			packagesize = sp.getLong(Constants.FileSize, 0) ;
			mHolder.scrollview.setVisibility(View.VISIBLE) ;
			mHolder.textTargetVer.setText(targetVersion) ;
			mHolder.textPkgSize.setText("("+packagesize/1024+"KB"+")") ;
			mHolder.textDesc_info.setText(updateinfo) ;
			break;
		case COMPLETE :
			mHolder.mPauseButtonLayout.setVisibility(ViewGroup.INVISIBLE) ;
			mHolder.buttonUpdate.setVisibility(Button.VISIBLE) ;
			updateinfo = sp.getString(Constants.UpdateInfo, "") ;
			targetVersion = sp.getString(Constants.TargetVer, "") ;
			packagesize = sp.getLong(Constants.FileSize, 0) ;
			mHolder.scrollview.setVisibility(View.VISIBLE) ;
			mHolder.textTargetVer.setText(targetVersion) ;
			mHolder.textPkgSize.setText("("+packagesize/1024+"KB"+")") ;
			mHolder.textDesc_info.setText(updateinfo) ;
			break;
		case PAUSE :
			mHolder.mProgressBarLayout.setVisibility(View.VISIBLE) ;
			mHolder.check.setVisibility(Button.INVISIBLE) ;
			//Pause.setVisibility(Button.INVISIBLE) ;
			mHolder.download.setVisibility(Button.VISIBLE) ;
			updateinfo = sp.getString(Constants.UpdateInfo, "") ;
			targetVersion = sp.getString(Constants.TargetVer, "") ;
			packagesize = sp.getLong(Constants.FileSize, 0) ;
			mHolder.scrollview.setVisibility(View.VISIBLE) ;
			mHolder.textTargetVer.setText(targetVersion) ;
			mHolder.textPkgSize.setText("("+packagesize/1024+"KB"+")") ;
			mHolder.textDesc_info.setText(updateinfo) ;
			break;
		case NOTWORK :

			break;
		}
	}
	
	
	
	public Handler getOnlineUpdateHandler(){
		return mHanlder ;
	}
	
	public Handler mHanlder = new Handler() {
		
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case update:
				DialogClass.cancelProgressDialog_circle();
				mHolder.check.setVisibility(Button.INVISIBLE);
				mHolder.download.setVisibility(Button.VISIBLE);
				mHolder.scrollview.setVisibility(View.VISIBLE) ;
				mHolder.textTargetVer.setText(targetVer) ;
				mHolder.textPkgSize.setText("("+fileSize/1024+"KB"+")") ;
				mHolder.textDesc_info.setText(UpdateMessage);
				//fileSize = Long.valueOf(packageSize);
				break;
			case install_NotDload:
				DialogClass.cancelProgressDialog_circle();
				Toast.makeText(mContext, "最新的升级包已经存在，可直接升级.", Toast.LENGTH_SHORT).show() ;
				mHolder.scrollview.setVisibility(View.VISIBLE) ;
				mHolder.textTargetVer.setText(targetVer) ;
				mHolder.textPkgSize.setText("("+fileSize/1024+"KB"+")") ;
				mHolder.textDesc_info.setText(UpdateMessage);
				mHolder.check.setVisibility(Button.INVISIBLE);
				mHolder.download.setVisibility(Button.INVISIBLE);
				mHolder.mPauseButtonLayout.setVisibility(ViewGroup.INVISIBLE) ;
				mHolder.buttonUpdate.setVisibility(Button.VISIBLE);
				break;
			case Unupdate:
				DialogClass.cancelProgressDialog_circle();
				mHolder.scrollview.setVisibility(View.VISIBLE) ;
				mHolder.textDesc_info.setText((Integer) msg.obj);
				break;
			case DloadComplete:
				String md5 = "" ;
				md5 = (String)msg.obj ;
				String filelocal = Constants.FilePath + Constants.UpdateZipPackageFileName ;
				new PkgCheckMd5().execute(filelocal,md5) ;
				break;
			case DloadFailure:
				Toast.makeText(mContext, "升级包下载失败", Toast.LENGTH_SHORT).show() ;
				mHolder.check.setVisibility(Button.INVISIBLE);
				mHolder.mPauseButtonLayout.setVisibility(ViewGroup.INVISIBLE) ;
				mHolder.download.setVisibility(Button.VISIBLE);
				
				//发送下载失败报告
				String ErrorCode = "1" ;
				String ErrorMsg = "downloadfail" ;
				SendUpdateReport mSendUpdateReport = new SendUpdateReport(mContext) ;
				mSendUpdateReport.UpdateReport(ErrorCode,ErrorMsg,curFirmwareVer,curFirmwareVer,targetVer,IMEI,deviceModel) ; 
				break;
				
			case progressTag : 
				long CompleteLength = (Long)msg.obj ;
				mHolder.mPgbText.setText(CompleteLength/1024+"kb"+"/"+fileSize/1024+"kb") ;
				break ;
			}
		}
	};	
	
	/**
	 * 通过MD5判断升级包是否有效
	 * @param md5 该值是在服务器获取的MD5的值，是对比时的参照值
	 */
	
	class PkgCheckMd5 extends AsyncTask<String, Void, String>{
		String mMd5 = "" ;
		
		
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			DialogClass.showProgressDialog(mContext, "正在校验下载的完整度...") ;
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			mMd5 = params[1] ;
			File UpdPackage = new File(params[0]) ;
			String md5sumStr = "" ;
			try {
				md5sumStr = MD5Sum.getFileMD5Sum(UpdPackage).toUpperCase();
				Log.d(Tag, "AsycTask comput md5 ="+md5sumStr) ;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return md5sumStr;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			DialogClass.cancelProgressDialog_circle() ;
			if(result.equalsIgnoreCase(mMd5)){
				mHolder.mProgressBarLayout.setVisibility(View.GONE) ;
				mHolder.check.setVisibility(Button.INVISIBLE);
				mHolder.download.setVisibility(Button.INVISIBLE);
				mHolder.mPauseButtonLayout.setVisibility(ViewGroup.INVISIBLE) ;
				mHolder.buttonUpdate.setVisibility(Button.VISIBLE);
				//发送下载完成报告
				String ErrorCode = "0" ;
				SendUpdateReport mSendUpdateReport = new SendUpdateReport(mContext) ;
				mSendUpdateReport.DownloadReport(ErrorCode,"",curFirmwareVer,targetVer,IMEI,deviceModel) ; 
			}else{
				//在这里处理MD5检测失败的情况
				String filelocal = Constants.FilePath + Constants.UpdateZipPackageFileName ;
				File PkgFile = new File(filelocal) ;
				if(PkgFile.exists()) 
					PkgFile.delete() ;
				mHolder.mProgressBarLayout.setVisibility(View.GONE) ;
				mHolder.check.setVisibility(Button.VISIBLE);
				mHolder.download.setVisibility(Button.INVISIBLE);
				mHolder.mPauseButtonLayout.setVisibility(ViewGroup.INVISIBLE) ;
				mHolder.buttonUpdate.setVisibility(Button.INVISIBLE);
				Toast.makeText(mContext, "下载包出现问题，请重试.", Toast.LENGTH_SHORT).show() ;
				
			}
		}
		
	}

	
	/**
	 * 对升级信息进行排版
	 */
	
	private String TypeSetting(String info){
		String newInfo = "" ;
		if(info!=null)
			newInfo = info.replaceAll("！！", "\n").replaceAll("。", "。\n\n").replaceAll("；", "；\n\n") ;
		return "\n" + newInfo ;
	}

	
	
	@Override
	public void shortageBattery(int batteryLevel) {
		// TODO Auto-generated method stub
		Toast.makeText(mContext, "电量不足，现在的现在的电量是："+batteryLevel, Toast.LENGTH_SHORT).show() ;
	}
	


	@Override
	public void NeedUpdate(Map<String,String> updateMessage ,
			String packageSize ,String md5,String downloadUrl,
			String targetVer) {
		// TODO Auto-generated method stub
		this.targetVer = targetVer ;
		this.packageSize = packageSize ;
		this.downloadUrl = downloadUrl ;
		this.md5 = md5 ;
		String curLocale = mContext.getResources().getConfiguration().locale.getLanguage();
		//临时代码  规避服务器语言缩写不同意的问题
		if(curLocale.equals("zh"))
			curLocale = mContext.getResources().getConfiguration().locale.getCountry().toLowerCase();
		if(updateMessage.containsKey(curLocale)){
			UpdateMessage = TypeSetting(updateMessage.get(curLocale)) ;
		}else{
			Collection<String> values = updateMessage.values() ;
			if(!values.isEmpty()){
				UpdateMessage = TypeSetting((String)values.toArray()[0]) ;
			}
				
		}
			
		try{
			
			fileSize = Long.valueOf(packageSize);
		}catch(NumberFormatException e){
			e.printStackTrace() ;
		}
		String filelocal = Constants.FilePath + Constants.UpdateZipPackageFileName ;
		File UpdPackage = new File(filelocal) ;
		String oldMd5 = sp.getString(Constants.SP_MD5, "") ;
		boolean isAmount = false ;
		if(UpdPackage.exists()){
			isAmount = (oldMd5.equals(md5))&&(UpdPackage.length()==fileSize)?true:false ;
		}
		if(isAmount){
			Message msg = mHanlder.obtainMessage(install_NotDload) ;
			mHanlder.sendMessage(msg) ;
			return ;
		}
		if(!oldMd5.equals(md5)){
			sp.edit().putString(Constants.UpdateInfo, UpdateMessage)
			 .putBoolean(Constants.SP_Tag_UpInfoIsNew, true) 
	         .putString(Constants.TargetVer, targetVer)
			 .putLong(Constants.FileSize, fileSize)
			 .putString(Constants.DownloadUrl ,downloadUrl)
			 .putString(Constants.SP_MD5, md5)
			 .commit() ; 
		}
		Message msg = mHanlder.obtainMessage(update) ;
		mHanlder.sendMessage(msg) ;

		
	}

	@Override
	public void NotUpdate(int arg0) {
		// TODO Auto-generated method stub
		int UpdateMessage = R.string.updateisnew ;
		if(arg0==CheckUpdate.UpdateInfoDownloadError){
			UpdateMessage = R.string.UpdateInfoDownloadError ;
		}
		Message msg = mHanlder.obtainMessage(Unupdate, UpdateMessage) ;
		mHanlder.sendMessage(msg) ;
	}
	
	@Override
	public boolean checkNewestFirmwarePackageVer(String name) {
		// TODO Auto-generated method stub
		return true;
	}
	
	
    class ViewHolder {
    	public Button check = null ;
    	public Button download = null ;
    	public Button buttonUpdate = null ;
    	public Button Pause = null ;
    	public Button Stop = null ;
    	public LinearLayout mPauseButtonLayout = null ;
    	public LinearLayout mProgressBarLayout = null ;
    	public ProgressBar mProgressBar = null ;
    	public TextView mPgbText = null ;
    	public TextView textDesc_info = null ;
    	public TextView textTargetVer = null ;
    	public TextView textPkgSize = null ;
    	public ScrollView scrollview = null ;
    }


	@Override
	public void progress(int progress, long completesize) {
		Log.d(Tag, progress+"") ;
		// TODO Auto-generated method stub
		mHolder.mProgressBar.setProgress(progress) ;
		Message msg = mHanlder.obtainMessage(progressTag,completesize) ;
		mHanlder.sendMessage(msg) ;
	}

}
