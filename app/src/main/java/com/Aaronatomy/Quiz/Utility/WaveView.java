package com.Aaronatomy.Quiz.Utility;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by AaronAtomy on 2018/3/22.
 * WaveView
 */

public class WaveView extends View {
    private int width = 0;
    private int height = 0;
    private int baseLine = 0;
    private Paint mPaint;
    private int waveWidth;
    private float offset = 0f;

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void updateXControl() {
        ValueAnimator mAnimator = ValueAnimator.ofFloat(0, waveWidth);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                offset = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        mAnimator.setDuration(1000);
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(getPath(), mPaint);
    }

    private void initView() {
        mPaint = new Paint();
        mPaint.setColor(Color.argb(64, 255, 255, 255));
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        waveWidth = width;
        baseLine = height / 2;
        updateXControl();
    }

    private Path getPath() {
        int itemWidth = waveWidth / 2;
        Path mPath = new Path();
        mPath.moveTo(-itemWidth * 3, baseLine);
        for (int i = -3; i < 2; i++) {
            int startX = i * itemWidth;
            mPath.quadTo( startX + itemWidth / 2 + offset,
                    getWaveHeight(i), startX + itemWidth + offset,
                    baseLine);
        }
        mPath.lineTo(width, height); //
        mPath.lineTo(0, height); //
        mPath.close();
        return mPath;
    }

    private int getWaveHeight(int num) {
        int waveHeight = 45;
        if (num % 2 == 0) {
            return baseLine + waveHeight;
        }
        return baseLine - waveHeight;
    }
}
