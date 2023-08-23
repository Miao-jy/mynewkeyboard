package com.flyjingfish.switchkeyboard;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;

public class DragRelativeLayout extends RelativeLayout {

    private final String TAG = "DragChangeHeight";
    private ViewDragHelper mDragHelper;
    private int oriTop;
    private int oriBottom;
    private int oriLeft;
    private int oriRight;

    public DragRelativeLayout(Context context) {
        this(context, (AttributeSet) null);
    }

    public DragRelativeLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public DragRelativeLayout(Context context, AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
        this.initDragView();
    }


    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            Log.d(TAG, "mDragHelper.continueSettling");
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mDragHelper.cancel();
            return false;
        }
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragHelper.processTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            oriTop = getTop();
            oriBottom = getBottom();
            oriLeft = getLeft();
            oriRight = getRight();
        }
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        Log.d(TAG, "l=" + l + ",t=" + t + ",r=" + r + ",b=" + b);
    }

    private void initDragView() {
        RelativeLayout _this = this;
        ViewDragHelper.Callback callback = (ViewDragHelper.Callback) (new ViewDragHelper.Callback() {
            public boolean tryCaptureView(View child, int pointerId) {
                View mDragView = findViewById(R.id.drag_view);
                Log.d(TAG, "child=" + child + ", dragView=" + mDragView + ", child==dragView:" + (child == mDragView));
                return mDragView == child;
            }

            public int clampViewPositionVertical(View child, int top, int dy) {
                Log.d(TAG, "top=" + top + ", dy=" + dy);
                int topBound = getPaddingTop();
                int bottomBound = getHeight() - child.getHeight() - getPaddingBottom();
                int newTop = Math.min(Math.max(top, topBound), bottomBound);
                return top;
            }

            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
                Log.d(TAG, "left=" + left + ", top=" + top + ", dx=" + dx + ", dy=" + dy + ", oriTop=" + oriTop + ", " + "oriLeft=" + oriLeft + ", oriRight=" + oriRight + ", oriBottom-" + oriBottom + ", width=" + (oriRight - oriLeft) + ", height=" + (oriBottom - oriTop));
                View panelView = findViewById(R.id.ll_menu);
                RelativeLayout.LayoutParams topViewLayoutParams = (RelativeLayout.LayoutParams) panelView.getLayoutParams();
                topViewLayoutParams.height = topViewLayoutParams.height - dy;
                panelView.setLayoutParams(topViewLayoutParams);
                super.onViewPositionChanged(changedView, left, top, dx, dy);
            }

            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                Log.d(TAG, "xvel=" + xvel + ", yvel=" + yvel);
//                mDragHelper.smoothSlideViewTo(releasedChild, oriLeft, oriTop);
            }
        });

        this.mDragHelper = ViewDragHelper.create(this, 0.6f, callback);
    }
}
