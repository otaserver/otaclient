package com.otaserver.client;

import com.otaserver.client.R;
import com.otaserver.client.customwidget.ReminderLayout;
import com.otaserver.client.customwidget.SettingItemView;
import com.otaserver.client.utils.Constants;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;



public class SettingActivity extends Activity implements OnClickListener{
	private boolean onlywifi_state ;
	private boolean push_state ;
	private boolean reminder_state ;
	private ReminderLayout mReminderLayout = null ;
	private SharedPreferences sp = null ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings) ;
		mReminderLayout = (ReminderLayout)findViewById(R.id.reminderLayout) ;
		sp = getSharedPreferences(Constants.SP_Settings, Activity.MODE_PRIVATE) ;
		onlywifi_state = sp.getBoolean(Constants.SP_Settings_WifiOnly, false) ;
		push_state = sp.getBoolean(Constants.SP_Settings_PushOnOff, false) ;
		reminder_state = sp.getBoolean(Constants.SP_Settings_ReminderOnOff, false) ;
		initBackBtn() ;
		initOnlyWifiItem() ;
		initPushItem() ;
		initReminderItem() ;
		
	}
	
	private void initBackBtn(){
		ImageView back = (ImageView)findViewById(R.id.settings_back) ;
		back.setOnClickListener(this) ;
	}
	
	private void initOnlyWifiItem(){
		final SettingItemView only_wifi = (SettingItemView)findViewById(R.id.setting_onlywifi) ;
		only_wifi.changeSettingState(onlywifi_state) ;
		only_wifi.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(onlywifi_state){
					onlywifi_state = false ;
					sp.edit().putBoolean(Constants.SP_Settings_WifiOnly, onlywifi_state).commit() ;
					only_wifi.changeSettingState(onlywifi_state) ;
				}else{
					onlywifi_state = true ;
					sp.edit().putBoolean(Constants.SP_Settings_WifiOnly, onlywifi_state).commit() ;
					only_wifi.changeSettingState(onlywifi_state) ;
				}
				
			}
		}) ;
	}
	
	private void initPushItem(){
		final SettingItemView push = (SettingItemView)findViewById(R.id.setting_push) ;
		push.changeSettingState(push_state) ;
		push.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(push_state){
					push_state = false ;
					sp.edit().putBoolean(Constants.SP_Settings_PushOnOff, push_state).commit() ;
					push.changeSettingState(push_state) ;
				}else{
					push_state = true ;
					sp.edit().putBoolean(Constants.SP_Settings_PushOnOff, push_state).commit() ;
					push.changeSettingState(push_state) ;
				}
				
			}
		}) ;
	}
	
	private void initReminderItem(){
		final SettingItemView reminder = (SettingItemView)findViewById(R.id.setting_reminder) ;
		reminder.changeSettingState(reminder_state) ;
		reminder.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(reminder_state){
					reminder_state = false ;
					sp.edit().putBoolean(Constants.SP_Settings_ReminderOnOff, reminder_state).commit() ;
					reminder.changeSettingState(reminder_state) ;
					mReminderLayout.ExitEntryView() ;
				}else{
					reminder_state = true ;
					sp.edit().putBoolean(Constants.SP_Settings_ReminderOnOff, reminder_state).commit() ;
					reminder.changeSettingState(reminder_state) ;
					mReminderLayout.startEntryView() ;
				}
				
			}
		}) ;
	}
	

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.settings_back :
			finish() ;
			break ;
		}
	}



	
	
}
