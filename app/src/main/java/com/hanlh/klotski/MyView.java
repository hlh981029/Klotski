package com.hanlh.klotski;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

/**
 * TODO: document your custom view class.
 */
public class MyView extends android.support.v7.widget.AppCompatButton {
    Scroller scroller;
    int direction = -1;

    public MyView(Context context) {

        this(context, null);
    }

    public MyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        scroller = new Scroller(context);
    }

    @Override
    public void computeScroll() {
        if (scroller != null) {
            if (scroller.computeScrollOffset()) {//判断scroll是否完成
                ((View) getParent()).scrollTo(
                        scroller.getCurrX(), scroller.getCurrY()
                );//执行本段位移

                invalidate();//进行下段位移
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                scroller.startScroll(((int) getX()), ((int) getY()), ((int) getX()) * direction,
                        ((int) getY()) * direction);//开始位移，真正开始是在下面的invalidate
                direction *= -1;//改变方向
                invalidate();//开始执行位移
                break;
        }
        return super.onTouchEvent(event);
    }
}
