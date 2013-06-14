package com.otaserver.client;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import com.otaserver.client.R;
import com.otaserver.client.customwidget.SdUpdatePromptView;
import com.otaserver.client.framework.DownloadService;
import com.otaserver.client.framework.DownloadService.MyBinder;
import com.otaserver.client.framework.DownloadService.downloadReceiver;
import com.otaserver.client.utils.ConnNetWork;
import com.otaserver.client.utils.Constants;
import com.otaserver.client.viewflow.FlowIndicator;
import com.otaserver.client.viewflow.ViewFlow;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

public class OtaActivity extends Activity implements OnClickListener,
		FlowIndicator {

	private static final String Tag = "OtaActivity";
	private static final int mOnLineUpdateViewIndex = 0;
	private static final int mSdUpdateViewIndex = 1;
	// private static final int mLocalInfoViewIndex = 2 ;
	private static final int mMoreViewIndex = 2;
	private int status_download = -1;
	private DownloadService mDownloadService = null;
	private downloadReceiver mDownloadReceiver = null;
	private LayoutInflater mInflate = null;
	private ViewFlow viewFlow = null;
	private myBaseAdapter mbaseAdapter = null;
	private OnLineUpdateViewContent mOnLineUpdateViewContent = null;
	private SdcardUpdate mSdcardUpdate = null;
	private LinearLayout mOnLineUpdateTab = null;
	private LinearLayout mSdUpdateTab = null;
	private LinearLayout mMoreTab = null;
	private List<View> mViewList = null;
	private MyBinder binder = null;
	private boolean appIsKilled = false;
	private int DStatus_killing = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
//		Debug.startMethodTracing("trace") ;

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		//activity 被kill后的处理
		if(savedInstanceState!=null){
			appIsKilled = savedInstanceState.getBoolean(Constants.MainActivityIsKilled) ;
			DStatus_killing = savedInstanceState.getInt(Constants.DownloadStatus_Killing) ;
			Log.d(Tag, "appIsKilled = " + appIsKilled) ;
		}


		
		//检查版本升级
		if(ConnNetWork.isConnNetWork(this)){
			UpdateApk mUpdateApk = new UpdateApk(this,false) ;
			mUpdateApk.checkVersion() ;
		}
			
		
		//第一次启动应用时初始化设置的状态
		initSettingsStatus() ;
		
		initTabWidget() ;
		
		initView() ;
		// 定义viewFlow和CircleFlowIndicator
		viewFlow = (ViewFlow) findViewById(R.id.viewflow);
		mbaseAdapter = new myBaseAdapter(this);
		viewFlow.setAdapter(mbaseAdapter, 0);
		viewFlow.setFlowIndicator(this);
		
	}

	private void initView() {
		mViewList = new ArrayList<View>();
		mInflate = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View mOnLineUpdateView = mInflate.inflate(R.layout.onlineupdate, null);
		View mSdcardUpdateView = mInflate.inflate(R.layout.sdcardupdate, null);
		View mMoreView = mInflate.inflate(R.layout.more, null);
		mViewList.add(mOnLineUpdateView);
		mViewList.add(mSdcardUpdateView);
		mViewList.add(mMoreView);

	}

	private void initTabWidget() {
		mOnLineUpdateTab = (LinearLayout) findViewById(R.id.online);
		// mLocalInfoTab = (RelativeLayout)findViewById(R.id.ShowLocalInfo) ;
		mSdUpdateTab = (LinearLayout) findViewById(R.id.SDUpdate);
		mMoreTab = (LinearLayout) findViewById(R.id.More);
		mOnLineUpdateTab.setOnClickListener(this);
		// mLocalInfoTab.setOnClickListener(this) ;
		mSdUpdateTab.setOnClickListener(this);
		mMoreTab.setOnClickListener(this);

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Intent intent = new Intent(this, DownloadService.class);
		bindService(intent, SC, BIND_AUTO_CREATE);

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.d(Tag, "OtaActivity is onStop");
		if (mDownloadService != null) {
			status_download = mDownloadService.getStatus();
			if (status_download == DownloadService.DOWNLOADING) {
				mDownloadService.showNotify();
			}
		}
		mUnRegisterReceiver();
		unbindService(SC);
		mDownloadService = null;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// Debug.stopMethodTracing() ;
		DialogClass.mProgressBar = null;
		DialogClass.progressDialog_circle = null;
		DialogClass.progressDialog_horizontal = null;
		DialogClass.dialog = null;
		DialogClass.pushNotify = null;
		DialogClass.manager = null;
		mViewList = null;
		viewFlow = null;
		mbaseAdapter = null;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub

		outState.putBoolean(Constants.MainActivityIsKilled, true);
		outState.putInt(Constants.DownloadStatus_Killing,
				mDownloadService.getStatus());
		super.onSaveInstanceState(outState);

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		if (savedInstanceState != null) {
			appIsKilled = savedInstanceState
					.getBoolean(Constants.MainActivityIsKilled);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				Uri uri = data.getData();
				String choicePath = "";
				if (uri != null) {
					choicePath = uri.getPath();
					mSdcardUpdate.mShowPath.setText(choicePath);
				}
			}
		}
	}

	// 第一次启动应用时初始应用设置的状态值到SharedPreferences

	private void initSettingsStatus() {
		SharedPreferences mSP = getSharedPreferences(Constants.SP_Settings,
				Activity.MODE_PRIVATE);
		boolean isInit = mSP.getBoolean(Constants.SP_Settings_initSettings,
				true);
		if (isInit) {
			SharedPreferences.Editor editor = mSP.edit();
			editor.putBoolean(Constants.SP_Settings_initSettings, false);
			editor.putBoolean(Constants.SP_Settings_WifiOnly, true);
			editor.putBoolean(Constants.SP_Settings_PushOnOff, false);
			editor.putBoolean(Constants.SP_Settings_ReminderOnOff, false);
			editor.putInt(Constants.TimeForDays, 7);
			editor.commit();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			DialogClass.showAlertDialog(this, "退出提示", "确认退出程序", "确定", "取消",
					new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							DialogClass.CancelDialog();
							sendBroadcast(new Intent(Constants.DOWNLOAD_PAUSE));
							finish();
							//lenovo_dialog.xml-------------------
						}
					}, new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							DialogClass.CancelDialog();
						}
					});
		}

		return super.onKeyDown(keyCode, event);
	}

	ServiceConnection SC = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			binder = (MyBinder) service;
			mDownloadService = binder.getService(mOnLineUpdateViewContent
					.getOnlineUpdateHandler());
			mDownloadService.setProgressInter(mOnLineUpdateViewContent);
			status_download = mDownloadService.getStatus();
			if (status_download == DownloadService.DOWNLOADING) {
				mDownloadService.cancelNotifyD();
			}
			mRegisterReceiver();

			if (!appIsKilled) {
				mOnLineUpdateViewContent.checkDownloadStatus(status_download);
			} else {
				appIsKilled = false;
				if (DStatus_killing == DownloadService.DOWNLOADING)
					mOnLineUpdateViewContent
							.checkDownloadStatus(DownloadService.PAUSE);
			}

			mSdcardUpdate.setDownloadService(mDownloadService);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			Log.d(Tag, "Service error stop!!!");
		}

	};

	/**
	 * 注册download广播
	 */

	private void mRegisterReceiver() {
		// mDownloadService = new DownloadService() ;
		mDownloadReceiver = mDownloadService.new downloadReceiver();
		IntentFilter filter_start = new IntentFilter();
		filter_start.addAction(Constants.DOWNLOAD_START);
		this.registerReceiver(mDownloadReceiver, filter_start);

		IntentFilter filter_pause = new IntentFilter();
		filter_pause.addAction(Constants.DOWNLOAD_PAUSE);
		this.registerReceiver(mDownloadReceiver, filter_pause);

		IntentFilter filter_stop = new IntentFilter();
		filter_stop.addAction(Constants.DOWNLOAD_STOP);
		this.registerReceiver(mDownloadReceiver, filter_stop);

	}

	/**
	 * 注销download广播
	 */

	private void mUnRegisterReceiver() {
		this.unregisterReceiver(mDownloadReceiver);
	}

	// BaseAdapter
	class myBaseAdapter extends BaseAdapter {
		private Context con = null;
		private final int OnLineView = 0;
		private final int SdcardUpdateView = 1;
		private final int MoreView = 2;

		public myBaseAdapter(Context context) {
			con = context;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mViewList.size();
		}

		@Override
		public int getItemViewType(int position) {
			return position;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			int viewType = getItemViewType(position);

			if (convertView == null) {
				switch (viewType) {
				case OnLineView:
					Log.d("jiang", "new  convertView");
					convertView = mViewList.get(mOnLineUpdateViewIndex);
					mOnLineUpdateViewContent = new OnLineUpdateViewContent(con,
							convertView);
					mOnLineUpdateViewContent.startShowView();
					break;

				case SdcardUpdateView:
					convertView = mViewList.get(mSdUpdateViewIndex);
					mSdcardUpdate = new SdcardUpdate(con, convertView);
					mSdcardUpdate.startShowView();
					break;

				case MoreView:
					convertView = mViewList.get(mMoreViewIndex);
					new more(OtaActivity.this, convertView).startShowView();
					break;
				}

			} else {
				Log.d("jiang", "redo convertView");
				switch (viewType) {
				case OnLineView:
					convertView = mViewList.get(mOnLineUpdateViewIndex);
					break;

				case SdcardUpdateView:
					convertView = mViewList.get(mSdUpdateViewIndex);
					break;

				// case LocalInfoView:
				// convertView = mViewList.get(mLocalInfoViewIndex);
				// break;

				case MoreView:
					convertView = mViewList.get(mMoreViewIndex);
					break;
				}
			}

			return convertView;
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {

		case R.id.online:
			viewFlow.setAdapter(mbaseAdapter, mOnLineUpdateViewIndex);
			break;
		case R.id.SDUpdate:
			viewFlow.setAdapter(mbaseAdapter, mSdUpdateViewIndex);
			break;
		case R.id.More:
			viewFlow.setAdapter(mbaseAdapter, mMoreViewIndex);
			break;
		}

	}

	@Override
	public void onSwitched(View view, int position) {
		// TODO Auto-generated method stub
		switch (position) {

		case 0:
			mOnLineUpdateTab.setBackgroundResource(R.drawable.bg_nav2);
			mSdUpdateTab.setBackgroundResource(R.drawable.bg_nav1);
			mMoreTab.setBackgroundResource(R.drawable.bg_nav1);
			break;
		case 1:
			mSdUpdateTab.setBackgroundResource(R.drawable.bg_nav2);
			mOnLineUpdateTab.setBackgroundResource(R.drawable.bg_nav1);
			mMoreTab.setBackgroundResource(R.drawable.bg_nav1);
			break;
		case 2:
			mMoreTab.setBackgroundResource(R.drawable.bg_nav2);
			mSdUpdateTab.setBackgroundResource(R.drawable.bg_nav1);
			mOnLineUpdateTab.setBackgroundResource(R.drawable.bg_nav1);
			break;
		}
	}

	@Override
	public void onScrolled(int h, int v, int oldh, int oldv) {
		// TODO Auto-generated method stub

	}

}
