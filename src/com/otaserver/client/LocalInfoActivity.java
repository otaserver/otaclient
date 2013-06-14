package com.otaserver.client;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.otaserver.client.R;
import com.otaserver.client.sdk.MobileInfo;
import com.otaserver.client.customwidget.SdUpdatePromptView;
import com.otaserver.client.utils.CheckRoot;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
//本机信息
public class LocalInfoActivity extends Activity implements OnClickListener{
	private static final int handler_root = 0 ; 
//	private ListView mListView = null ;
	private TextView root_text = null ;
	private TextView sdSize_text = null ;
	private TextView battery_text = null ;
	private ImageView back = null ;
//	private String[] Array_key = null ;
	private String[] Array_info = null ;
	private float sdavailsize = 0 ;
	private boolean isUser = true ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.localinfo) ;
		
		getData() ;
		initWidget() ;
		
//		setAdapter(mListView) ;
		sdSize_text.setText("SD卡可用："+sdavailsize+"MB") ;
		
		

	}
	
	Handler mhandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
			case handler_root:
				if(isUser)
					root_text.setText("ROOT权限：已授权") ;
					
				else
					root_text.setText("ROOT权限：未授权") ;
				break ;
			}
		}
		
	};
	
	private void initWidget(){
		back = (ImageView)findViewById(R.id.localinfo_back) ;
		back.setOnClickListener(this) ;
		SdUpdatePromptView promptview = (SdUpdatePromptView)findViewById(R.id.localinfocontent) ;
		promptview.mGoDraw(Array_info) ;
		root_text = (TextView)findViewById(R.id.root_text) ;
		sdSize_text = (TextView)findViewById(R.id.sdcard_size_text) ;
		battery_text = (TextView)findViewById(R.id.battery_text) ;
	}
	
	private void getData(){
		//获取本机信息
		setinfo() ;
		//获取电量值
		registerReceiver(mBatteryReceiver, new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED));
		
		//获取是否是root
		new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				CheckRoot cr = new CheckRoot() ;
				isUser = cr.getRootAhth();
				mhandler.sendEmptyMessage(handler_root) ;
			}
			
		}).start() ;
		
		//获取SD卡可用空间
		sdavailsize = GetSDStatus.getSdAvailableBlocks()/1024 ;
		
		//小数点后取两位，四舍五入
		BigDecimal bd = new BigDecimal(sdavailsize);
		bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
		sdavailsize = bd.floatValue() ;
	}
	
	/**
	 * 监听当前电量的广播
	 */
	private BroadcastReceiver mBatteryReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
				int Level = intent.getIntExtra("level", 0);
				int Scale = intent.getIntExtra("scale", 100);
				onBatteryInfoReceiver(Level, Scale);
			}
		}
	};

	/**
	 * 得到电量值，判断是否升级
	 * @param intLevel
	 * @param intScale
	 */
	private void onBatteryInfoReceiver(int intLevel, int intScale) {
		int curbatteryLevel = (intLevel * 100) / intScale;
		battery_text.setText("电池信息："+curbatteryLevel+"%") ;
		//注销监听电量广播
		unregisterReceiver(mBatteryReceiver) ;

	}
	
	
//	private void setAdapter(ListView list){
//		List<Map<String,String>> data = new ArrayList<Map<String,String>>() ;
//		setData(data) ;
//		SimpleAdapter mSimpleAdapter = new SimpleAdapter(this, data, R.layout.localinfolistviewitem,
//														new String[]{"title","value"}, new int[]{R.id.item_key,R.id.item_value}) ;
//		list.setAdapter(mSimpleAdapter) ;
//	}
	
//	private void setData(List<Map<String,String>> data){
//		setkey() ;
//		setValue() ;
//		Map<String,String> data_map = null ;
//		for(int i=0;i<Array_key.length;i++){
//			data_map = new HashMap<String, String>();
//			data_map.put("title", Array_key[i]) ;
//			data_map.put("value", Array_value[i]) ;
//			data.add(data_map) ;
//		}
//		
//	}
	
//	private void setkey(){
//
//		Array_key = new String[]{IMEI_Key,
//								 FirmwareType_Key,
//								 LocaLanguage_Key,
//								 DeviceModel_Key} ;
//	}
	
	private void setinfo(){
		String IMEI_Key = "IMEI号:" ;
		String FirmwareType_Key = "当前版本号:" ;
		String LocaLanguage_Key = "当前手机语言:" ;
		String DeviceModel_Key = "手机型号:" ;
		MobileInfo mMobileInfo = new MobileInfo(this) ;
		String IMEI_Value = mMobileInfo.getImeiNumber() ;
		String FirmwareType_Value = mMobileInfo.getFirmwareType() ;
		String LocaLanguage_Value = getLanguage() ;
		String DeviceModel_Value = mMobileInfo.getDeviceModel() ;
		Array_info = new String[]{IMEI_Key+IMEI_Value,
								   FirmwareType_Key+FirmwareType_Value,
								   LocaLanguage_Key+LocaLanguage_Value,
								   DeviceModel_Key+DeviceModel_Value} ;
	}
	
	private String getLanguage(){
		String langStr = Locale.getDefault().getCountry();
		String LanguageAbbr = null;
		if (langStr.equals("US")) {
			LanguageAbbr = "English";
		} else if (langStr.equals("CN")) {
			LanguageAbbr = "中文简体";
		}else if (langStr.equals("TW")) {
			LanguageAbbr = "中文繁体";
		}
		return LanguageAbbr;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.localinfo_back :
			finish() ;
			break ;
		}
	}
	
}
