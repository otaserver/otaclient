package com.otaserver.client;

import com.otaserver.client.R;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
//常见问题界面
public class FamiliarQuestionActivity extends Activity implements OnClickListener{
	private WebView wv = null ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.familiarquestion) ;
		ImageView back = (ImageView)findViewById(R.id.question_back) ;
		back.setOnClickListener(this) ;
		wv = (WebView)findViewById(R.id.question_webview) ;
		//设置WebView属性，能够执行Javascript脚本 
		wv.getSettings().setJavaScriptEnabled(true);
		
		//加载需要显示的网页 
		wv.loadUrl("http://www.baidu.com");
		

		 //设置Web视图 
		wv.setWebViewClient(new HelloWebViewClient ()); 



	}
	
	
	 //Web视图 
    private class HelloWebViewClient extends WebViewClient { 
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) { 
            view.loadUrl(url); 
            return true; 
        }

		@Override
		public void onPageFinished(WebView view, String url) {
			// TODO Auto-generated method stub
			super.onPageFinished(view, url);
			DialogClass.cancelProgressDialog_circle() ;
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			// TODO Auto-generated method stub
			super.onPageStarted(view, url, favicon);
			DialogClass.showProgressDialog(FamiliarQuestionActivity.this, "正在加载...") ;
		} 
        
        
    }
	
	
	  @Override
	    //设置回退 
	    //覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法 
	    public boolean onKeyDown(int keyCode, KeyEvent event) { 
	        if ((keyCode == KeyEvent.KEYCODE_BACK) && wv.canGoBack()) { 
	            wv.goBack(); //goBack()表示返回WebView的上一页面 
	            return true; 
	        } else if((keyCode == KeyEvent.KEYCODE_BACK) && !wv.canGoBack()){
	        	finish() ;
	        }
	        return false; 
	    }


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.question_back :
			finish() ;
			break ;
		}
	}

}
