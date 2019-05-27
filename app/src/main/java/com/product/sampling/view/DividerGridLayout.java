package com.product.sampling.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.gridlayout.widget.GridLayout;

public class DividerGridLayout extends GridLayout {

    private Paint vPaint;
    private Paint hPaint;

    public DividerGridLayout(Context context) {
        this(context, null);
    }

    public DividerGridLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DividerGridLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            int left = view.getLeft();
            int top = view.getTop();
            int bottom = view.getBottom();
            int right = view.getRight();
//            if (i % 2 == 0) {
                canvas.drawLine(right - 1, top, right - 1, bottom, vPaint);
//            } else {
                canvas.drawLine(left, top, left, bottom, vPaint);
//            }
//            if (i >= getColumnCount())
                canvas.drawLine(left, top, right, top, hPaint);
        }
        super.dispatchDraw(canvas);
    }

    private void init() {
        vPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        vPaint.setColor(Color.parseColor("#9EA7B4"));
        vPaint.setStyle(Paint.Style.STROKE);
        vPaint.setStrokeWidth(2.0f);
        hPaint = new Paint(vPaint);
        hPaint.setColor(Color.parseColor("#22000000"));
        hPaint.setStyle(Paint.Style.STROKE);
        hPaint.setStrokeWidth(2.0f);
    }
}