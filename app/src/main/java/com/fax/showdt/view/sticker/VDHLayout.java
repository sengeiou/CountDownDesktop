package com.fax.showdt.view.sticker;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.customview.widget.ViewDragHelper;

public class VDHLayout extends RelativeLayout
{
    private ViewDragHelper mDragger;
    private DraggerCallback mCallback;

    public void setCallback(DraggerCallback callback) {
        this.mCallback = callback;
    }

    public VDHLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        mDragger = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback()
        {
            @Override
            public boolean tryCaptureView(View child, int pointerId)
            {
                return true;
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx)
            {
                return left;
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy)
            {
                return top;
            }

            @Override
            public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
                super.onViewReleased(releasedChild, xvel, yvel);
                Log.i("test_drag","手指拖动结束 x:"+xvel + "  y:"+yvel);
                if (mCallback != null){
                    mCallback.release();
                }
            }

            @Override
            public void onViewDragStateChanged(int state) {
                super.onViewDragStateChanged(state);

            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event)
    {
        Log.i("test_touch","onInterceptTouchEvent");
        return mDragger.shouldInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        Log.i("test_touch","onTouchEvent");
        mDragger.processTouchEvent(event);
        return true;
    }

    public interface DraggerCallback{
        void release();
        void down();
    }
}