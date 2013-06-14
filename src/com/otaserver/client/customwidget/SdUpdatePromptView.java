package com.otaserver.client.customwidget;


import java.util.Vector;

import com.otaserver.client.R;
import com.otaserver.client.R.dimen;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

public class SdUpdatePromptView extends View{
	public static final int TopAlignment = 101 ;
	public static final int CenterAlignment = 102 ;
	public int mPortraitSpace = 0 ;
	public int initCoordsX = 0 ;
	public int initCoordsY = 0 ;
	public int paddingTop = 20 ;
	public int paddingButtom = 20 ;
	public int paddingLeft = 20 ;
	public int paddingRight = 20 ;
	public float textSize = 0 ; ;
	public int mLineHeight = 1 ;
	public Paint mPaintImage = null ;
	public Paint mPaintText = null ;
	public Bitmap mBitmap_line = null ;
	public String[] mTextCentent = null ;
	public int mLineWidth ;
	public int linenum = 0;
	public float offset = 0 ;
	private int mCircleW = 10 ;
	private int mCircleH = 10 ;
	private int TextPaddingLeft = 15 ;
	private Bitmap mBitmap_Image = null ;
	public int m_iFontHeight ;
	public int mViewWidth ;
	public int mViewHeight ;
	public int m_iRealLine = 0 ;
	private int m_CurLineIndex = 0 ;
	
