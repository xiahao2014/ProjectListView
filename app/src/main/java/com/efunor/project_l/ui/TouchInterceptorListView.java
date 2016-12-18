package com.efunor.project_l.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.efunor.project_l.R;
import com.efunor.project_l.util.DensUtils;


/**
 * <br/>Description
 * <br/>Author: xiahao
 * <br/>Version 1.0
 * <br/>Date: 2016-10-28
 * <br/>Copyright<h3/> Copyright (c) 2016 Shenzhen YiFeng Technology Co., Ltd. Inc.
 * All rights reserved.
 */
public class TouchInterceptorListView extends ListView {


    private final Vibrator mVibrator;
    private ImageView mDragView;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mWindowParams;
    /**
     * 移动目的点
     */
    private int mDragPos;
    /**
     * 当前被拖动的点
     */
    private int mSrcDragPos;
    /**
     * 移动目的点 x ,y
     */
    private int mDragPointX;
    private int mDragPointY;
    private int mXOffset;
    private int mYOffset;
    private DropListener mDropListener;
    private ClearFromSubListener mClearFromSubListener;
    private int mUpperBound;
    private int mLowerBound;
    private int mHeight;
    private static final int FLING = 0;
    private static final int SLIDE = 1;
    private static final int TRASH = 2;
    private int mRemoveMode = -1;
    private Rect mTempRect = new Rect();
    private Bitmap mDragBitmap;
    //    private final int mTouchSlop;
    private int mItemHeightNormal;
    private int mItemHeightExpanded;
    private int mItemHeightHalf;
    private Drawable mTrashcan;

    private int currenLayerType;
    /**
     * 移动没达到改变层级关系
     */
    public static final int LAYER_NONE = 0;
    /**
     * 移动达到变为父级
     */
    public static final int LAYER_PARENT = 1;
    /**
     * 移动达到改变子级
     */
    public static final int LAYER_CHILD = 2;

    private int itemMargin;
    /**
     * 屏幕宽度
     */
    private final int width;


    private int downX;
    /**
     * 子项margin值
     */
    public static final int SUBMARGIN = 80;
    private MotionEvent mEvent;
    private boolean isDrag;
    /**
     * 震动相关
     */
    private final long[] mPattern;


    /**
     * 自定义长按事件
     */
    private int mLastMotionX, mLastMotionY;
    //是否移动了
    private boolean isMoved;
    //是否释放了
    private boolean isReleased;
    //计数器，防止多次点击导致最后一次形成longpress的时间变短
    private int mCounter;
    //长按的runnable
    private Runnable mLongPressRunnable;
    //移动的阈值
    private static final int TOUCH_SLOP = 20;

    /**
     * 当前滑动的ListView　position
     */
    private int slidePosition;
    /**
     * 手指按下X的坐标
     */
    private int mDownY;
    /**
     * 手指按下Y的坐标
     */
    private int mDownX;
    /**
     * 屏幕宽度
     */
    private int screenWidth;
    /**
     * ListView的item
     */
    private View itemView;
    /**
     * 滑动类
     */
    private Scroller scroller;
    private static final int SNAP_VELOCITY = 600;

    private boolean isShowMenu = false;
    /**
     * 速度追踪对象
     */
    private VelocityTracker velocityTracker;
    /**
     * 是否响应滑动，默认为不响应
     */
    private boolean isSlide = false;
    /**
     * 认为是用户滑动的最小距离
     */
    private int mTouchSlop;
    /**
     * 移除item后的回调接口
     */
    private RemoveListener mRemoveListener;
    /**
     * 用来指示item滑出屏幕的方向,向左或者向右,用一个枚举值来标记
     */
    private RemoveDirection removeDirection;

    // 滑动删除方向的枚举值
    public enum RemoveDirection {
        RIGHT, LEFT, NONE;
    }

    private boolean isChangY = true;


    public TouchInterceptorListView(Context context) {
        this(context, null);
    }

