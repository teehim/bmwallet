package com.bmwallet.Class;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ViewFlipper;

/**
 * Created by Thanatkorn on 9/9/2014.
 */
public class CustomViewFlipper extends ViewFlipper {

    Paint paint = new Paint();


    public CustomViewFlipper(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
    protected void dispatchDraw(Canvas canvas)
    {
        super.dispatchDraw(canvas);
        int width = getWidth();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);

        float margin = 10;
        float radius = 20;
        float cx = width / 2 - ((radius + margin) * 2 * getChildCount() / 2);
        float cy = getHeight() - 25;

        canvas.save();

        for (int i = 0; i < getChildCount(); i++)
        {
            if (i == getDisplayedChild())
            {
                paint.setColor(0xFFFFFFFF);
                paint.setAlpha(125);
                canvas.drawCircle(cx, cy, radius, paint);

            } else
            {
                paint.setColor(0xFFFFFFFF);
                paint.setAlpha(60);
                canvas.drawCircle(cx, cy, radius, paint);
            }
            cx += 2 * (radius + margin);
        }
        canvas.restore();
    }
}
