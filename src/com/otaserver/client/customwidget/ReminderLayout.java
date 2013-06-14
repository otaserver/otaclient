package com.otaserver.client.customwidget;

import com.otaserver.client.R;
import com.otaserver.client.utils.Constants;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ReminderLayout extends LinearLayout implements View.OnClickListener{
	private static final int OneWeek = 0 ;
	private static final int TwoWeeks = 1 ;
	private static final int ThreeWeeks = 2 ;
	private static final int FourWeeks = 3 ;
	private static final int OneWeek_DaysNum = 7 ;
	private static final int TwoWeeks_DaysNum = 14 ;
	private static final int ThreeWeeks_DaysNum = 21 ;
	private static final int FourWeeks_DaysNum = 30 ;
	private SharedPreferences mSP = null ;
	private LinearLayout oneWeekLayout = null ;
	private LinearLayout twoWeekLayout = null ;
	private LinearLayout threeWeekLayout = null ;
	private LinearLayout fourWeekLayout = null ;
	private ImageView[] imageviewArray = null ;
	private ImageView oneWeek_Select = null ;
	private ImageView twoWeek_Select = null ;
	private ImageView threeWeek_Select = null ;
	private ImageView fourWeek_Select = null ;
	public ReminderLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public ReminderLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		 mSP = context.getSharedPreferences(Constants.SP_Settings, Activity.MODE_PRIVATE) ;
		 TypedArray a = context.obtainStyledAttributes(attrs,
                 R.styleable.ReminderView);
		 a.recycle();
	}
	
	
    public void startEntryView() {
    	initLayout() ;	
        if (this.getVisibility() == View.GONE || this.getVisibility() == View.INVISIBLE) {
            this.setVisibility(View.VISIBLE);
            this.startAnimation(entryAnimation(600));
        }
    }
    
    public void ExitEntryView() {
        if (this.getVisibility() == View.VISIBLE ) {
        	this.setVisibility(View.INVISIBLE);
            this.startAnimation(exitAnimation(600));
        }
    }
    
    /**
     * 进入动画
     * @param duration
     * @return
     */
    private static Animation entryAnimation(int duration) {
        Animation outtoLeft = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 1.0f, Animation.RELATIVE_TO_PARENT,
                        +0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoLeft.setDuration(duration);
        return outtoLeft;
    }
    
    /**
     * 退出动画
     * @param duration
     * @return
     */
    private static Animation exitAnimation(int duration) {
        Animation outtoLeft = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
                        +1.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoLeft.setDuration(duration);
        return outtoLeft;
    }
	
	private void initLayout(){
		oneWeekLayout = (LinearLayout) this.findViewById(R.id.oneweeklayout) ;
		twoWeekLayout = (LinearLayout) this.findViewById(R.id.twoweeklayout) ;
		threeWeekLayout = (LinearLayout) this.findViewById(R.id.throwweeklayout) ;
		fourWeekLayout = (LinearLayout) this.findViewById(R.id.fourweeklayout) ;
		oneWeekLayout.setOnClickListener(this) ;
		twoWeekLayout.setOnClickListener(this) ;
		threeWeekLayout.setOnClickListener(this) ;
		fourWeekLayout.setOnClickListener(this) ;
		
		
		oneWeek_Select = (ImageView) this.findViewById(R.id.oneweek_select_image) ;
		twoWeek_Select = (ImageView) this.findViewById(R.id.twoweek_select_image) ;
		threeWeek_Select = (ImageView) this.findViewById(R.id.throwweek_select_image) ;
		fourWeek_Select = (ImageView) this.findViewById(R.id.fourweek_select_image) ;
		initItemStatus() ;
		
		imageviewArray = new ImageView[]{oneWeek_Select,twoWeek_Select,threeWeek_Select,fourWeek_Select} ;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.oneweeklayout:
			changeItemStatus(0);
			break;
		case R.id.twoweeklayout:
			changeItemStatus(1);
			break;
		case R.id.throwweeklayout:
			changeItemStatus(2);
			break;
		case R.id.fourweeklayout:
			changeItemStatus(3);
			break;
		}
	}

	private void changeItemStatus(int index){
		int count = getChildCount() ;
		for(int i=0;i<count;i++){
			
			if(i==index){
				imageviewArray[i].setVisibility(View.VISIBLE) ;
				editorSharedPreferences(index) ;
			}else{
				imageviewArray[i].setVisibility(View.GONE) ;
			}
		}
		ExitEntryView() ;
	}
	
	private void initItemStatus(){
		int Days = mSP.getInt(Constants.TimeForDays, 7) ;
		int index = Days/7-1 ;

		switch(index){
		case OneWeek :
			oneWeek_Select.setVisibility(View.VISIBLE) ;
			break;
		case TwoWeeks :
			twoWeek_Select.setVisibility(View.VISIBLE) ;
			break;
		case ThreeWeeks :
			threeWeek_Select.setVisibility(View.VISIBLE) ;
			break;
		case FourWeeks :
			fourWeek_Select.setVisibility(View.VISIBLE) ;
			break;
		}
	}
	
	private void editorSharedPreferences(int pos){
		SharedPreferences.Editor editor = mSP.edit() ;
		int DayNum = OneWeek_DaysNum ;
		switch(pos){
		case TwoWeeks :
			DayNum = TwoWeeks_DaysNum ;
			break;
		case ThreeWeeks :
			DayNum = ThreeWeeks_DaysNum ;
			break;
		case FourWeeks :
			DayNum = FourWeeks_DaysNum ;
			break;
		}
		editor.putInt(Constants.TimeForDays, DayNum) ;
		editor.commit() ;
		
	}


	@Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }
	
}