    public TouchInterceptorListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TouchInterceptorListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        screenWidth = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
        scroller = new Scroller(context);
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        mRemoveMode = FLING;
        Resources res = getResources();
        mItemHeightNormal = res.getDimensionPixelSize(R.dimen.normal_height);
        mItemHeightHalf = mItemHeightNormal / 2;
        mItemHeightExpanded = res.getDimensionPixelSize(R.dimen.expanded_height);
        width = new DensUtils().getWidth(context);
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        // 停止 开启 停止 开启
        mPattern = new long[]{50, 200};
//        init();
        mLongPressRunnable = new Runnable() {
            @Override
            public void run() {
                mCounter--;
                if (mCounter > 0 || isReleased || isMoved) return;
                isDrag = true;
                if (!isRefreshIng())
                    onLongClick();
            }
        };
    }

    /**
     * 设置滑动删除的回调接口
     *
     * @param removeListener
     */
    public void setRemoveListener(RemoveListener removeListener) {
        this.mRemoveListener = removeListener;
    }

    public void setTrashcan(Drawable trash) {
        mTrashcan = trash;
        mRemoveMode = TRASH;
    }

    public void setDropListener(DropListener l) {
        mDropListener = l;
    }

    public void setClearFromSubListener(ClearFromSubListener l) {
        mClearFromSubListener = l;
    }

    public interface DragListener {
        void drag(int from, int to);
    }

    /**
     * 交换
     */
    public interface DropListener {
        void drop(int from, int to, int layerType);
    }

