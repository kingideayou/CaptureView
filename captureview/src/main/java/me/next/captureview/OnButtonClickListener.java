package me.next.captureview;

import android.graphics.Rect;

/**
 * Created by NeXT on 17/1/14.
 */

public interface OnButtonClickListener {
    void onConfirmClick(Rect rect);
    void onCancelClick();
}
