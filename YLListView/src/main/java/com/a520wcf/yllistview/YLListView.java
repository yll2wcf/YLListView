package com.a520wcf.yllistview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Scroller;

/**
 * 邮箱 yll@520wcf.com
 * Created by yull on 12/17.
 */
public class YLListView extends ListView implements AbsListView.OnScrollListener {
    private Scroller mScroller; // used for scroll back
    private float mLastY = -1;

    private int mScrollBack;
    private final static int SCROLLBACK_HEADER = 0;
    private final static int SCROLLBACK_FOOTER = 1;

    private final static int SCROLL_DURATION = 400; // scroll back duration
    private final static float OFFSET_RADIO = 1.8f;
    // total list items, used to detect is at the bottom of ListView.
    private int mTotalItemCount;
    private View mHeaderView;  // 顶部图片
    private View mFooterView;  // 底部图片
    private int finalTopHeight;
    private int finalBottomHeight;

    public YLListView(Context context) {
        super(context);
        initWithContext(context);
    }

    public YLListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWithContext(context);
    }

    public YLListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initWithContext(context);
    }

    private void initWithContext(Context context) {
        mScroller = new Scroller(context, new DecelerateInterpolator());
        super.setOnScrollListener(this);

        this.getViewTreeObserver().addOnGlobalLayoutListener(
                new OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if(mHeaderView==null){
                            View view=new View(getContext());
                            addHeaderView(view);
                        }
                        if(mFooterView==null){
                            View view=new View(getContext());
                            addFooterView(view);
                        }
                        getViewTreeObserver()
                                .removeGlobalOnLayoutListener(this);
                    }
                });
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mLastY == -1) {
            mLastY = ev.getRawY();
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float deltaY = ev.getRawY() - mLastY;
                mLastY = ev.getRawY();
                if (getFirstVisiblePosition() == 0 && (mHeaderView.getHeight() > finalTopHeight || deltaY > 0)
                        && mHeaderView.getTop() >= 0) {
                    // the first item is showing, header has shown or pull down.
                    updateHeaderHeight(deltaY / OFFSET_RADIO);
                } else if (getLastVisiblePosition() == mTotalItemCount - 1
                        && (getFootHeight() >finalBottomHeight || deltaY < 0)) {
                    updateFooterHeight(-deltaY / OFFSET_RADIO);
                }
                break;
            default:
                mLastY = -1; // reset
                if (getFirstVisiblePosition() == 0 && getHeaderHeight() > finalTopHeight) {
                    resetHeaderHeight();
                }
                if (getLastVisiblePosition() == mTotalItemCount - 1 ){
                        if(getFootHeight() > finalBottomHeight) {
                            resetFooterHeight();
                        }
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 重置底部高度
     */
    private void resetFooterHeight() {
        int bottomHeight = getFootHeight();
        if (bottomHeight > finalBottomHeight) {
            mScrollBack = SCROLLBACK_FOOTER;
            mScroller.startScroll(0, bottomHeight, 0, -bottomHeight+finalBottomHeight,
                    SCROLL_DURATION);
            invalidate();
        }
    }
    // 计算滑动  当invalidate()后 系统会自动调用
    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            if (mScrollBack == SCROLLBACK_HEADER) {
                setHeaderHeight(mScroller.getCurrY());
            } else {
                setFooterViewHeight(mScroller.getCurrY());
            }
            postInvalidate();
        }
        super.computeScroll();
    }
    // 设置顶部高度
    private void setHeaderHeight(int height) {
        LayoutParams layoutParams = (LayoutParams) mHeaderView.getLayoutParams();
        layoutParams.height = height;
        mHeaderView.setLayoutParams(layoutParams);
    }
    // 设置底部高度
    private void setFooterViewHeight(int height) {
        LayoutParams layoutParams =
                (LayoutParams) mFooterView.getLayoutParams();
        layoutParams.height =height;
        mFooterView.setLayoutParams(layoutParams);
    }
    // 获取顶部高度
    public int getHeaderHeight() {
        AbsListView.LayoutParams layoutParams =
                (AbsListView.LayoutParams) mHeaderView.getLayoutParams();
        return layoutParams.height;
    }
    // 获取底部高度
    public int getFootHeight() {
        AbsListView.LayoutParams layoutParams =
                (AbsListView.LayoutParams) mFooterView.getLayoutParams();
        return layoutParams.height;
    }

    private void resetHeaderHeight() {
        int height = getHeaderHeight();
        if (height == 0) // not visible.
            return;
        mScrollBack = SCROLLBACK_HEADER;
        mScroller.startScroll(0, height, 0, finalTopHeight - height,
                SCROLL_DURATION);
        invalidate();
    }

    /**
     * 设置顶部高度  如果不设置高度,默认就是布局本身的高度
     * @param height 顶部高度
     */
    public void setFinalTopHeight(int height) {
        this.finalTopHeight = height;
    }
    /**
     * 设置底部高度  如果不设置高度,默认就是布局本身的高度
     * @param height 底部高度
     */
    public void setFinalBottomHeight(int height){
        this.finalBottomHeight=height;
    }
    @Override
    public void addHeaderView(View v) {
        mHeaderView = v;
        super.addHeaderView(mHeaderView);
        mHeaderView.getViewTreeObserver().addOnGlobalLayoutListener(
                new OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if(finalTopHeight==0) {
                            finalTopHeight = mHeaderView.getMeasuredHeight();
                        }
                        setHeaderHeight(finalTopHeight);
                        getViewTreeObserver()
                                .removeGlobalOnLayoutListener(this);
                    }
                });
    }

    @Override
    public void addFooterView(View v) {
        mFooterView = v;
        super.addFooterView(mFooterView);

        mFooterView.getViewTreeObserver().addOnGlobalLayoutListener(
                new OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if(finalBottomHeight==0) {
                            finalBottomHeight = mFooterView.getMeasuredHeight();
                        }
                        setFooterViewHeight(finalBottomHeight);
                        getViewTreeObserver()
                                .removeGlobalOnLayoutListener(this);
                    }
                });
    }

    private OnScrollListener mScrollListener; // user's scroll listener

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        mScrollListener = l;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mScrollListener != null) {
            mScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        // send to user's listener
        mTotalItemCount = totalItemCount;
        if (mScrollListener != null) {
            mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount,
                    totalItemCount);
        }
    }

    private void updateHeaderHeight(float delta) {
        setHeaderHeight((int) (getHeaderHeight()+delta));
        setSelection(0); // scroll to top each time
    }

    private void updateFooterHeight(float delta) {
        setFooterViewHeight((int) (getFootHeight()+delta));

    }
}
