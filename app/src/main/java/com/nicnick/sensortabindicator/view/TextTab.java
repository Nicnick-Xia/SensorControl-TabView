package com.nicnick.sensortabindicator.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nicnick.sensortabindicator.R;

/**
 * @author xiazicheng
 * @date 16/10/3.
 */

class TextTab extends Tab {

    private static final int TAB_DEFAULT_TEXTSIZE = 40;
    private static final int TITLE_DEFAULT_COLOR = 0x77FFFFFF;
    private static final int TITLE_HIGHLIGHT_COLOR = 0xFFFFFFFF;

    private int mTabTextSize;
    private int mTextDefaultColor;
    private int mTextHighlightColor;

    TextTab(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ViewPagerIndicator);

        mTabTextSize = (int) a.getDimension(R.styleable.ViewPagerIndicator_tab_textSize, TAB_DEFAULT_TEXTSIZE);
        mTextDefaultColor = a.getColor(R.styleable.ViewPagerIndicator_tab_text_default_color, TITLE_DEFAULT_COLOR);
        mTextHighlightColor = a.getColor(R.styleable.ViewPagerIndicator_tab_text_highlight_color, TITLE_HIGHLIGHT_COLOR);

        //使用完之后要进行回收
        a.recycle();
    }

    /**
     * 生成一个TextView的Tab
     *
     * @param title Tab的文字
     * @return 一个根据样式生成的TextView
     */
    View getTextViewTabs(Context context, String title, int tabVisibleCount) {
        TextView tv = new TextView(context);
        int screenWidth = getScreenWidth(context);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.width = screenWidth / tabVisibleCount;
        tv.setText(title);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTabTextSize);
        tv.setTextColor(mTextDefaultColor);
        tv.setLayoutParams(lp);
        return tv;
    }

    /**
     * 使得所有的Tab恢复为未选中的状态
     *
     * @param vpi 当前的ViewPagerIndicator对象
     */
    private void resetTabs(ViewPagerIndicator vpi) {
        int cCount = vpi.getChildCount();
        for (int i = 0; i < cCount; i++) {
            View view = vpi.getChildAt(i);
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(mTextDefaultColor);
            }
        }
    }

    /**
     * 使所有的Tab重新调整参数,例如宽度
     *
     * @param context 上下文
     * @param vpi     Tab所在的ViewPagerIndicator
     */
    void refreshTab(Context context, ViewPagerIndicator vpi) {
        View view;
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.width = getScreenWidth(context) / vpi.getTabVisibleCount();
        int cCount = vpi.getChildCount();
        for (int i = 0; i < cCount; i++) {
            view = vpi.getChildAt(i);
            view.setLayoutParams(lp);
        }
    }

    /**
     * 设置当前Tab为高亮
     *
     * @param position 当前Tab的位置
     */
    void highLightTab(int position, ViewPagerIndicator vpi) {
        resetTabs(vpi);
        View view = vpi.getChildAt(position);
        if (view instanceof TextView) {
            ((TextView) view).setTextColor(mTextHighlightColor);
        }
    }

    /**
     * 获取屏幕宽度(用于重设Tab宽度)
     *
     * @param context 上下文对象
     * @return 屏幕像素宽度
     */
    private int getScreenWidth(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.widthPixels;
    }

}