//    /**
//     * 显示菜单
//     */
//    public interface RemoveListener {
//        void showMenu(int which, boolean isShowMenu);
//    }

    /**
     * 长按清除当前拖动的子项
     */
    public interface ClearFromSubListener {
        void clearFromSub(int from);
    }

    /**
     * 当ListView item滑出屏幕，回调这个接口
     * 我们需要在回调方法removeItem()中移除该Item,然后刷新ListView
     *
     * @author xiaanming
     */
    public interface RemoveListener {
        public void showSlideMenu(RemoveDirection direction, int position, boolean isShowMenu);
    }


    /**********************************************************
     事件的分发， 拖动事件、左滑实现
     /**********************************************************/
    /**
     * 为长按设置一个监听事件
     */
    public void onLongClick() {
//        onListItemLongClick(mEvent);
//        stopDragging();
        int itemnum = pointToPosition(mDownX, mDownY);
        if (itemnum == AdapterView.INVALID_POSITION) {
            return;
        }
        ViewGroup view = (ViewGroup) getChildAt(itemnum - getFirstVisiblePosition());
        RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.rl_partent);
        int leftMargin = relativeLayout.getLeft();

        mDragPointX = mDownX - view.getLeft() - leftMargin;
        mDragPointY = mDownY - view.getTop();
        mXOffset = ((int) mEvent.getRawX()) - mDownX;
        mYOffset = ((int) mEvent.getRawY()) - mDownY;
        itemMargin = leftMargin;
        // The left side of the item is the grabber for dragging the item
        view.setDrawingCacheEnabled(true);
        // Create a copy of the drawing cache so that it does not get recycled
        // by the framework when the list tries to clean up memory
        isChangY = true;
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        startDragging(bitmap, mDownX, mDownY);
        itemMargin = leftMargin;
        mDragPos = itemnum;
        mSrcDragPos = mDragPos;
        mHeight = getHeight();
        int touchSlop = mTouchSlop;
        mUpperBound = Math.min(mDownX - touchSlop, mHeight / 3);
        mLowerBound = Math.max(mDownY + touchSlop, mHeight * 2 / 3);

        mClearFromSubListener.clearFromSub(mSrcDragPos);//列表顺序从0开始 -1
    }

    /**
     * 分发事件，主要做的是判断点击的是那个item, 以及通过postDelayed来设置响应左右滑动事件
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                addVelocityTracker(event);
                // 假如scroller滚动还没有结束，我们直接返回
                if (!scroller.isFinished()) {
                    return super.dispatchTouchEvent(event);
                }
                mDownX = (int) event.getX();
                mDownY = (int) event.getY();

                slidePosition = pointToPosition(mDownX, mDownY);

                // 无效的position, 不做任何处理
                if (slidePosition == AdapterView.INVALID_POSITION) {
                    return super.dispatchTouchEvent(event);
                }
                // 获取我们点击的item view
                itemView = getChildAt(slidePosition - getFirstVisiblePosition());

                mEvent = event;
                mLastMotionX = mDownX;
                mLastMotionY = mDownY;
                mCounter++;
                isReleased = false;
                isMoved = false;
                isDrag = false;
                downX = (int) event.getX();
                postDelayed(mLongPressRunnable, 800);
            }
            break;
            case MotionEvent.ACTION_MOVE: {
                if (Math.abs(getScrollVelocity()) > SNAP_VELOCITY
                        || (Math.abs(event.getX() - mDownX) > mTouchSlop && Math
                        .abs(event.getY() - mDownY) < mTouchSlop)) {
                    isSlide = true;
                    if (!isRefreshIng()) {
                        setRefreshEnable(false);
                    }
                }

                if (isMoved) break;
                if (Math.abs(event.getX() - mLastMotionX) > TOUCH_SLOP
                        || Math.abs(event.getY() - mLastMotionY) > TOUCH_SLOP) {
                    //移动超过阈值，则表示移动了
                    isMoved = true;
//                    removeCallbacks(mLongPressRunnable);
                }
                break;
            }
            case MotionEvent.ACTION_UP:
                recycleVelocityTracker();
                //释放了
//                removeCallbacks(mLongPressRunnable);
                isReleased = true;
                break;
        }
        return super.dispatchTouchEvent(event);
    }


    /**
     * 处理拖动ListView item的逻辑
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!isDrag) {
            if (isSlide && slidePosition != AdapterView.INVALID_POSITION) {
                //滑动事件
                requestDisallowInterceptTouchEvent(true);
                addVelocityTracker(ev);
                final int action = ev.getAction();
                int x = (int) ev.getX();
                switch (action) {
                    case MotionEvent.ACTION_MOVE:
                        int deltaX = mDownX - x;
//                        mDownX = x;
                        // 手指拖动itemView滚动, deltaX大于0向左滚动，小于0向右滚
//                        itemView.scrollBy(deltaX, 0);

//                        System.out.println("deltaX-----" + deltaX);
                        MotionEvent cancelEvent = MotionEvent.obtain(ev);
                        cancelEvent.setAction(MotionEvent.ACTION_CANCEL |
                                (ev.getActionIndex() << MotionEvent.ACTION_POINTER_INDEX_SHIFT));
                        onTouchEvent(cancelEvent);

                        // 手指拖动itemView滚动, deltaX大于0向左滚动，小于0向右滚

                        if (!isRefreshIng() && deltaX > 0) {
                            itemView.scrollTo(deltaX, 0);
                            // 根据手指滑动的距离，调整透明度
//                            itemView.setAlpha(1f - Math.abs((float) deltaX / screenWidth));
                        } else {
                            mDownX = x;
                        }

                        break;
                    case MotionEvent.ACTION_UP:
                        int distance = (int) (downX - ev.getX());
                        if (!isRefreshIng()) {
                            if (distance > width / 4) {
                                isShowMenu = true;
                                scrollLeft();
                                mRemoveListener.showSlideMenu(removeDirection, slidePosition, isShowMenu);

                            } else if (-distance > width / 6) {
                                isShowMenu = false;
                                mRemoveListener.showSlideMenu(removeDirection, slidePosition, isShowMenu);

                            } else {
                                scrollBack();

                            }
//                            TouchDragDownActivity.instance.getSwipeRefreshLayout().setEnabled(true);
                            setRefreshEnable(true);
                        }
                        isSlide = false;

                        break;
                }
                return true; // 拖动的时候ListView不滚动
            } else if (isSlide) {
                addVelocityTracker(ev);
                final int action = ev.getAction();
                int x = (int) ev.getX();
                switch (action) {
                    case MotionEvent.ACTION_MOVE:
                        int deltaX = mDownX - x;
                        mDownX = x;
                        break;
                    case MotionEvent.ACTION_UP:
                        int distance = (int) (downX - ev.getX());
                        if (-distance > width / 6) {
                            isShowMenu = false;
                            mRemoveListener.showSlideMenu(removeDirection, slidePosition, isShowMenu);
                        }
                        isSlide = false;
                        break;
                }
                return true; // 拖动的时候ListView不滚动
            }
        } else {
            if ((mDropListener != null) && mDragView != null) {
                //拖动事件
                int action = ev.getAction();
                switch (action) {
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        Rect r = mTempRect;
                        mDragView.getDrawingRect(r);
                        stopDragging();

                        if (mDropListener != null && mDragPos >= 0) {
                            int distance = (int) (downX - ev.getX());
                            if (distance > 0) {
                                if (distance > width / 3) {
                                    //右滑变成父项
                                    if (itemMargin >= SUBMARGIN) {
                                        currenLayerType = LAYER_PARENT;
                                        mDropListener.drop(mSrcDragPos, mDragPos, currenLayerType);
                                    } else {
                                        currenLayerType = LAYER_NONE;
                                        mDropListener.drop(mSrcDragPos, mDragPos, currenLayerType);
                                    }
                                } else {
                                    currenLayerType = LAYER_NONE;
                                    mDropListener.drop(mSrcDragPos, mDragPos, currenLayerType);
                                }
                            } else if (-distance > width / 3) {
                                //左滑变成子项
                                currenLayerType = LAYER_CHILD;
                                mDropListener.drop(mSrcDragPos, mDragPos, currenLayerType);
                            } else {
                                currenLayerType = LAYER_NONE;
                                mDropListener.drop(mSrcDragPos, mDragPos, currenLayerType);
                            }

                        }
                        break;

                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        if (!isRefreshIng()) {
//                            TouchDragDownActivity.instance.getSwipeRefreshLayout().setEnabled(false);
                            setRefreshEnable(false);
                        }
                        int x = (int) ev.getX();
                        int y = (int) ev.getY();
                        dragView(x, y);
                        int itemnum = getItemForPosition(y);


                        if (itemnum >= 0) {
//                            System.out.println("111111111111");

                            if (action == MotionEvent.ACTION_DOWN || itemnum != mDragPos) {
//                                if (mDragListener != null) {
//                                    mDragListener.drag(mDragPos, itemnum);
//                                }
                                mDragPos = itemnum;
                                doExpansion();

                            }
                            int speed = 0;
                            adjustScrollBounds(y);
                            if (y > mLowerBound) {
                                // scroll the list up a bit
                                if (getLastVisiblePosition() < getCount() - 1) {
                                    speed = y > (mHeight + mLowerBound) / 2 ? 16 : 4;
                                } else {
                                    speed = 1;
                                }


                            } else if (y < mUpperBound) {
                                // scroll the list down a bit
                                speed = y < mUpperBound / 2 ? -16 : -4;
                                if (getFirstVisiblePosition() == 0
                                        && getChildAt(0).getTop() >= getPaddingTop()) {
                                    // if we're already at the top, don't try to scroll, because
                                    // it causes the framework to do some extra drawing that messes
                                    // up our animation
                                    speed = 0;

                                }
                            }
                            if (speed != 0) {
                                smoothScrollBy(speed, 30);
                            }
                        }
                        break;
                }
                return true;
            }
        }


        //否则直接交给ListView来处理onTouchEvent事件
        return super.onTouchEvent(ev);
    }

    /************item滑动时的一些操作****************/
    /**
     * 往右滑动，getScrollX()返回的是左边缘的距离，就是以View左边缘为原点到开始滑动的距离，所以向右边滑动为负值
     */

    private void scrollRight() {
        isShowMenu = false;
        removeDirection = RemoveDirection.RIGHT;
        final int delta = (screenWidth + itemView.getScrollX());
        // 调用startScroll方法来设置一些滚动的参数，我们在computeScroll()方法中调用scrollTo来滚动item
        scroller.startScroll(itemView.getScrollX(), 0, -delta, 0,
                Math.abs(delta));
//        mRemoveListener.showSlideMenu(removeDirection, slidePosition,isShowMenu);
        postInvalidate(); // 刷新itemView
    }

    /**
     * 向左滑动，根据上面知道向左滑动为正值
     */
    private void scrollLeft() {
        isShowMenu = true;
        removeDirection = RemoveDirection.LEFT;
        final int delta = (screenWidth - itemView.getScrollX());
        // 调用startScroll方法来设置一些滚动的参数，我们在computeScroll()方法中调用scrollTo来滚动item
        scroller.startScroll(itemView.getScrollX(), 0, delta, 0,
                Math.abs(delta));
//        mRemoveListener.showSlideMenu(removeDirection, slidePosition,isShowMenu);
        postInvalidate(); // 刷新itemView

    }

    /**
     * 滑动会原来的位置
     */
    private void scrollBack() {
        removeDirection = RemoveDirection.NONE;
        scroller.startScroll(itemView.getScrollX(), 0, -itemView.getScrollX(), 0,
                Math.abs(itemView.getScrollX()));
        postInvalidate(); // 刷新itemView
    }

    /**
     * 根据手指滚动itemView的距离来判断是滚动到开始位置还是向左或者向右滚动
     */
    private void scrollByDistanceX() {
        // 如果向左滚动的距离大于屏幕的三分之一，就让其删除
        if (itemView.getScrollX() >= screenWidth / 3) {
            scrollLeft();
        } else if (itemView.getScrollX() <= -screenWidth / 3) {
            scrollRight();
        } else {
            // 滚回到原始位置,为了偷下懒这里是直接调用scrollTo滚动
//            itemView.scrollTo(0, 0);
            // 滚回到原始位置
            scrollBack();
        }

    }

    @Override
    public void computeScroll() {
        // 调用startScroll的时候scroller.computeScrollOffset()返回true，
        if (scroller.computeScrollOffset()) {
            // 让ListView item根据当前的滚动偏移量进行滚动
            itemView.scrollTo(scroller.getCurrX(), scroller.getCurrY());

//            itemView.setAlpha(1f - Math.abs((float) scroller.getCurrX() / screenWidth));

            postInvalidate();

            // 滚动动画结束的时候调用回调接口
            if (scroller.isFinished() && removeDirection != RemoveDirection.NONE) {
                if (mRemoveListener == null) {
                    throw new NullPointerException("RemoveListener is null, we should called setRemoveListener()");
                }

                itemView.scrollTo(0, 0);
//                itemView.setAlpha(1f);
//                mRemoveListener.showSlideMenu(removeDirection, slidePosition, isShowMenu);
            }
        }
    }

    /**
     * 添加用户的速度跟踪器
     *
     * @param event
     */
    private void addVelocityTracker(MotionEvent event) {
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }

        velocityTracker.addMovement(event);
    }

    /**
     * 移除用户速度跟踪器
     */
    private void recycleVelocityTracker() {
        if (velocityTracker != null) {
            velocityTracker.recycle();
            velocityTracker = null;
        }
    }

    /**
     * 获取X方向的滑动速度,大于0向右滑动，反之向左
     *
     * @return
     */
    private int getScrollVelocity() {
        velocityTracker.computeCurrentVelocity(1000);
        int velocity = (int) velocityTracker.getXVelocity();
        return velocity;
    }


    /*****
     * 拖动时候的一些操作
     *********/
    /*
     * pointToPosition() doesn't consider invisible views, but we
     * need to, so implement a slightly different version.
     */
    private int myPointToPosition(int x, int y) {

        if (y < 0) {
            // when dragging off the top of the screen, calculate position
            // by going back from a visible item
            int pos = myPointToPosition(x, y + mItemHeightNormal);
            if (pos > 0) {
                return pos - 1;
            }
        }

        Rect frame = mTempRect;
        final int count = getChildCount();
        for (int i = count - 1; i >= 0; i--) {
            final View child = getChildAt(i);
            child.getHitRect(frame);
            if (frame.contains(x, y)) {
                return getFirstVisiblePosition() + i;
            }
        }
        return INVALID_POSITION;
    }

    private int getItemForPosition(int y) {
        int adjustedy = y - mDragPointY - mItemHeightHalf;
        int pos = myPointToPosition(0, adjustedy);
        if (pos >= 0) {
            if (pos <= mSrcDragPos) {
                pos += 1;
            }
        } else if (adjustedy < 0) {
            // this shouldn't happen anymore now that myPointToPosition deals
            // with this situation
            pos = 0;
        }
        return pos;
    }

    private void adjustScrollBounds(int y) {
        if (y >= mHeight / 3) {
            mUpperBound = mHeight / 3;
        }
        if (y <= mHeight * 2 / 3) {
            mLowerBound = mHeight * 2 / 3;
        }
    }

    /*
     * Restore size and visibility for all listitems
     */
    private void unExpandViews(boolean deletion) {
        for (int i = 0; ; i++) {
            View v = getChildAt(i);
            if (v == null) {
                if (deletion) {
                    // HACK force update of mItemCount
                    int position = getFirstVisiblePosition();
                    int y = getChildAt(0).getTop();
                    setAdapter(getAdapter());
                    setSelectionFromTop(position, y);
                    // end hack
                }
                try {
                    layoutChildren(); // force children to be recreated where needed
                    v = getChildAt(i);
                } catch (IllegalStateException ex) {
                    // layoutChildren throws this sometimes, presumably because we're
                    // in the process of being torn down but are still getting touch
                    // events
                }
                if (v == null) {
                    return;
                }
            }
            ViewGroup.LayoutParams params = v.getLayoutParams();
            params.height = mItemHeightNormal;
            v.setLayoutParams(params);
            v.setVisibility(View.VISIBLE);
        }
    }

    /* Adjust visibility and size to make it appear as though
     * an item is being dragged around and other items are making
     * room for it:
     * If dropping the item would result in it still being in the
     * same place, then make the dragged listitem's size normal,
     * but make the item invisible.
     * Otherwise, if the dragged listitem is still on screen, make
     * it as small as possible and expand the item below the insert
     * point.
     * If the dragged item is not on screen, only expand the item
     * below the current insertpoint.
     */
    private void doExpansion() {
        boolean isChange;
        int childnum = mDragPos - getFirstVisiblePosition();
        if (mDragPos > mSrcDragPos) {
            childnum++;
        }
        int numheaders = getHeaderViewsCount();

        View first = getChildAt(mSrcDragPos - getFirstVisiblePosition());
        for (int i = 0; ; i++) {
            View vv = getChildAt(i);
            if (vv == null) {
                break;
            }
            isChange = false;
            int height = mItemHeightNormal;
            int visibility = View.VISIBLE;
            if (mDragPos < numheaders && i == numheaders) {
                // dragging on top of the header item, so adjust the item below
                // instead
                if (vv.equals(first)) {
                    visibility = View.INVISIBLE;
//                    System.out.println("========");
                } else {
                    height = mItemHeightExpanded;
//                    System.out.println("-------" + mItemHeightExpanded);
                }
            } else if (vv.equals(first)) {
                // processing the item that is being dragged
                if (mDragPos == mSrcDragPos || getPositionForView(vv) == getCount() - 1) {
                    // hovering over the original location
                    visibility = View.INVISIBLE;
//                    System.out.println("...........");
                } else {
                    // not hovering over it
                    // Ideally the item would be completely gone, but neither
                    // setting its size to 0 nor settings visibility to GONE
                    // has the desired effect.
                    height = 1;
//                    System.out.println("***********");
                }
            } else if (i == childnum) {
                if (mDragPos >= numheaders && mDragPos < getCount() - 1) {
                    height = mItemHeightExpanded;
//                    System.out.println("/////////////" + height);
                    isChange = true;

                }
            }

            ViewGroup.LayoutParams params = vv.getLayoutParams();
            params.height = height;
            vv.setLayoutParams(params);
            vv.setVisibility(visibility);
            if (isChange) {
                RelativeLayout rl = (RelativeLayout) vv.findViewById(R.id.broder);
                RelativeLayout ry = (RelativeLayout) vv.findViewById(R.id.rl_partent);

                if (ry != null && rl != null) {
                    rl.setVisibility(View.VISIBLE);
                    if (first != null) {
                        RelativeLayout currenRy = (RelativeLayout) first.findViewById(R.id.rl_partent);
                        if (currenRy != null && currenRy.getLeft() == SUBMARGIN) {
                            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) rl.getLayoutParams();
                            lp.setMargins(TouchInterceptorListView.SUBMARGIN, 0, 0, 0);
                            rl.setLayoutParams(lp);
                        }
                    }
//                    if (ry.getLeft() == SUBMARGIN) {
//                        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) rl.getLayoutParams();
//                        lp.setMargins(TouchInterceptorListView.SUBMARGIN, 0, 0, 0);
//                        rl.setLayoutParams(lp);
//                    }
                }
            } else {
                RelativeLayout rl = (RelativeLayout) vv.findViewById(R.id.broder);
                if (rl != null) {
                    rl.setVisibility(View.GONE);
                }
            }
        }
    }


    private void startDragging(Bitmap bm, int x, int y) {


        mVibrator.vibrate(mPattern, -1);
        stopDragging();

        mWindowParams = new WindowManager.LayoutParams();
        mWindowParams.gravity = Gravity.TOP | Gravity.LEFT;
        mWindowParams.x = x - mDragPointX + mXOffset + -itemMargin;

        if (isChangY)
            mWindowParams.y = y - mDragPointY + mYOffset;
        isChangY = false;

        mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        mWindowParams.format = PixelFormat.TRANSLUCENT;
        mWindowParams.windowAnimations = 0;

        Context context = getContext();
        ImageView v = new ImageView(context);
        //int backGroundColor = context.getResources().getColor(R.color.dragndrop_background);
        //v.setBackgroundColor(backGroundColor);
//        v.setBackgroundResource(R.drawable.playlist_tile_drag);
        v.setPadding(0, 0, 0, 0);
        v.setImageBitmap(bm);
        mDragBitmap = bm;

        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mWindowManager.addView(v, mWindowParams);
        mDragView = v;
    }

    private void dragView(int x, int y) {

        if (mRemoveMode == SLIDE) {
            float alpha = 1.0f;
            int width = mDragView.getWidth();
            if (x > width / 2) {
                alpha = ((float) (width - x)) / (width / 2);
            }
            mWindowParams.alpha = alpha;
        }

        if (mRemoveMode == FLING || mRemoveMode == TRASH) {
            mWindowParams.x = x - mDragPointX + mXOffset;
        } else {
            mWindowParams.x = 0;
        }
        mWindowParams.y = y - mDragPointY + mYOffset;
        mWindowManager.updateViewLayout(mDragView, mWindowParams);

        if (mTrashcan != null) {
            int width = mDragView.getWidth();
            if (y > getHeight() * 3 / 4) {
                mTrashcan.setLevel(2);
            } else if (width > 0 && x > width / 4) {
                mTrashcan.setLevel(1);
            } else {
                mTrashcan.setLevel(0);
            }
        }
    }

    private void stopDragging() {
        if (!isRefreshIng()) {
//            TouchDragDownActivity.instance.getSwipeRefreshLayout().setEnabled(true);
            setRefreshEnable(true);
        }
        if (mDragView != null) {
            mDragView.setVisibility(GONE);
            WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            wm.removeView(mDragView);
            mDragView.setImageDrawable(null);
            mDragView = null;
        }
        if (mDragBitmap != null) {
            mDragBitmap.recycle();
            mDragBitmap = null;
        }
        if (mTrashcan != null) {
            mTrashcan.setLevel(0);
        }
    }

    /**
     * 当前是否正在刷新
     */
    private boolean isRefreshIng() {
        if (TouchDragDownActivity.instance != null
                &&
                TouchDragDownActivity.instance.getSwipeRefreshLayout() != null
                &&
                TouchDragDownActivity.instance.getSwipeRefreshLayout().mCircleView != null
                &&
                TouchDragDownActivity.instance.getSwipeRefreshLayout().mCircleView.getVisibility() > 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 禁用刷新
     */
    private void setRefreshEnable(boolean enable) {
        TouchDragDownActivity.instance.getSwipeRefreshLayout().setEnabled(enable);
    }
}
