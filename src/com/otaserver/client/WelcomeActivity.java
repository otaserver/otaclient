package com.otaserver.client;

import com.otaserver.client.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Window;
import android.view.WindowManager;

public class WelcomeActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		
		new CountDownTimer(2000,1000) {
			
			@Override
			public void onTick(long millisUntilFinished) {
			}
			@Override
			public void onFinish() {
				Intent intent = new Intent();
				intent.setClass(WelcomeActivity.this, OtaActivity.class);
				startActivity(intent);
				
				int VERSION=Integer.parseInt(android.os.Build.VERSION.SDK);
				if(VERSION >= 5){
					WelcomeActivity.this.overridePendingTransition(R.anim.translate_in, R.anim.translate_out);
				}
				finish();
			}
		}.start();
	}

}
