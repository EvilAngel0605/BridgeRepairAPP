package net.jsrbc.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import net.jsrbc.R;
import net.jsrbc.utils.SystemUtils;

/**
 * Created by ZZZ on 2017-12-05.
 */
public class SlidingMenu extends HorizontalScrollView {

    private static final float ratio = 0.2F;

    private final int menuCount;

    private final int screenWidth;

    private final int menuWidth;

    private boolean once = true;

    private OnMenuSlidingListener menuSlidingListener;

    /** 菜单滑开监听 */
    public interface OnMenuSlidingListener {
        void onMenuOpen(SlidingMenu slidingMenu);
    }

    /** 设置菜单打开监听事件 */
    public void setOnMenuSlidingListener(OnMenuSlidingListener menuSlidingListener) {
        this.menuSlidingListener = menuSlidingListener;
    }

    /** 关闭菜单 */
    public void closeMenu() {
        this.smoothScrollTo(0, 0);
    }

    public SlidingMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        //获取菜单数量
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SlidingMenu);
        menuCount = typedArray.getInt(R.styleable.SlidingMenu_menu_count, 0);
        typedArray.recycle();
        //获取屏幕宽度以及横向滚动样式等
        screenWidth = SystemUtils.getScreenWidth(context);
        menuWidth = (int)(screenWidth * ratio);
        setOverScrollMode(View.OVER_SCROLL_NEVER);
        setHorizontalScrollBarEnabled(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (once) {
            if (getChildCount() > 0) {
                LinearLayout wrapper = (LinearLayout)getChildAt(0);
                if (wrapper.getChildCount() == menuCount + 1) {
                    wrapper.getChildAt(0).getLayoutParams().width = screenWidth;
                    for (int i=1; i<=menuCount; i++) {
                        wrapper.getChildAt(i).getLayoutParams().width = menuWidth;
                    }
                }
            }
            once = false;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP: {
                int scrollX = getScrollX();
                if (Math.abs(scrollX) > (menuWidth * menuCount) / 2) {
                    this.smoothScrollTo(menuWidth * menuCount, 0);
                    if (menuSlidingListener != null) menuSlidingListener.onMenuOpen(this);
                } else {
                    this.smoothScrollTo(0, 0);
                }
                return true;
            }
        }
        return super.onTouchEvent(ev);
    }
}
