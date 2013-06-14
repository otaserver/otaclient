package com.otaserver.client;

import com.otaserver.client.R;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;


//更多界面
public class more implements OnClickListener{
	private LinearLayout mLocalInfo = null ;
	private LinearLayout mSettings = null ;
	private LinearLayout mFeedback = null ;
	private LinearLayout mQuestion = null ;
	private LinearLayout mApkUpdate = null ;
	private LinearLayout mAbout = null ;
	private Context mContext = null ;
	private View mView = null ;

	public more(Context context,View view){
		mContext = context ;
		mView = view ;
	}

	public void startShowView(){
		initWidget() ;
	}
	
	private void initWidget(){
		mLocalInfo = (LinearLayout)mView.findViewById(R.id.localinfo) ;
		mSettings = (LinearLayout)mView.findViewById(R.id.settings) ;
		mFeedback = (LinearLayout)mView.findViewById(R.id.feedback) ;
		mQuestion = (LinearLayout)mView.findViewById(R.id.question) ;
		mApkUpdate = (LinearLayout)mView.findViewById(R.id.update) ;
		mAbout = (LinearLayout)mView.findViewById(R.id.about) ;
		mLocalInfo.setOnClickListener(this) ;
		mSettings.setOnClickListener(this) ;
		mFeedback.setOnClickListener(this) ;
		mQuestion.setOnClickListener(this) ;
		mApkUpdate.setOnClickListener(this) ;
		mAbout.setOnClickListener(this) ;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.localinfo :
			Intent intent_localinfo = new Intent() ;
			intent_localinfo.setClass(mContext, LocalInfoActivity.class) ;
			mContext.startActivity(intent_localinfo) ;
			break ;
		case R.id.settings :
			Intent intent_settings = new Intent() ;
			intent_settings.setClass(mContext, SettingActivity.class) ;
			mContext.startActivity(intent_settings) ;
			break ;
		case R.id.feedback :
			Intent intent_feedback = new Intent() ;
			intent_feedback.setClass(mContext, FeedBack.class) ;
			mContext.startActivity(intent_feedback) ;
			break ;
		case R.id.question :
			Intent intent_question = new Intent() ;
			intent_question.setClass(mContext, FamiliarQuestionActivity.class) ;
			mContext.startActivity(intent_question) ;
			break ;
		case R.id.update :
			UpdateApk mUpdateApk = new UpdateApk(mContext,true) ;
			mUpdateApk.checkVersion() ;
			break ;
		case R.id.about :
			Intent intent_about = new Intent() ;
			intent_about.setClass(mContext, AboutActivity.class) ;
			mContext.startActivity(intent_about) ;
			break ;
		
		}
		
	}

		
	
}

