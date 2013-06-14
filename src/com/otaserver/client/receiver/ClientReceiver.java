package com.otaserver.client.receiver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Map;
import com.otaserver.client.sdk.CheckUpdate;
import com.otaserver.client.sdk.CheckUpdate.checkUpdateInter;
import com.otaserver.client.DialogClass;
import com.otaserver.client.GetSDStatus;
import com.otaserver.client.utils.ConnNetWork;
import com.otaserver.client.utils.Constants;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ClientReceiver extends BroadcastReceiver implements checkUpdateInter{
	private static final String Tag = "ClientReceiver" ;
	private static final String Push_Notify = "Push_Notify" ;
	private SharedPreferences sp = null ;
	private static final int Push = 0 ;
	private static final int Reminder =1 ;
	private static final long ReminderOneDay = 1000*60*60*24 ;
	private Context context = null ;
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
    	this.context = context ;
        if (intent.getAction() != null) {
        	sp = context.getSharedPreferences(Constants.SP_Settings, Context.MODE_PRIVATE) ;
        	String actionName = intent.getAction();
        	Log.d(Tag,"actionName="+ actionName);
        	if(actionName.equals(Constants.Push_SID)) {
        		boolean pushSwitchOn = sp.getBoolean(Constants.SP_Settings_PushOnOff, false) ;
        		if(pushSwitchOn){
        			String body = intent.getStringExtra("body").trim();
        			Log.d(Tag,"body="+ body);
        			Bundle mBundle = new Bundle() ;
        			mBundle.putString("body", body) ;
        			Message msg = mHandler.obtainMessage(Push, mBundle) ;
        			mHandler.sendMessage(msg) ;
        		}
        	}else if(actionName.equals(Constants.RemindAction)){
        		boolean reminderSwitchOn = sp.getBoolean(Constants.SP_Settings_ReminderOnOff, false) ;
        		if(reminderSwitchOn&&ConnNetWork.isConnNetWork(context)&&compareTime(context)){
        			Log.d(Tag,"network is connect,start to check...");
        			if(GetSDStatus.isSdExist()){
        				CheckUpdate mCheckUpdate = new CheckUpdate(context,Constants.FilePath) ;
        				mCheckUpdate.recordCheckTime(Constants.TimeRecordFilePath,Constants.TimeRecordFileName) ;
        				mCheckUpdate.StartCheck(ClientReceiver.this) ;
        			}
        				
        		}
        	}
        	
        } 
    }
        			
    
    //Compare last query time’s absolute value
    private boolean compareTime(Context context){
    	SharedPreferences sp = context.getSharedPreferences(Constants.SP_Settings, Context.MODE_PRIVATE) ;
    	int Days = sp.getInt(Constants.TimeForDays, 7) ;
    	Log.d(Tag, "Reminder Days is " + Days) ;
    	long Remindercycle = ReminderOneDay * Days ;
    	long curTime = System.currentTimeMillis() ;
    	File timeFile = new File(Constants.TimeRecordFilePath+Constants.TimeRecordFileName) ; 
    	BufferedReader reader = null ;
    	String timeStr = null ;
    	if(timeFile.exists()){
    		try {
				reader = new BufferedReader(new FileReader(timeFile)) ;
				timeStr = reader.readLine() ;
				long oldTime = Long.valueOf(timeStr) ;
				Log.d(Tag, "Time is " + (curTime-oldTime)) ;
				if((curTime-oldTime)>Remindercycle)
					return true ;
				else
					return false ;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false ;
			}
    	}
    	return true ;
    }
    
    Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			Bundle bundle = (Bundle)msg.obj ;
			switch(msg.what){
			case Push :
				String body = (String)bundle.get("body") ;
				DialogClass.showPushNotify(context,body) ;
				break;
			case Reminder :
				String info = (String)bundle.get("Reminder") ;
				DialogClass.showPushNotify(context,info) ;
				break;
			}
		}
    	
    };
    
    

	@Override
	public boolean checkNewestFirmwarePackageVer(String name) {
		// TODO Auto-generated method stub
		return true;
	}


	@Override
	public void NeedUpdate(Map<String, String> arg0, String arg1, String arg2,
			String arg3, String arg4) {
		// TODO Auto-generated method stub
		Bundle mBundle = new Bundle() ;
		mBundle.putString("Reminder", "你有新版本可以升级啦，去看看吧") ;
		Message msg = mHandler.obtainMessage(Reminder, mBundle) ;
		mHandler.sendMessage(msg) ;	
	}


	@Override
	public void NotUpdate(int arg0) {
		// TODO Auto-generated method stub
		
	}
    



}
