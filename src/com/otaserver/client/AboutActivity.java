package com.otaserver.client;

import com.otaserver.client.R;
import com.otaserver.client.customwidget.AboutPromptView;
import com.otaserver.client.customwidget.SdUpdatePromptView;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

//关于界面
public class AboutActivity extends Activity implements OnClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about) ;
		ImageView back = (ImageView)findViewById(R.id.about_back) ;
		back.setOnClickListener(this) ;
		TextView about_appname = (TextView)findViewById(R.id.about_text_appname) ;
		TextView about_appver = (TextView)findViewById(R.id.about_text_ver) ;
		String[] title_array = new String[]{"在线升级：","本地升级：","升级设置："} ;
		String[] info_array = new String[]{getResources().getString(R.string.about_onlineupt_spec),
										   getResources().getString(R.string.about_sdcardupt_spec),
										   getResources().getString(R.string.about_onlineupt_setting_spec)} ;
																	
		AboutPromptView promptview = (AboutPromptView)findViewById(R.id.aboutPromptView1) ;
		promptview.mGoDraw(title_array,info_array) ;
		PackageManager pm = getPackageManager() ;
		String appName = "" ;
		String VerName = "" ;
		try {
			PackageInfo pi = pm.getPackageInfo(getApplicationContext().getPackageName(), 0);
			VerName = getResources().getString(R.string.appver) + pi.versionName ;
			appName = getResources().getString(R.string.appname) + pi.applicationInfo.loadLabel(getPackageManager()).toString();
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		about_appver.setText(VerName) ;
		about_appname.setText(appName) ;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.about_back :
			finish() ;
			break ;
		}
	}
	
	
}
