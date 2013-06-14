package com.otaserver.client;

import com.otaserver.client.R;
import com.otaserver.client.utils.Constants;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.TextView;

public class DialogClass {
	private static final int NF_PUSHID = 0x14 ;
	public static Dialog dialog = null ;
	public static Dialog progressDialog_circle = null ;
	public static Dialog progressDialog_horizontal = null ;
	public static ProgressBar mProgressBar = null ;

	public static Notification pushNotify = null ;
	public static NotificationManager manager = null;
    
	public static void cancelNotify(Context context,int id){
		manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		manager.cancel(id) ;
	}
	
	public static void showPushNotify(Context context, String body) {
		if (pushNotify == null) {
			pushNotify = new Notification(R.drawable.icon, "有新版本啦",
					System.currentTimeMillis());
			pushNotify.flags = Notification.FLAG_AUTO_CANCEL;
			pushNotify.contentView = new RemoteViews(context
					.getApplicationContext().getPackageName(),
					R.layout.notify_push);
			manager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			Intent pushIntent = new Intent();
			pushIntent.setClass(context, OtaActivity.class);
			pushIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			
			Bundle bundle = new Bundle() ;
			bundle.putString(Constants.Intent_Bundle_Key_Notification, 
					Constants.Intent_Bundle_Value_Notification) ;
			pushIntent.putExtras(bundle) ;
			pushNotify.contentIntent = PendingIntent.getActivity(context, 0,
					pushIntent, 0);
		}
		pushNotify.contentView.setTextViewText(R.id.notifypushtext, body);
		manager.notify(NF_PUSHID, pushNotify);

	}
	
	public static void showProgressDialog_h(Context context,String title){
		if(null == progressDialog_horizontal)
			progressDialog_horizontal = new Dialog(context, R.style.MyDialog) ;
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
		View mProgressDialogView = inflater.inflate(R.layout.lenovo_progressdialog_horizontal, null) ;
	
		mProgressBar = (ProgressBar)mProgressDialogView.findViewById(R.id.progressbar_horizontal) ;
		TextView title_textView = (TextView)mProgressDialogView.findViewById(R.id.lenovo_progressdialog_horizontal_titletext) ;
		title_textView.setText(title) ;
		
		progressDialog_horizontal.setContentView(mProgressDialogView) ;
		progressDialog_horizontal.setCancelable(false) ;
		progressDialog_horizontal.show() ;
	}
	
	public static void setProgress(int progress_value){
		if(mProgressBar!=null)
			mProgressBar.setProgress(progress_value) ;
	}
	
	public static void cancelProgressDialog_h(){
		if(progressDialog_horizontal!=null){
			progressDialog_horizontal.cancel() ;
			progressDialog_horizontal = null ;
			mProgressBar = null ;
		}
	}
	
	public static void showProgressDialog(Context context,String message){
		if(null == progressDialog_circle)
			progressDialog_circle = new Dialog(context, R.style.MyDialog) ;
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
		View mProgressDialogView = inflater.inflate(R.layout.lenovo_progressdialog_circle, null) ;
		
		TextView message_textView = (TextView)mProgressDialogView.findViewById(R.id.progress_circle_textview_message) ;
		message_textView.setText(message) ;
		
		progressDialog_circle.setContentView(mProgressDialogView) ;
		progressDialog_circle.show() ;
	}
	

	public static void cancelProgressDialog_circle(){
		if(null != progressDialog_circle){
			progressDialog_circle.cancel() ;
			progressDialog_circle = null ;
		}
	}
	
	public static void showAlertDialog(Context context,
									   String title,
									   String message,
									   String LeftButtonText,
									   String RightButtonText,
									   android.view.View.OnClickListener onClickListener_Left,
									   android.view.View.OnClickListener onClickListener_Right){
		
		if(null == dialog)
			dialog = new Dialog(context, R.style.MyDialog);
		
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View mDialogView = inflater.inflate(R.layout.lenovo_dialog, null) ;
        
		//定义widget
		Button mDialogButton_Left = (Button)mDialogView.findViewById(R.id.lenovo_dialog_button_ok) ;
        Button mDialogButton_Right= (Button)mDialogView.findViewById(R.id.lenovo_dialog_button_cancel) ;
        TextView mDialogText_Message = (TextView)mDialogView.findViewById(R.id.lenovo_dialog_messagetext) ;
        TextView mDialogText_Title = (TextView)mDialogView.findViewById(R.id.lenovo_dialog_titletext) ;
		
        
        mDialogText_Title.setText(title) ;
        mDialogText_Message.setText(message) ;
        
        mDialogButton_Left.setText(LeftButtonText) ;
        mDialogButton_Right.setText(RightButtonText) ;
        mDialogButton_Left.setOnClickListener(onClickListener_Left) ;
        mDialogButton_Right.setOnClickListener(onClickListener_Right) ;
        //设置它的ContentView
        dialog.setContentView(mDialogView);
        dialog.show() ;
	}
	
	public static void CancelDialog(){
		if(dialog!=null){
			dialog.cancel() ;
			dialog = null ;
		}
	}
	
	
	
	
}
