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
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by NeXT on 17/1/11.
 */

public class PaletteView extends View {

    private static final String TAG = "PaletteView";
    private static final int BORDER_STROKE_WIDTH = 2;//dp
    private static final int MIN_AREA_HEIGHT = 10;//dp
    private static final int DEFAULT_BUTTON_HEIGHT = 10;//dp
    private static final int BUTTON_RADIUS = 15;//dp

    private static final int BUTTON_NONE = -1;
    private static final int BUTTON_LEFT_TOP = 1;
    private static final int BUTTON_RIGHT_TOP = 2;
    private static final int BUTTON_LEFT_BOTTOM = 3;
    private static final int BUTTON_RIGHT_BOTTOM = 4;

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
    RectF mLeftTopButtonRect = new RectF();
    RectF mRightTopButtonRect = new RectF();
    RectF mLeftBottomButtonRect = new RectF();
    RectF mRightBottomButtonRect = new RectF();

    private int mButtonRadius;
    @CurrentButton
    private int currentControlButton = BUTTON_NONE;

    //记录缩放前绘制区域四角坐标
    int currentLeft;
    int currentTop;
    int currentRight;
    int currentBottom;

    int mScreenHeight;
    int mScreenWidth;

    @IntDef({BUTTON_NONE, BUTTON_LEFT_TOP, BUTTON_RIGHT_TOP, BUTTON_LEFT_BOTTOM, BUTTON_RIGHT_BOTTOM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CurrentButton {}

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

        okButtonBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.check_white);
        cancelButtonBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.close_white);

        buttonHeight = okButtonBitmap.getHeight();
        buttonWidth = okButtonBitmap.getWidth();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mScreenWidth = MeasureSpec.getSize(widthMeasureSpec);
        mScreenHeight = MeasureSpec.getSize(heightMeasureSpec);
        this.setMeasuredDimension(mScreenWidth, mScreenHeight);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
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

            canvas.drawOval(mLeftTopButtonRect, mSpecificAreaBorderPaint);
            canvas.drawOval(mRightTopButtonRect, mSpecificAreaBorderPaint);
            canvas.drawOval(mLeftBottomButtonRect, mSpecificAreaBorderPaint);
            canvas.drawOval(mRightBottomButtonRect, mSpecificAreaBorderPaint);
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

                if (mLeftTopButtonRect.contains(downX, downY)) {
                    currentControlButton = BUTTON_LEFT_TOP;
                    saveCurrentLocation();
                    break;
                } else if (mRightTopButtonRect.contains(downX, downY)) {
                    currentControlButton = BUTTON_RIGHT_TOP;
                    saveCurrentLocation();
                    break;
                } else if (mLeftBottomButtonRect.contains(downX, downY)) {
                    currentControlButton = BUTTON_LEFT_BOTTOM;
                    saveCurrentLocation();
                    break;
                } else if (mRightBottomButtonRect.contains(downX, downY)) {
                    currentControlButton = BUTTON_RIGHT_BOTTOM;
                    saveCurrentLocation();
                    break;
                } else {
                    currentControlButton = BUTTON_NONE;
                }

                //触摸在之前绘制的区域
                Log.e(TAG, "SpecificRect : " + mSpecificRect.left + " - " + mSpecificRect.top + " - " + mSpecificRect.right + " - " + mSpecificRect.bottom);
                isTouchingSpecificArea = mSpecificRect.contains(downX, downY);

                if (!isTouchingSpecificArea) {
                    mOkButtonRect.set(0, 0, 0, 0);
                    mCancelButtonRect.set(0, 0, 0, 0);
                    mLeftTopButtonRect.set(0, 0, 0, 0);
                    mRightTopButtonRect.set(0, 0, 0, 0);
                    mLeftBottomButtonRect.set(0, 0, 0, 0);
                    mRightBottomButtonRect.set(0, 0, 0, 0);
                }

                break;
            case MotionEvent.ACTION_MOVE:
                moveX = (int) event.getX();
                moveY = (int) event.getY();

