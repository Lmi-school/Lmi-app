package com.students.lmi.lmi_app;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

public class MyScrollView extends ScrollView {

    public interface OnScrollViewListener {

        void onScrollChanged(int l, int t, int oldl, int oldt);

        void onScrollBottomDetect();

        void onScrollTopDetect();
    }

    private OnScrollViewListener onScrollViewChange;

    public MyScrollView(Context context) {
        super(context);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        View view = (View) getChildAt(getChildCount() - 1);
        if (onScrollViewChange != null) {
            onScrollViewChange.onScrollChanged(l, t, oldl, oldt);
            int diff = view.getBottom() - (getHeight() + getScrollY());
            if (diff == 0) {
                onScrollViewChange.onScrollBottomDetect();
            }
            diff = view.getTop() - getScrollY();
            if (diff == 0) {
                onScrollViewChange.onScrollTopDetect();
            }
        }
        super.onScrollChanged(l, t, oldl, oldt);
    }

    public OnScrollViewListener getOnScrollViewChange() {
        return onScrollViewChange;
    }

    public void setOnScrollViewChange(OnScrollViewListener onScrollViewChange) {
        this.onScrollViewChange = onScrollViewChange;
    }
}