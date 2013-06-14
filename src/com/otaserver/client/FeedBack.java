package com.otaserver.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import com.otaserver.client.R;
import com.otaserver.client.sdk.MobileInfo;
import com.otaserver.client.utils.ConnNetWork;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//问题反馈界面
public class FeedBack extends Activity implements OnClickListener{
	private static final String feedbackinfo = "FeedbackInfo" ;
	private static final String imei = "IMEI" ;
	private static final String devicemodel = "DeviceModel" ;
	private static final String firmwaretype = "FirmwareType" ;
	private static final String pid = "PID" ;
	private static final String locale = "Locale" ;
	private static final String URL = "http://fus.dev.surepush.cn/" ;
	private static final int TIMEOUT = 60;
	private HttpClient httpClient = null ;
	private boolean mTextIsChanged = false ;
	private EditText mEditText = null;
	private TextView mtv = null ;
	private int TextNum = 0 ;
	private final int textSize = 500 ;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feedback);
		mtv = (TextView)findViewById(R.id.feedback_tv) ;
		mEditText = (EditText) findViewById(R.id.feedback_et);
		mEditText.addTextChangedListener(mTextWatcher);
		Button refer = (Button) findViewById(R.id.feedback_ok) ;
		Button cancel = (Button) findViewById(R.id.feedback_cancel) ;
		ImageView back = (ImageView)findViewById(R.id.feedback_back) ;
		back.setOnClickListener(this) ;
		refer.setOnClickListener(this) ;
		cancel.setOnClickListener(this) ;
	}
//用于edittext控件的字符计数功能
	TextWatcher mTextWatcher = new TextWatcher() {
		private CharSequence temp;
		private int editStart;
		private int editEnd;

		@Override
		public void beforeTextChanged(CharSequence s, int arg1, int arg2,
				int arg3) {
			temp = s;
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int end, int count) {
			if(count>0){
				TextNum += count ;
				mTextIsChanged = true ;
				if(TextNum>textSize){
					TextNum = textSize ;
				}
			}else{
				--TextNum ;
				if(TextNum<=0){
					mTextIsChanged = false ;
					TextNum = 0 ;
				}
			}
				
			mtv.setText(textSize-TextNum +"/"+textSize) ;
		}

		@Override
		public void afterTextChanged(Editable s) {
			editStart = mEditText.getSelectionStart();
			editEnd = mEditText.getSelectionEnd();
			if (temp.length() > textSize) {
				Toast.makeText(FeedBack.this, "你输入的字数已经超过了限制！",
						Toast.LENGTH_SHORT).show();
				s.delete(editStart - 1, editEnd);
				int tempSelection = editStart;
				mEditText.setText(s);
				mEditText.setSelection(tempSelection);
			}
		}
	};
	
	private String EncodeURL(String parameter){
		String mEncodeparameter = "" ;
		try {
			mEncodeparameter = URLEncoder.encode(parameter, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return mEncodeparameter ;
	}

	private String getJson(String Feedbackinfo) throws JSONException{
		MobileInfo minfo = new MobileInfo(this) ;
		String Imei = minfo.getImeiNumber() ;
		String Devicemodel = EncodeURL(minfo.getDeviceModel()) ;
		String Firmwaretype = EncodeURL(minfo.getFirmwareType()) ;
		String Pid = EncodeURL(minfo.getPid()) ;
		String Locale = EncodeURL(minfo.getLocLanguage()) ;
		JSONObject json = new JSONObject() ;
		json.put(feedbackinfo, Feedbackinfo) ;
		json.put(imei, Imei) ;
		json.put(devicemodel, Devicemodel) ;
		json.put(firmwaretype, Firmwaretype) ;
		json.put(pid, Pid) ;
		json.put(locale, Locale) ;
		
		return json.toString() ;
	}
	//想服务端提交信息
	private void post(){
		String feedbackinfo = mEditText.getText().toString() ;
		 HttpParams paramsw = createHttpParams();

	        httpClient = new DefaultHttpClient(paramsw);

	        HttpPost post = new HttpPost(URL);

	        List<NameValuePair> params = new ArrayList<NameValuePair>();

	        params.add(new BasicNameValuePair("par", "HttpClien_android_Post"));


		try {
			String stringjson = getJson(feedbackinfo);

			StringEntity se = new StringEntity("JSON: " + stringjson);
			se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
					"application/json"));

			post.setEntity(se);

			post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

			HttpResponse httpResponse = httpClient.execute(post);

			int httpCode = httpResponse.getStatusLine().getStatusCode();

			if (httpCode == HttpURLConnection.HTTP_OK && httpResponse != null) {
				Toast.makeText(FeedBack.this, "提交成功。",
						Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(FeedBack.this, "提交失败。",
						Toast.LENGTH_SHORT).show();
			}
		} catch (JSONException e) {
		} catch (UnsupportedEncodingException e) {
		} catch (IOException e) {
			Toast.makeText(FeedBack.this, "提交失败。",
					Toast.LENGTH_SHORT).show();
		} finally{
			if(null != httpClient){
				httpClient.getConnectionManager().shutdown() ;
				httpClient = null ;
			}
		}


	}
	
	
	 public static final HttpParams createHttpParams() {

	        final HttpParams params = new BasicHttpParams();

	        HttpConnectionParams.setStaleCheckingEnabled(params, false);

	        HttpConnectionParams.setConnectionTimeout(params, TIMEOUT * 1000);

	        HttpConnectionParams.setSoTimeout(params, TIMEOUT * 1000);

	        HttpConnectionParams.setSocketBufferSize(params, 8192 * 5);

	        return params;

	    }


	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.feedback_ok :
			if(mTextIsChanged){
				post() ;
			}else{
				Toast.makeText(FeedBack.this, "未填反馈信息！",
						Toast.LENGTH_SHORT).show();
			}
			break ;
		case R.id.feedback_cancel :
			finish() ;
			break ;
		
		case R.id.feedback_back :
			finish() ;
			break ;
		}
	}

}