                Log.e(TAG, "moveX : " + moveX + " --- moveY : " + moveY);
                Log.e(TAG, "isTouchingSpecificArea : " + isTouchingSpecificArea);
                if (isTouchingSpecificArea) { //移动绘制区域

                    deltaX = moveX - (lastMoveX == 0 ? downX : lastMoveX);
                    deltaY = moveY - (lastMoveY == 0 ? downY : lastMoveY);

                    lastMoveX = moveX;
                    lastMoveY = moveY;

                    int targetLeft = mSpecificRect.left + deltaX;
                    int targetTop = mSpecificRect.top + deltaY;
                    int targetRight = mSpecificRect.right + deltaX;
                    int targetBottom = mSpecificRect.bottom + deltaY;

                    if (targetLeft <= 0 || targetRight >= mScreenWidth) {//接触到左右边缘,只处理上下滑动
                        if (targetTop >= 0 && targetBottom <= mScreenHeight) {
                            mSpecificRect.set(
                                    mSpecificRect.left,
                                    targetTop < 0 ? 0 : targetTop,
                                    mSpecificRect.right,
                                    targetBottom < 0 ? 0 : targetBottom);
                        }
                    } else if (targetTop <= 0 || targetBottom >= mScreenHeight) {
                        if (targetLeft >= 0 && targetRight <= mScreenWidth) {
                            mSpecificRect.set(
                                    targetLeft < 0 ? 0 : targetLeft,
                                    mSpecificRect.top,
                                    targetRight < 0 ? 0 : targetRight,
                                    mSpecificRect.bottom);
                        }
                    } else {
                        mSpecificRect.set(
                                targetLeft < 0 ? 0 : targetLeft,
                                targetTop < 0 ? 0 : targetTop,
                                targetRight < 0 ? 0 : targetRight,
                                targetBottom < 0 ? 0 : targetBottom);
                    }
                    updateMenuBarLocation();
                    updateZoomButtonLocation();

                } else {
                    if (currentControlButton != BUTTON_NONE) { //缩放
                        if (currentControlButton == BUTTON_LEFT_TOP) {
                            mSpecificRect.set(
                                    Math.min(moveX, currentRight),
                                    Math.min(moveY, currentBottom),
                                    Math.max(moveX, currentRight),
                                    Math.max(moveY, currentBottom));
                        } else if (currentControlButton == BUTTON_RIGHT_TOP) {
                            mSpecificRect.set(
                                    Math.min(moveX, currentLeft),
                                    Math.min(moveY, currentBottom),
                                    Math.max(moveX, currentLeft),
                                    Math.max(moveY, currentBottom));
                        } else if (currentControlButton == BUTTON_LEFT_BOTTOM) {
                            mSpecificRect.set(
                                    Math.min(moveX, currentRight),
                                    Math.min(moveY, currentTop),
                                    Math.max(moveX, currentRight),
                                    Math.max(moveY, currentTop));
                        } else { //BUTTON_RIGHT_BOTTOM
                            mSpecificRect.set(
                                    Math.min(moveX, currentLeft),
                                    Math.min(moveY, currentTop),
                                    Math.max(moveX, currentLeft),
                                    Math.max(moveY, currentTop));
                        }
                        updateMenuBarLocation();
                        updateZoomButtonLocation();
                    } else {
                        mSpecificRect.set(Math.min(downX, moveX), Math.min(downY, moveY), Math.max(downX, moveX), Math.max(downY, moveY));
                    }
                }
                break;
            case MotionEvent.ACTION_UP:

                if (!isTouchingSpecificArea) { //处理点击事件
                    //对绘制区域最小值进行限制
                    if (currentControlButton == BUTTON_NONE &&
                            (moveX == 0 || moveY == 0
                            || Math.abs(moveX - downX) < minHeight || Math.abs(moveY - downY) < minHeight)) {
                        mSpecificRect.set(0, 0, 0, 0);
                        showMenuBar = false;
                    } else {
                        showMenuBar = true;
                        updateMenuBarLocation();
                        updateZoomButtonLocation();
                    }
                } else { //移动绘制视图
                    showMenuBar = true;
                }
                isTouchingSpecificArea = false;
                currentControlButton = BUTTON_NONE;
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

    private void updateMenuBarLocation() {

        int topLocation = mSpecificRect.top;
        int bottomLocation = mSpecificRect.bottom;

        if (mScreenHeight - bottomLocation >= buttonHeight) {//底部有足够空间
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
        } else if (topLocation > buttonHeight) {
            mOkButtonRect.set( //顶部有足够空间
                    mSpecificRect.right - buttonWidth,
                    mSpecificRect.top - buttonHeight,
                    mSpecificRect.right,
                    mSpecificRect.top);

            mCancelButtonRect.set(
                    mSpecificRect.right - buttonWidth * 2,
                    mSpecificRect.top - buttonHeight,
                    mSpecificRect.right - buttonWidth,
                    mSpecificRect.top);
        } else {
            mOkButtonRect.set(
                    mSpecificRect.right - buttonWidth,
                    mSpecificRect.bottom - buttonHeight,
                    mSpecificRect.right,
                    mSpecificRect.bottom);

            mCancelButtonRect.set(
                    mSpecificRect.right - buttonWidth * 2,
                    mSpecificRect.bottom - buttonHeight,
                    mSpecificRect.right - buttonWidth,
                    mSpecificRect.bottom);
        }
    }

    private void updateZoomButtonLocation() {
        mLeftTopButtonRect.set(
                mSpecificRect.left - mButtonRadius,
                mSpecificRect.top - mButtonRadius,
                mSpecificRect.left + mButtonRadius,
                mSpecificRect.top + mButtonRadius);

        mRightTopButtonRect.set(
                mSpecificRect.right - mButtonRadius,
                mSpecificRect.top - mButtonRadius,
                mSpecificRect.right + mButtonRadius,
                mSpecificRect.top + mButtonRadius);

        mLeftBottomButtonRect.set(
                mSpecificRect.left - mButtonRadius,
                mSpecificRect.bottom - mButtonRadius,
                mSpecificRect.left + mButtonRadius,
                mSpecificRect.bottom + mButtonRadius);

        mRightBottomButtonRect.set(
                mSpecificRect.right - mButtonRadius,
                mSpecificRect.bottom - mButtonRadius,
                mSpecificRect.right + mButtonRadius,
                mSpecificRect.bottom + mButtonRadius);
    }

    private void saveCurrentLocation() {
        currentLeft = mSpecificRect.left;
        currentTop = mSpecificRect.top;
        currentRight = mSpecificRect.right;
        currentBottom = mSpecificRect.bottom;
    }
}
