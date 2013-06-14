package com.otaserver.client.customwidget;

import com.otaserver.client.customwidget.SdUpdatePromptView.mOnTouchListener;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;


public class AboutPromptView extends SdUpdatePromptView{
	private Paint mTitlePaint = null ;
	private final float titletsize = 28 ;
	private String[] title = null;
	private int titleTextH = 0 ;
	public AboutPromptView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		setOnTouchListener(new mOnTouchListener()) ;
		mTitlePaint = new Paint() ;
		mTitlePaint.setStyle(Style.FILL) ;
		mTitlePaint.setAntiAlias(true) ;
		mTitlePaint.setTextSize(titletsize) ;
		mTitlePaint.setColor(Color.BLUE) ;
		mTitlePaint.setTypeface(Typeface.DEFAULT_BOLD) ;
		FontMetrics fm = mTitlePaint.getFontMetrics() ;
		titleTextH = (int) Math.ceil(fm.bottom - fm.top) + 2;
	}




	
	public void mGoDraw(String[] texttitle,String[] textcontent) {
		// TODO Auto-generated method stub
		mGoDraw(textcontent);
		title = texttitle ;
	}
		
	private int mOnlineTextLineNum = 0 ;
	private int mSdcardTextLineNum = 0 ;
	private int mOnlineSetTextLineNum = 0 ;
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		int mOnlineItemHeight = 0 ;
		int mSDcardItemHeight = 0 ;

		canvas.drawText(title[0], initCoordsX, initCoordsY+titleTextH+offset, mTitlePaint) ;
		mOnlineTextLineNum = GetTextIfon(mTextCentent[0],mLineWidth, initCoordsX ,initCoordsY+titleTextH,TopAlignment ,canvas) ;
		mOnlineItemHeight = initCoordsY+titleTextH+m_iFontHeight*mOnlineTextLineNum+20 ;
		canvas.drawBitmap(mBitmap_line, initCoordsX, mOnlineItemHeight+offset, mPaintImage) ;

		
		canvas.drawText(title[1], initCoordsX,  mOnlineItemHeight+titleTextH+10+offset, mTitlePaint) ;
		mSdcardTextLineNum = GetTextIfon(mTextCentent[1],mLineWidth, initCoordsX ,mOnlineItemHeight+titleTextH+10,TopAlignment ,canvas) ;
		mSDcardItemHeight = mOnlineItemHeight+titleTextH+10 +m_iFontHeight*mSdcardTextLineNum+20 ;
		canvas.drawBitmap(mBitmap_line, initCoordsX, mSDcardItemHeight+offset, mPaintImage) ;
		
		
		canvas.drawText(title[2], initCoordsX,  mSDcardItemHeight+titleTextH+10+offset, mTitlePaint) ;
		mOnlineSetTextLineNum = GetTextIfon(mTextCentent[2],mLineWidth, initCoordsX ,mSDcardItemHeight+titleTextH+10,TopAlignment ,canvas) ;

	}
//	private int offset = 0;
	private int Y = 0 ;
	private int paperHeight =  0 ;
	public class mOnTouchListener implements OnTouchListener{
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			paperHeight = initCoordsY+titleTextH+m_iFontHeight*mOnlineTextLineNum+20+titleTextH+m_iFontHeight*mSdcardTextLineNum+20+titleTextH+m_iFontHeight*mOnlineSetTextLineNum+50+initCoordsY;
			switch(event.getAction()){
			case MotionEvent.ACTION_DOWN :
				Y = (int) event.getY() ;
				Log.d("ontouch", "downY is = "+Y) ;
				break;
			case MotionEvent.ACTION_MOVE :
				offset+=event.getY()-Y ;
				if(mViewHeight>=paperHeight){
					offset = 0 ;
				}
				else if(offset>=0){
					offset = 0 ;
				}
				else if(mViewHeight-offset>=paperHeight){
					offset = mViewHeight-paperHeight ;
				}
				Log.d("ontouch", "moveY is = "+event.getY()) ;
				Log.d("ontouch", "offsetYSpace is = "+offset) ;
				Log.d("ontouch", "paperHeight is = "+paperHeight) ;
				Y = (int) event.getY() ;
				invalidate() ;
				break ;
			}
			return true;
		}
		
	}

	
}
