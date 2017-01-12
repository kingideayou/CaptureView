package me.next.drawrectview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
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
    private static final int DEFAULT_BUTTON_HEIGHT = 10;//dp
    private static final int BUTTON_RADIUS = 10;//dp

    private int downX;
    private int downY;
    private int moveX;
    private int moveY;
    private int deltaX;
    private int deltaY;
    private int lastMoveX;
    private int lastMoveY;

    private boolean isTouchingSpecificArea = false; //拖拽选定视图标记位
    private boolean showMenuBar = false;
    private int minHeight;
    private int buttonHeight;
    private int buttonWidth;

    Rect mSpecificRect = new Rect();
    Rect mSpecificBorderRect = new Rect();
    Paint mPaint = new Paint();
    Paint mSpecificAreaPaint = new Paint();
    Paint mSpecificAreaBorderPaint = new Paint();

    RectF mOkButtonRect = new RectF();
    RectF mCancelButtonRect = new RectF();
    Bitmap okButtonBitmap;
    Bitmap cancelButtonBitmap;

    //四个控制缩放按钮
    RectF mLeftTopRect = new RectF();
    RectF mRightTopRect = new RectF();
    RectF mLeftBottomRect = new RectF();
    RectF mRightBottomRect = new RectF();

    private int mButtonRadius;

    public PaletteView(Context context) {
        this(context, null);
    }

    public PaletteView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PaletteView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        minHeight = (int) ScreenUtils.dipToPixels(getContext(), MIN_AREA_HEIGHT);
        mButtonRadius = (int) ScreenUtils.dipToPixels(getContext(), BUTTON_RADIUS);

//        buttonHeight = (int) ScreenUtils.dipToPixels(getContext(), DEFAULT_BUTTON_HEIGHT);

        okButtonBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.check_white);
        cancelButtonBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.close_white);

        buttonHeight = okButtonBitmap.getHeight();
        buttonWidth = okButtonBitmap.getWidth();

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

        if (showMenuBar) {
            canvas.drawBitmap(okButtonBitmap, null, mOkButtonRect, mSpecificAreaBorderPaint);
            canvas.drawBitmap(cancelButtonBitmap, null, mCancelButtonRect, mSpecificAreaBorderPaint);

            canvas.drawOval(mLeftTopRect, mSpecificAreaBorderPaint);
            canvas.drawOval(mRightTopRect, mSpecificAreaBorderPaint);
            canvas.drawOval(mLeftBottomRect, mSpecificAreaBorderPaint);
            canvas.drawOval(mRightBottomRect, mSpecificAreaBorderPaint);
        }

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

                if (!isTouchingSpecificArea) {
                    mOkButtonRect.set(0, 0, 0, 0);
                    mCancelButtonRect.set(0, 0, 0, 0);
                    mLeftTopRect.set(0, 0, 0, 0);
                    mRightTopRect.set(0, 0, 0, 0);
                    mLeftBottomRect.set(0, 0, 0, 0);
                    mRightBottomRect.set(0, 0, 0, 0);
                }

                break;
            case MotionEvent.ACTION_MOVE:
                moveX = (int) event.getX();
                moveY = (int) event.getY();

                Log.e(TAG, "moveX : " + moveX + " --- moveY : " + moveY);
                if (isTouchingSpecificArea) { //移动绘制区域

                    deltaX = moveX - (lastMoveX == 0 ? downX : lastMoveX);
                    deltaY = moveY - (lastMoveY == 0 ? downY : lastMoveY);

                    lastMoveX = moveX;
                    lastMoveY = moveY;

                    mSpecificRect.set(
                            mSpecificRect.left + deltaX,
                            mSpecificRect.top + deltaY,
                            mSpecificRect.right + deltaX,
                            mSpecificRect.bottom + deltaY);

                    mOkButtonRect.set(
                            mOkButtonRect.left + deltaX,
                            mOkButtonRect.top + deltaY,
                            mOkButtonRect.right + deltaX,
                            mOkButtonRect.bottom + deltaY);

                    mCancelButtonRect.set(
                            mCancelButtonRect.left + deltaX,
                            mCancelButtonRect.top + deltaY,
                            mCancelButtonRect.right + deltaX,
                            mCancelButtonRect.bottom + deltaY);

                    mLeftTopRect.set(
                            mSpecificRect.left - mButtonRadius,
                            mSpecificRect.top - mButtonRadius,
                            mSpecificRect.left + mButtonRadius,
                            mSpecificRect.top + mButtonRadius);

                    mRightTopRect.set(
                            mSpecificRect.right - mButtonRadius,
                            mSpecificRect.top - mButtonRadius,
                            mSpecificRect.right + mButtonRadius,
                            mSpecificRect.top + mButtonRadius);

                    mLeftBottomRect.set(
                            mSpecificRect.left - mButtonRadius,
                            mSpecificRect.bottom - mButtonRadius,
                            mSpecificRect.left + mButtonRadius,
                            mSpecificRect.bottom + mButtonRadius);

                    mRightBottomRect.set(
                            mSpecificRect.right - mButtonRadius,
                            mSpecificRect.bottom - mButtonRadius,
                            mSpecificRect.right + mButtonRadius,
                            mSpecificRect.bottom + mButtonRadius);

                } else {
                    mSpecificRect.set(Math.min(downX, moveX), Math.min(downY, moveY), Math.max(downX, moveX), Math.max(downY, moveY));
                }
                break;
            case MotionEvent.ACTION_UP:

                int targetRight = mSpecificRect.right;
                int targetBottom = mSpecificRect.bottom;

                if (!isTouchingSpecificArea) { //处理点击事件
                    //对绘制区域最小值进行限制
                    if (moveX == 0 || moveY == 0
                            || Math.abs(moveX - downX) < minHeight || Math.abs(moveY - downY) < minHeight) {
                        mSpecificRect.set(0, 0, 0, 0);
                        showMenuBar = false;
                    } else {
                        showMenuBar = true;
                        mOkButtonRect.set(
                                mSpecificRect.right - buttonWidth,
                                mSpecificRect.bottom,
                                mSpecificRect.right,
                                mSpecificRect.bottom + buttonHeight);

                        mCancelButtonRect.set(
                                mSpecificRect.right - buttonWidth * 2,
                                mSpecificRect.bottom,
                                mSpecificRect.right - buttonWidth,
                                mSpecificRect.bottom + buttonHeight);

                        mLeftTopRect.set(
                                mSpecificRect.left - mButtonRadius,
                                mSpecificRect.top - mButtonRadius,
                                mSpecificRect.left + mButtonRadius,
                                mSpecificRect.top + mButtonRadius);

                        mRightTopRect.set(
                                mSpecificRect.right - mButtonRadius,
                                mSpecificRect.top - mButtonRadius,
                                mSpecificRect.right + mButtonRadius,
                                mSpecificRect.top + mButtonRadius);

                        mLeftBottomRect.set(
                                mSpecificRect.left - mButtonRadius,
                                mSpecificRect.bottom - mButtonRadius,
                                mSpecificRect.left + mButtonRadius,
                                mSpecificRect.bottom + mButtonRadius);

                        mRightBottomRect.set(
                                mSpecificRect.right - mButtonRadius,
                                mSpecificRect.bottom - mButtonRadius,
                                mSpecificRect.right + mButtonRadius,
                                mSpecificRect.bottom + mButtonRadius);

                    }
                } else { //移动绘制视图
                    showMenuBar = true;
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
