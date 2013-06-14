package com.otaserver.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EncodingUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.otaserver.client.utils.Constants;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class UpdateApk {
	private static final String Tag = "updateApk" ;
	private static final int ConnectError = 2 ;
	private static final int DownloadAPK = 1 ;
	private static final int DownloadAPK_ConfigText = 0 ;
	private String appPackageName = null ;
	private String downloadUrl = null ;
	private int appVersionName = 0 ;
	private int apksize = 0 ;
	private int targetVersionCode = 0 ;
	private Context context = null ;
	private int Timeout = 5000 ;
	private int progress_value = 0 ;
	private boolean tag_downloadAPK = false ;
	private boolean isShowDialog = false ;
	
	public UpdateApk(Context context,boolean isShowDialog){
		this.context = context ;
		this.isShowDialog = isShowDialog ;
	}
	
	
	public void checkVersion() {
		if(isShowDialog)
			DialogClass.showProgressDialog(context,"正在检查，请稍后") ;
		File mPathconfigFile = new File(Constants.DOWNLOAD_APK_CONFIGFILE_SAVE_PATH) ;
		if(!mPathconfigFile.exists())
			mPathconfigFile.mkdirs() ;
		
		File configFile = new File(Constants.DOWNLOAD_APK_CONFIGFILE_SAVE_PATH
				+ Constants.DOWNLOAD_APK_CONFIGFILE_NAME) ;
		if(configFile.exists()){
			configFile.delete() ;
		}
		downloadFileThread mDownloadFileThread = new downloadFileThread(
				Constants.DOWNLOAD_APK_CONFIGFILE_URL,
				Constants.DOWNLOAD_APK_CONFIGFILE_SAVE_PATH
						+ Constants.DOWNLOAD_APK_CONFIGFILE_NAME);
		mDownloadFileThread.start() ;
	}
	
	
	/**
	 * Handler
	 */
	Handler mHandler = new Handler() {

		@SuppressLint("ParserError")
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case DownloadAPK_ConfigText:
				String jsonContent = (String)msg.obj ;
				jsonParser(jsonContent);
				if (targetVersionCode > getCurVersionCode()) {
					DialogClass.cancelProgressDialog_circle() ;
					DialogClass.showAlertDialog(context, "版本更新", "有新的版本是否下载更新","确定","取消",
							new View.OnClickListener() {

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									
									File mPathApkFile = new File(Constants.DOWNLOAD_APK_SAVE_PATH) ;
									if(!mPathApkFile.exists())
										mPathApkFile.mkdirs() ;
									
									
									File apkFile = new File(
											Constants.DOWNLOAD_APK_SAVE_PATH
													+ Constants.DOWNLOAD_APK_NAME);
									if (apkFile.exists()) {
										apkFile.delete();
									}
									tag_downloadAPK = true ;
									downloadFileThread mDownloadFileThread = new downloadFileThread(
											downloadUrl,
											Constants.DOWNLOAD_APK_SAVE_PATH
													+ Constants.DOWNLOAD_APK_NAME);
									mDownloadFileThread.start();
									DialogClass.CancelDialog();
									DialogClass.showProgressDialog_h(context, "正在下载") ;
								}

							}, new View.OnClickListener() {

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									if(isShowDialog){
										DialogClass.CancelDialog();
									}else{
										((OtaActivity)context).finish() ;
									}
										
								}
							});
				} else {
					DialogClass.cancelProgressDialog_circle();
					if(isShowDialog)
						Toast.makeText(context, "您当前的版本已是最新.", Toast.LENGTH_SHORT).show() ;
				}
				break;
			case DownloadAPK:
				DialogClass.cancelProgressDialog_h() ;
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(
						Uri.fromFile(new File(Constants.DOWNLOAD_APK_SAVE_PATH
								+ Constants.DOWNLOAD_APK_NAME)),
						"application/vnd.android.package-archive");
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK) ;
				context.startActivity(intent);

				break;
			case ConnectError :
				if(tag_downloadAPK){
					DialogClass.cancelProgressDialog_h() ;
					Toast.makeText(context, "下载失败", Toast.LENGTH_SHORT).show() ;
				}
				else{
					
					DialogClass.cancelProgressDialog_circle() ;
					if(isShowDialog)
						Toast.makeText(context, "连接失败，请重试.", Toast.LENGTH_SHORT).show() ;
				}
				break;
			}
		}
		
	} ;
	
	/**
	 * 得到现在APK的版本号
	 * @return 现在APK的版本号
	 */
	private int getCurVersionCode() {
		PackageManager pm = context.getPackageManager();
		int versioncode = 0 ;
		try {
			PackageInfo mPackageInfo = pm.getPackageInfo(
					context.getPackageName(), 0);
			versioncode = mPackageInfo.versionCode ;
			Log.d(Tag, "CurVersioncode = " + versioncode) ;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return versioncode ;
		
	}
	
	
	
	/**
	 * 下载版本配置文档
	 */
	public class downloadFileThread extends Thread{
		private String URL = null ;
		private String Path = null ;
		public downloadFileThread(String URL , String Path){
			this.URL = URL ;
			this.Path = Path ;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			HttpClient client = new DefaultHttpClient() ;
			client.getParams().setParameter(Constants.http_socket_timeout,
					Timeout);

			client.getParams().setParameter(Constants.http_connection_timeout,
					Timeout);
			HttpGet get = new HttpGet(URL) ;
			InputStream in = null ;
			FileOutputStream out = null ;
			try {
				HttpResponse response = client.execute(get) ;
				if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
					in = response.getEntity().getContent() ;
					
					if(tag_downloadAPK){
						File file = new File(Path) ;
						out = new FileOutputStream(file) ;
						byte[] buffer = new byte[1024] ;
						int line = 0 ;
						int completedsize = 0 ;
						while((line=in.read(buffer, 0, buffer.length))!=-1){
							out.write(buffer, 0, line) ;
							
							completedsize += line ;
							progress_value = completedsize*100/apksize ;
							DialogClass.setProgress(progress_value) ;
	
						}
						
						mHandler.sendEmptyMessage(DownloadAPK) ;
						
					}else{
						BufferedReader br = new BufferedReader(new InputStreamReader(in)) ;
						StringBuffer buffer = new StringBuffer(50) ;
						String temp = "" ;					
						while((temp = br.readLine())!=null){
							buffer.append(temp) ;
						}
						Log.d(Tag, buffer.toString()) ;
						Message msg = mHandler.obtainMessage(DownloadAPK_ConfigText, buffer.toString()) ;
						mHandler.sendMessage(msg) ;
					}
					

				}else{
					mHandler.sendEmptyMessage(ConnectError) ;
				}
			}catch(Exception e){
				e.printStackTrace() ;
				mHandler.sendEmptyMessage(ConnectError) ;
			}
			finally{
				try {
					in.close() ;
					out.close() ;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	private void jsonParser(String json){
		
		try {
			JSONObject mJSONObject = new JSONObject(json) ;
			appPackageName = mJSONObject.getString(Constants.JSON_PACKAGENAME) ;
			targetVersionCode = mJSONObject.getInt(Constants.JSON_VERSIONCODE) ;
			downloadUrl = mJSONObject.getString(Constants.JSON_DOWNLOADURL) ;
			appVersionName = mJSONObject.getInt(Constants.JSON_VERSIONCODE) ;
			apksize = mJSONObject.getInt(Constants.JSON_APKSIZE) ;
			Log.d(Tag, "appPackageName = " +appPackageName) ;
			Log.d(Tag, "appVersionName = " +appVersionName) ;
			Log.d(Tag, "targetVersionCode = " +targetVersionCode) ;
			Log.d(Tag, "apksize = " +apksize) ;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
//	private String readJson(){
//		File jsonFile = new File(Constants.DOWNLOAD_APK_CONFIGFILE_SAVE_PATH+Constants.DOWNLOAD_APK_CONFIGFILE_NAME) ;
//		String jsonString = null ;
//		if(jsonFile.exists()){
//			try{
//				FileInputStream outJson = new FileInputStream(jsonFile) ; 
//				int length = outJson.available() ;
//				byte[] buffer = new byte[512] ;
//				outJson.read(buffer) ;
//				jsonString = EncodingUtils.getString(buffer, 0, length, "UTF-8") ;
//				return jsonString ;
//			}catch(IOException e){
//				e.printStackTrace() ;
//			}
//		}
//		return "" ;
//	}

		
}
