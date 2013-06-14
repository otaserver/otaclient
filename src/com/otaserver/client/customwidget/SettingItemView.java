package com.otaserver.client.customwidget;

import com.otaserver.client.R;
import com.otaserver.client.utils.Constants;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

public class SettingItemView extends View{
	private int ScreenW = 0 ;
	private final int mPaddingLeft = 10 ;
	private final int mPaddingRight = 10;
	private final int mViewDefHeight = 70 ;
	private final int mTextX = 10 ;
	private final int mTextSpace = 5 ;
	private int mViewCurWigth  ;
	private int mViewCurHeight ;
	private int bitmapW = 0 ;
	private int bitmapH = 0 ;
	private int mNameTextH = 0 ;
	private int mSpecTextH = 0 ;
	private int mNameTextY = 0 ;
	private int mSpecTextY = 0 ;
	private int mBitmapX = 0 ;
	private int mBitmapY = 0 ;
	private String mNameText = "";
	private String mSpecText = "";
	private Bitmap bitmap_on = null ;
	private Bitmap bitmap_off = null ;
	private boolean settingstate = false ;
	private Paint mName_Paint = null ;
	private Paint mSpec_Paint = null ;
	private Paint mSwitch_Paint = null ;

	public SettingItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SettingItemView) ;
		mNameText = array.getString(R.styleable.SettingItemView_textname) ;
		mSpecText = array.getString(R.styleable.SettingItemView_textspec) ;
		DisplayMetrics dm = context.getResources().getDisplayMetrics() ;
		ScreenW = dm.widthPixels ;
		
		bitmap_on = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.on)).getBitmap() ;
		bitmap_off = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.off)).getBitmap() ;
		bitmapW = bitmap_on.getWidth() ;
		bitmapH = bitmap_on.getHeight() ;
		
		mName_Paint = new Paint() ;
		mName_Paint.setAntiAlias(true) ;
		mName_Paint.setColor(Color.BLACK) ;
		mName_Paint.setTypeface(Typeface.DEFAULT_BOLD) ;
		mName_Paint.setTextSize(25) ;
		FontMetrics name_fm =  mName_Paint.getFontMetrics() ;
		mNameTextH = (int) Math.ceil(name_fm.bottom - name_fm.top) + 2;
		
		mSpec_Paint = new Paint() ;
		mSpec_Paint.setAntiAlias(true) ;
		mSpec_Paint.setColor(Color.GRAY) ;
		mSpec_Paint.setTextSize(20) ;
		FontMetrics spec_fm =  mSpec_Paint.getFontMetrics() ;
		mSpecTextH = (int) Math.ceil(spec_fm.bottom - spec_fm.top) + 2;
		
		mSwitch_Paint = new Paint() ;
		mSwitch_Paint.setDither(true) ;
		
		
	}
	
	private void initCoordinate(){
		mNameTextY = getNameTextY() ;
		mSpecTextY = getSpecTextY(mNameTextY) ;
		mBitmapX = getBitmapX() ;
		mBitmapY = getBitmapY() ;
	}
		
		

	private int getNameTextY(){
		int mNameTextY = (mViewCurHeight-(mNameTextH+mTextSpace+mSpecTextH))/2+mNameTextH-10;
		return mNameTextY ;
	}
	
	private int getSpecTextY(int NameTextY){
		int mSpecTextY = NameTextY + mTextSpace + mSpecTextH ;
		return mSpecTextY ;
	}
	
	private int getBitmapX(){
		int mBitmapX = mViewCurWigth-bitmapW-mPaddingRight ;
		return mBitmapX ;
	}
	
	private int getBitmapY(){
		int mBitmapH = mViewCurHeight/2-bitmapH/2 ;
		return mBitmapH ;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		initCoordinate() ;
		canvas.drawText(mNameText, mPaddingLeft , mNameTextY , mName_Paint) ;
		canvas.drawText(mSpecText, mPaddingLeft , mSpecTextY , mSpec_Paint) ;
		if(settingstate){
			canvas.drawBitmap(bitmap_on, mBitmapX, mBitmapY, mSwitch_Paint) ;
		}else{
			canvas.drawBitmap(bitmap_off, mBitmapX, mBitmapY, mSwitch_Paint) ;
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		setMeasuredDimension(getMeasuredWidth(widthMeasureSpec), 
				getMeasuredHeight(heightMeasureSpec)) ;
	}
	
	private int getMeasuredWidth(int measureWidth){
		int widthMode = MeasureSpec.getMode(measureWidth) ;
		int widthSize = MeasureSpec.getSize(measureWidth) ;
		int result = 0 ;
		if(widthMode == MeasureSpec.EXACTLY){
			result = widthSize ;
		}else{
			result = ScreenW - mPaddingLeft - mPaddingRight ;
		}
		mViewCurWigth = result ;
		return result ;
	}
	
	private int getMeasuredHeight(int measureHeight){
		int heightMode = MeasureSpec.getMode(measureHeight) ;
		int heightSize = MeasureSpec.getSize(measureHeight) ;
		int result = 0 ;
		if(heightMode == MeasureSpec.EXACTLY){
			result = heightSize ;
		}else{
			result = mViewDefHeight ;
		}
		mViewCurHeight = result ;
		return result ;
	}
	
//	public void setSettingState(SettingState settingstate){
//		this.settingstate = settingstate ;
//	}
//	
//	public interface SettingState {
//		boolean getState() ;
//	}
	
	public void changeSettingState(boolean state){
		settingstate = state ;
		invalidate() ;
	}
}
