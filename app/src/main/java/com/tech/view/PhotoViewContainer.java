package com.tech.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class PhotoViewContainer extends RelativeLayout {

    public PhotoViewContainer(Context context) {
        super(context);
    }

    public PhotoViewContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PhotoViewContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PhotoViewContainer(Context context, AttributeSet attrs, int defStyleAttr,
            int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //多点触控时禁止父view拦截手势，防止在放缩/旋转时滑动ViewPager
        if (ev.getPointerCount() >= 2) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        return super.dispatchTouchEvent(ev);
    }
}
