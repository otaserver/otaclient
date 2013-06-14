package com.otaserver.client;

import java.io.File;
import com.otaserver.client.R;
import com.otaserver.client.customwidget.SdUpdatePromptView;
import com.otaserver.client.framework.DownloadService;
import com.otaserver.client.framework.UpdatePackage;
import com.otaserver.client.framework.UpdatePackage.batteryListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SdcardUpdate implements OnClickListener,batteryListener{
	private static final String Tag = "sdcardUpdate" ;
	private static final int mBatterylow = 1 ;
	private Context mContext = null ;
	private View mView =null ;
	public TextView mShowPath = null ;
	private DownloadService mDownloadService = null ;


	
	public SdcardUpdate(Context context,View view){
		mContext = context ;
		mView = view ;
	
	
	}
	
	public void startShowView(){
		initWidget() ;
		
	}
	
	private void initWidget(){
		mShowPath = (TextView)mView.findViewById(R.id.sdcard_file_path) ;
		Button btn_file_browser = (Button)mView.findViewById(R.id.sdcard_update_browser_btn) ;
		btn_file_browser.setOnClickListener(this) ;
		Button btn_update = (Button)mView.findViewById(R.id.sdcard_update_btn) ;
		btn_update.setOnClickListener(this) ;
		
		String prompt_one = mContext.getResources().getString(R.string.sdcardupdate_prompt_one) ;
		String prompt_two = mContext.getResources().getString(R.string.sdcardupdate_prompt_two) ;
		String prompt_three = mContext.getResources().getString(R.string.sdcardupdate_prompt_three) ;
		String prompt_four = mContext.getResources().getString(R.string.sdcardupdate_prompt_four) ;
		String[] prompt = new String[]{prompt_one,prompt_two,prompt_three,prompt_four} ;
		SdUpdatePromptView promptview = (SdUpdatePromptView)mView.findViewById(R.id.sdUpdatePromptView1) ;
		promptview.mGoDraw(prompt) ;
	}
	
	public void setDownloadService(DownloadService mDownloadService){
		this.mDownloadService = mDownloadService ;
	}
	

	Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){

			case mBatterylow :
				Toast.makeText(mContext, "电量不足,请及时充电.", Toast.LENGTH_SHORT).show() ;
				break;
			}
			
		}
		
	};


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.sdcard_update_browser_btn :
			if(GetSDStatus.isSdExist()){
				boolean isdownloading = getOnLineUpdStatus()== DownloadService.DOWNLOADING?true:false ;
				if(!isdownloading){
					startFileManagar() ;
				}else{
					Toast.makeText(mContext, "在线升级正在下载ROM中...", Toast.LENGTH_SHORT).show() ;
				}
				
			}else{
				Toast.makeText(mContext, "请插入SDcard", Toast.LENGTH_SHORT).show() ;
			}
			break ;
		case R.id.sdcard_update_btn :
			final String path = mShowPath.getText().toString() ;
			if(ISZipChoose(path)){
				DialogClass.showAlertDialog(mContext, "升级提示", "升级前会检验升级包，验证通过后会自动重启升级.", "确定","取消",new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						DialogClass.CancelDialog() ;
						UpdatePackage mUpdatePackage = new UpdatePackage(mContext,path) ;
						mUpdatePackage.Installpackage(SdcardUpdate.this) ;
					}
				}, new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						DialogClass.CancelDialog() ;
					}
				}) ;
				
			}else{
				Toast.makeText(mContext, "请先选择ROM包.", Toast.LENGTH_SHORT).show() ;
			}
			break ;
		}
	}
	
	private boolean ISZipChoose(String mZipFilePath){
		return mZipFilePath.endsWith(".zip") ;
	}
	
	private int getOnLineUpdStatus(){
		int dstatus = DownloadService.NOTWORK ;
		if(mDownloadService!=null){
			dstatus = mDownloadService.getStatus() ;
		}
		return dstatus ;
	}
	
	private void startFileManagar(){
		Intent intent = new Intent();
		String path = Environment.getExternalStorageDirectory().getPath() ;
		intent.setDataAndType(Uri.fromFile(new File(path)), "*/*");
		intent.setClass(mContext, FileManagarActivity.class);
		((Activity) mContext).startActivityForResult(intent, 1);
	}
	

	@Override
	public void shortageBattery(int batteryLevel) {
		// TODO Auto-generated method stub
		mHandler.sendEmptyMessage(mBatterylow) ;
	}
}