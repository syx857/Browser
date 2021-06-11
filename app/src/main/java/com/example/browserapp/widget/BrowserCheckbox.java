package com.example.browserapp.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class BrowserCheckbox extends androidx.appcompat.widget.AppCompatCheckBox {

    public BrowserCheckbox(Context context) {
        super(context);
    }

    public BrowserCheckbox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.performClick();
        return false;
    }
}
