package com.bmwallet.Class;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class CustomViewPager extends ViewPager {

    private GestureDetector mGestureDetector;
    View.OnTouchListener mGestureListener;

	public CustomViewPager(Context context) {
		super(context);
	}

	public CustomViewPager(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
        mGestureDetector = new GestureDetector(context, new YScrollDetector());
        setFadingEdgeLength(0);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(this.getCurrentItem()==0){
            return false;
        }else{
            return super.onInterceptTouchEvent(ev) && mGestureDetector.onTouchEvent(ev);
        }

	}

    class YScrollDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if(Math.abs(distanceY+50000) > Math.abs(distanceX)) {
                return true;
            }
            return false;
        }
    }


	
	
}
