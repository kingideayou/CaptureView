package me.next.drawrectview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by NeXT on 17/1/11.
 */

public class PaletteView extends View {

    private static final String TAG = "PaletteView";
    private int downX;
    private int downY;
    private int moveX;
    private int moveY;

    Rect mSpecificRect = new Rect();
    Paint mPaint = new Paint();
    Paint mSpecificAreaPaint = new Paint();
    Canvas mCanvas = new Canvas();

    public PaletteView(Context context) {
        this(context, null);
    }

    public PaletteView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PaletteView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawView(canvas);
        super.onDraw(canvas);
    }

    private void drawView(Canvas canvas) {

        int width = getWidth();
        int height = getHeight();

        Rect rect = new Rect(0, 0, width, height);

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(getResources().getColor(R.color.palette_background));
        mPaint.setAntiAlias(true);
        canvas.drawRect(rect, mPaint);

        mSpecificAreaPaint.setStyle(Paint.Style.FILL);
        mSpecificAreaPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mSpecificAreaPaint.setColor(getResources().getColor(R.color.specific_area_background));
        mSpecificAreaPaint.setAntiAlias(true);
        canvas.drawRect(mSpecificRect, mSpecificAreaPaint);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) event.getX();
                downY = (int) event.getY();
                Log.e(TAG, "downX : " + downX + " --- downY : " + downY);
                break;
            case MotionEvent.ACTION_MOVE:
                moveX = (int) event.getX();
                moveY = (int) event.getY();
                Log.e(TAG, "moveX : " + moveX + " --- moveY : " + moveY);
                break;
            case MotionEvent.ACTION_UP:
                mSpecificRect.set(downX, downY, moveX, moveY);
                downX = 0;
                downY = 0;
                moveX = 0;
                moveY = 0;
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        postInvalidate();
        return true;
    }
}