	public SdUpdatePromptView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		pxToPid(context) ; 
		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SdUpdatePromptView) ;
		textSize = array.getDimension(R.styleable.SdUpdatePromptView_textsize, 25) ;
		setOnTouchListener(new mOnTouchListener()) ;
		mPaintImage = new Paint() ;
		mPaintImage.setStyle(Style.FILL) ;
		
		mPaintText = new Paint() ;
		mPaintText.setStyle(Style.FILL) ;
		mPaintText.setAntiAlias(true) ;
		mPaintText.setTextSize(textSize) ;
		mPaintText.setColor(Color.BLACK) ;

		
		FontMetrics fm = mPaintText.getFontMetrics();
		m_iFontHeight = (int) Math.ceil(fm.bottom - fm.top) + 2;
		
		mBitmap_line = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.compartline)).getBitmap() ;
		mBitmap_Image = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.circle)).getBitmap() ;
	

	}
	
	private void pxToPid(Context context){
		DisplayMetrics dm = context.getResources().getDisplayMetrics() ;
		float density = (float)dm.densityDpi/160 ;

	}
	
	public void mGoDraw(String[] text){
		if(text==null){
			return ;
		}
		mTextCentent = text ;
		linenum = mTextCentent.length ;
		initCoordsX = paddingLeft ;
		initCoordsY = paddingTop ;
		invalidate() ;
	}
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas) ;

		

		for(int i = 0 ;i<linenum ;i++){
			m_CurLineIndex = i+1 ;
			float b = (float) (((i+1)*2-1)/2.0) ;
			canvas.drawBitmap(mBitmap_line, initCoordsX, initCoordsY+mPortraitSpace*i+offset, mPaintImage) ;
			GetTextIfon(mTextCentent[i],mLineWidth-mCircleW, initCoordsX + mCircleW + TextPaddingLeft,initCoordsY ,CenterAlignment,canvas) ;
			canvas.drawBitmap(mBitmap_Image, initCoordsX, (float)(initCoordsY+mPortraitSpace*b-mCircleH/2)+offset, mPaintImage) ;
		}
		canvas.drawBitmap(mBitmap_line, initCoordsX, initCoordsY+mPortraitSpace*linenum+offset, mPaintImage) ;
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		mViewWidth = getMeasureWidth(widthMeasureSpec) ;
		initbitmap(mViewWidth) ;
		mViewHeight = getMeasureHeight(heightMeasureSpec) ;
		setMeasuredDimension(mViewWidth, mViewHeight) ;

	}
	
	private void initbitmap(int ViewWidth){
		mLineWidth = mViewWidth - paddingLeft - paddingRight ;

		mBitmap_line = Bitmap.createScaledBitmap(mBitmap_line,mLineWidth, mLineHeight, false) ;
		mBitmap_Image = Bitmap.createScaledBitmap(mBitmap_Image, (int)mCircleW, (int)mCircleH, false) ;
	}
	
	private int getMeasureWidth(int measureWidth){
		int result = 0 ;
		int widthMode = MeasureSpec.getMode(measureWidth) ;
		int widthSize = MeasureSpec.getSize(measureWidth) ;
		if(widthMode == MeasureSpec.EXACTLY){
			result =  widthSize ;
		}else if(widthMode == MeasureSpec.AT_MOST){
			result = widthSize-getPaddingLeft()-getPaddingRight() ;
		}else{
			 throw new IllegalStateException("SdUpdatePromptView can not be used in UNSPECIFIED mode.");
		}
		
		return result ;
	}
	
	private int getMeasureHeight(int measureHeight){
		int result = 0 ;
		int HeightMode = MeasureSpec.getMode(measureHeight) ;
		int HeightSize = MeasureSpec.getSize(measureHeight) ;
		if(HeightMode == MeasureSpec.EXACTLY){
			result =  HeightSize ;
		}else if(HeightMode == MeasureSpec.AT_MOST){
			result = HeightSize-getPaddingTop()-getPaddingBottom() ;
		}else{
			 throw new IllegalStateException("SdUpdatePromptView can not be used in UNSPECIFIED mode.");
		}
		mPortraitSpace = (int)(result - paddingTop - paddingButtom)/linenum ;
		return result ;
	}
	
	
	public int GetTextIfon(String m_strText,int m_iTextWidth,int mTextPaddingLeft,int mTextPaddingTop,int AlignmentMode,Canvas canvas)
	{
		char ch;
		int w = 0;
		int istart = 0;
		m_iRealLine = 0 ;
		Vector<String> m_String = new Vector<String>();



		for (int i = 0; i < m_strText.length(); i++)
		{
			ch = m_strText.charAt(i);
			float[] widths = new float[1];
			String srt = String.valueOf(ch);
			mPaintText.getTextWidths(srt, widths);

			if (ch == '\n')
			{
				m_iRealLine++;
				m_String.addElement(m_strText.substring(istart, i));
				istart = i + 1;
				w = 0;
			}
			else
			{
				w += (int) (Math.ceil(widths[0]));
				if (w > m_iTextWidth)
				{
					m_iRealLine++;
					m_String.addElement(m_strText.substring(istart, i));
					istart = i;
					i--;
					w = 0;
				}
				else
				{
					if (i == (m_strText.length() - 1))
					{
						m_iRealLine++;
						m_String.addElement(m_strText.substring(istart, m_strText.length()));
					}
				}
			}
		}
		float mTextPosY = getTextPosY(AlignmentMode,mTextPaddingTop) ;
		
//		int m_ipageLineNum = m_iTextHeight / m_iFontHeight;
		DrawText(canvas,m_iRealLine,m_iFontHeight,mTextPaddingLeft,mTextPosY,m_String) ;
		
		return m_iRealLine ;
	}
	
	private void DrawText(Canvas canvas,int m_iRealLine,int m_iFontHeight ,int mTextPosX,float mTextPosY,Vector<String> m_String)
	{
		for (int j = 0; j < m_iRealLine;  j++)
		{

			canvas.drawText((String) (m_String.elementAt(j)), mTextPosX, mTextPosY + m_iFontHeight * j, mPaintText);
			
		}
	}
	
	private float getTextPosY(int mAlignmentMode,int mTextPaddingTop) {
		float mTextPosY = 0 ;
		if(mAlignmentMode==CenterAlignment){
			int mTextAllHeight = m_iFontHeight * m_iRealLine;
			if (mTextAllHeight > mPortraitSpace) {
				mPortraitSpace = mTextAllHeight ; 
				
				invalidate() ;
			}
			
			int lastLineTextPosY = (int) (mTextPaddingTop + mPortraitSpace
					* (m_CurLineIndex) - (mPortraitSpace - mTextAllHeight) / 2);
			int firstLineTextPosY = lastLineTextPosY - m_iFontHeight
			* (m_iRealLine - 1);
			mTextPosY = firstLineTextPosY-10+offset ;
		}else if(mAlignmentMode==TopAlignment){
			mTextPosY = mTextPaddingTop + m_iFontHeight+offset ;
		}

		return mTextPosY;
	}
	private int Y = 0;
	private int paperHeight = 0 ;
	public class mOnTouchListener implements OnTouchListener{
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			paperHeight = mPortraitSpace*linenum+ paddingTop+paddingButtom;
			switch(event.getAction()){
			case MotionEvent.ACTION_DOWN :
				Y = (int) event.getY() ;
				Log.d("ontouch", "downY is = "+Y) ;
				break;
			case MotionEvent.ACTION_MOVE :
				offset+=event.getY()-Y ;
				if(mViewHeight-offset>=paperHeight){
					offset = mViewHeight-paperHeight ;
				}else if(offset>=0){
					offset = 0 ;
				}
				Log.d("ontouch", "moveY is = "+event.getY()) ;
				Log.d("ontouch", "offsetYSpace is = "+offset) ;
				Y = (int) event.getY() ;
				invalidate() ;
				break ;
			}
			return true;
		}
		
	}
	

}
