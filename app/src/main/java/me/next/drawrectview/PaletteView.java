package me.next.drawrectview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
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
    private static final int BORDER_STROKE_WIDTH = 2;//dp
    private static final int MIN_AREA_HEIGHT = 10;//dp

    private int downX;
    private int downY;
    private int moveX;
    private int moveY;
    private int deltaX;
    private int deltaY;
    private int lastMoveX;
    private int lastMoveY;

    private boolean isTouchingSpecificArea = false; //拖拽选定视图标记位
    private int minHeight;

    Rect mSpecificRect = new Rect();
    Rect mSpecificBorderRect = new Rect();
    Paint mPaint = new Paint();
    Paint mSpecificAreaPaint = new Paint();
    Paint mSpecificAreaBorderPaint = new Paint();

    public PaletteView(Context context) {
        this(context, null);
    }

    public PaletteView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PaletteView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        minHeight = (int) ScreenUtils.dipToPixels(getContext(), MIN_AREA_HEIGHT);
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

        mSpecificBorderRect.set(
                mSpecificRect.left - 1,
                mSpecificRect.top - 1,
                mSpecificRect.right + 1,
                mSpecificRect.bottom + 1);

        mSpecificAreaBorderPaint.setStyle(Paint.Style.STROKE);
        mSpecificAreaBorderPaint.setColor(Color.BLACK);
        mSpecificAreaBorderPaint.setStrokeWidth(ScreenUtils.dipToPixels(getContext(), BORDER_STROKE_WIDTH));
        canvas.drawRect(mSpecificBorderRect, mSpecificAreaBorderPaint);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) event.getX();
                downY = (int) event.getY();
                moveX = 0;
                moveY = 0;
                Log.e(TAG, "downX : " + downX + " --- downY : " + downY);

                //触摸在之前绘制的区域
                Log.e(TAG, "SpecificRect : " + mSpecificRect.left + " - " + mSpecificRect.top + " - " + mSpecificRect.right + " - " + mSpecificRect.bottom);
                isTouchingSpecificArea = mSpecificRect.contains(downX, downY);
                break;
            case MotionEvent.ACTION_MOVE:
                moveX = (int) event.getX();
                moveY = (int) event.getY();

                Log.e(TAG, "moveX : " + moveX + " --- moveY : " + moveY);
                if (isTouchingSpecificArea) {

                    deltaX = moveX - (lastMoveX == 0 ? downX : lastMoveX);
                    deltaY = moveY - (lastMoveY == 0 ? downY : lastMoveY);

                    lastMoveX = moveX;
                    lastMoveY = moveY;

                    mSpecificRect.set(
                            mSpecificRect.left + deltaX,
                            mSpecificRect.top + deltaY,
                            mSpecificRect.right + deltaX,
                            mSpecificRect.bottom + deltaY);
                } else {
                    mSpecificRect.set(Math.min(downX, moveX), Math.min(downY, moveY), Math.max(downX, moveX), Math.max(downY, moveY));
                }
                break;
            case MotionEvent.ACTION_UP:
                if (!isTouchingSpecificArea) { //处理点击事件 & 对绘制区域最小值进行限制
                    if (moveX == 0 || moveY == 0
                            || Math.abs(moveX - downX) < minHeight || Math.abs(moveY - downY) < minHeight) {
                        mSpecificRect.set(0, 0, 0, 0);
                    }
                }
                isTouchingSpecificArea = false;
                downX = 0;
                downY = 0;
                moveX = 0;
                moveY = 0;
                lastMoveX = 0;
                lastMoveY = 0;
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        postInvalidate();
        return true;
    }
}
