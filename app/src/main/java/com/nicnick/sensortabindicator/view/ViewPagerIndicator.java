package com.nicnick.sensortabindicator.view;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;

import com.nicnick.sensortabindicator.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiazicheng
 * @date 16/9/29.
 */

public class ViewPagerIndicator extends LinearLayout {

    public static final int COUNT_DEFAULT_TAB = 4;
    private static final String INSTANCE = "instance";
    private static final String MYPOSITION = "myPosition";
    private static final String LEFT_CURSOR = "leftCursor";
    private static final int UPTATE_INTERVAL_TIME = 100; //两次检测的时间间隔
    private static boolean isSave = false;//是否切屏的标识

    /**
     * 加速度传感器的相关变量
     */
    private static final float TMAX = 4.0f;
    private static final float TMIN = -2.0f;
    float lastX = 0;
    float lastY = 0;
    float lastZ = 0;

    private int mTabVisibleCount;
    private int totalCount;
    private List<String> mTitles = new ArrayList<>();
    private ViewPager mViewPager;
    private TriangleIndicator mTriangleIndicator;
    private TextTab mTextTab;
    private Paint mPaint;
    private int mTranslationX;//指示器的累计位移量
    private int lastValue = -1;//用于判断左右滑动
    private int rightCursor;//当前可见Tab列的右端点位置
    private int leftCursor;//当前可见Tab列的左端点位置
    private int myPosition;//用来记录当前选择的position
    private long lastUpdateTime;//上次检测时间
    private SensorManager mSensorManager;
    private OnPageChangeListener mListener;
    /**
     * 加速度传感器的监听器
     */
    private SensorEventListener sensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
                return;
            }
            //现在检测时间
            long currentUpdateTime = System.currentTimeMillis();
            //判断是否达到了检测时间间隔
            if ((currentUpdateTime - lastUpdateTime) < UPTATE_INTERVAL_TIME)
                return;
            //现在的时间变成last时间
            lastUpdateTime = currentUpdateTime;

            //获取加速度数值，以下三个值为重力分量在设备坐标的分量大小
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            int cCount = mTitles.size();
            int currentPage = mViewPager.getCurrentItem();
            int newPage;

            if (x < TMIN && (lastX - x) > TMAX) {
                newPage = currentPage + 1;
                if (newPage <= cCount) {
                    mViewPager.setCurrentItem(newPage);
                }
                //Log.i(TAG, "onSensorChanged: turn left");
            } else if (x > -TMIN && (lastX - x) < -TMAX) {
                newPage = currentPage - 1;
                if (newPage >= 0) {
                    mViewPager.setCurrentItem(newPage);
                }
                //Log.i(TAG, "onSensorChanged: turn right");
            }

            lastX = x;
            lastY = y;
            lastZ = z;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    public ViewPagerIndicator(Context context) {
        this(context, null);
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        //获取XML定义的参数
        initParameters(context, attrs);
        //初始化画笔
        mPaint = IndicatorPainter.getPaint(IndicatorPainter.DEFAULT_COLOR);
    }

    /**
     * 初始化变量,从XML文件中获取一些必要参数
     *
     * @param context 上下文对象
     * @param attrs   描述集合
     */
    private void initParameters(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ViewPagerIndicator);

        mTabVisibleCount = a.getInt(R.styleable.ViewPagerIndicator_visible_tab_count, COUNT_DEFAULT_TAB);
        if (mTabVisibleCount < 0)
            mTabVisibleCount = COUNT_DEFAULT_TAB;

        //使用完之后要进行回收
        a.recycle();

        mTextTab = new TextTab(context, attrs);

        leftCursor = 0;
        rightCursor = mTabVisibleCount - 1;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(mTriangleIndicator.mInitTranslationX + mTranslationX, getHeight() + 3);
        canvas.drawPath(mTriangleIndicator.getPath(), mPaint);
        canvas.restore();
        super.dispatchDraw(canvas);
    }

    /**
     * 这个方法在每次绘制View的时候都会被调用,所以可以将绘制自定义图形的代码放置于此
     *
     * @param w    新宽度
     * @param h    新高度
     * @param oldw 旧宽度
     * @param oldh 旧高度
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        //如果当前状态为横屏,则需要重设mTabVisibleCount的数量
        if (getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            mTabVisibleCount = mTabVisibleCount * metrics.widthPixels / metrics.heightPixels;
            rightCursor = leftCursor + mTabVisibleCount - 1;
        }

        mTriangleIndicator = TriangleIndicator.getTriangleIndicator();
        mTriangleIndicator.setParameters(w, mTabVisibleCount);
        mTriangleIndicator.initTriangle();
        mTextTab.highLightTab(myPosition, this);

    }

    /**
     * 根据可见Tab的数量在每次加载Tab的View时进行宽度的调整
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        int cCount = getChildCount();
        if (cCount == 0) return;

        for (int i = 0; i < cCount; i++) {
            View view = getChildAt(i);
            LayoutParams lp = (LayoutParams) view.getLayoutParams();
            lp.weight = 0;
            lp.width = getScreenWidth() / mTabVisibleCount;
        }
        setItemClickEvent();
    }

    /**
     * 指示器跟随手指进行滚动
     *
     * @param position 当前Tab的位置
     * @param offset   位移量
     * @param isLeft   是否向左滑动
     */
    public void scroll(int position, float offset, boolean isLeft) {
        int tabWidth = getWidth() / mTabVisibleCount;
        //Log.i(TAG, "scroll: left-right: "+leftCursor+" - "+rightCursor+"  positon="+position);
        /**
         * 如果当前位置在左端点+1,同时滑动方向为向右且未到Tab起点时,
         * 触发Tab列的scrollTo()方法,向右滑动Tab列,同时重设左右游标
         */
        if (position <= leftCursor + 1 && offset > 0 && !isLeft && position != 0) {
            this.scrollTo((int) (tabWidth * offset + tabWidth * (position - 1)), 0);
            leftCursor = position - 1;
            rightCursor = position + mTabVisibleCount - 2;
        }
        /**
         * 如果当前位置在右端点-1,同时滑动方向为向左且未到Tab末尾时,
         * 触发Tab列的scrollTo()方法,向左滑动Tab列,同时重设左右游标
         */
        if (position >= (rightCursor - 1) && offset > 0 && isLeft && position != (totalCount - 2)) {
            this.scrollTo((int) (tabWidth * offset + tabWidth * (position - mTabVisibleCount + 2)), 0);
            leftCursor = position - mTabVisibleCount + 2;
            rightCursor = position + 1;
        }
        mTranslationX = (int) (tabWidth * (offset + position));
        invalidate();
    }

    /**
     * 获取屏幕的宽度,用于设置Tab的宽度
     *
     * @return 屏幕的像素宽度
     */
    public int getScreenWidth() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return metrics.widthPixels;
    }

    public void setmTabVisibleCount(int visibleCount) {
        this.mTabVisibleCount = visibleCount;
        rightCursor = mTabVisibleCount - 1;
    }

    public int getTabVisibleCount() {
        return mTabVisibleCount;
    }

    /**
     * 自动设置添加的Tab
     *
     * @param titles Tab的题目组成的一个字符串List
     */
    public void setTabTitle(List<String> titles) {
        Context context = getContext();
        if (titles != null && titles.size() > 0) {
            this.removeAllViews();
            mTitles = titles;
            totalCount = mTitles.size();
            for (String title : mTitles) {
                addView(mTextTab.getTextViewTabs(context, title, mTabVisibleCount));
            }
            setItemClickEvent();
        }
    }

    /**
     * 设置Tab的点击事件
     */
    public void setItemClickEvent() {
        int cCount = getChildCount();
        for (int i = 0; i < cCount; i++) {
            final int j = i;
            View view = getChildAt(i);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewPager.setCurrentItem(j);
                }
            });
        }
    }

    /**
     * 在ViewPagerIndicator中集成ViewPager
     *
     * @param vp       定义好的 ViewPager
     * @param position 默认首位
     */
    public void setViewPager(ViewPager vp, int position) {
        mViewPager = vp;
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                myPosition = position;
                //Log.i(TAG, "onPageScrolled: positionOffsetPixels=" + positionOffsetPixels + "   lastValue=" + lastValue);
                if (positionOffset > 0) {
                    if (lastValue > positionOffsetPixels) {
                        //递减,向右滑动
                        //Log.i(TAG, "onPageScrolled: scroll right");
                        scroll(position, positionOffset, false);
                    } else if (lastValue < positionOffsetPixels) {
                        //递增,向左滑动
                        //Log.i(TAG, "onPageScrolled: scroll left");
                        scroll(position, positionOffset, true);
                    }
                }
                //如果用户想使用原来的接口,这里就需要提供原有接口的回调,下面几个方法都相同
                if (mListener != null) {
                    mListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }
                lastValue = positionOffsetPixels;
            }

            @Override
            public void onPageSelected(int position) {
                if (mListener != null) {
                    mListener.onPageSelected(position);
                }
                mTextTab.highLightTab(position, ViewPagerIndicator.this);
                //Log.i("Tag","Page" + position + " is selected.");
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //Log.i(TAG, "onPageScrollStateChanged: state=" + state);
                //state = SCROLLBAR_POSITION_RIGHT
                if (mListener != null) {
                    mListener.onPageScrollStateChanged(state);
                }

            }
        });
        mViewPager.setCurrentItem(position);
        mTextTab.highLightTab(position, this);
    }

    /**
     * 如果其他开发者想调用原有的OnPageChangeListen接口,我们就需要提供一个接口的回调
     *
     * @param onPageChangeListener 和原有的接口相同
     */
    public void setOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
        this.mListener = onPageChangeListener;
    }

    /**
     * 重写这个方法在bundle里存入当前位置和左端点位置的信息,横竖屏切换时保留原来位置
     *
     * @return 存值的bundle
     */
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE, super.onSaveInstanceState());
        bundle.putInt(MYPOSITION, myPosition);
        bundle.putInt(LEFT_CURSOR, leftCursor);
        isSave = true;
        return bundle;
    }

    /**
     * 界面重绘后,取出原来的位置信息
     *
     * @param state 当前状态
     */
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            myPosition = bundle.getInt(MYPOSITION);
            leftCursor = bundle.getInt(LEFT_CURSOR);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE));
            return;
        }
        super.onRestoreInstanceState(state);
    }

    /**
     * 重绘界面,重要的地方在于,横屏之后为了保证每个Tab所占宽度和原来相差不大,会重新确定一个tabVisibleCount
     *
     * @param canvas 画布对象
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isSave) {
            int tabWidth = getWidth() / mTabVisibleCount;
            if (myPosition >= mTabVisibleCount)
                this.scrollTo((myPosition - mTabVisibleCount + 1) * tabWidth, 0);
            isSave = false;
            mTextTab.refreshTab(getContext(), this);
            mTranslationX = tabWidth * myPosition;
            mViewPager.setCurrentItem(myPosition);
        }
    }

    /**
     * 封装好设置加速度传感器的一系列操作
     *
     * @param context 上下文对象
     */
    public void setGravitySensor(Context context) {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        Sensor gravitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (gravitySensor != null) {
            mSensorManager.registerListener(sensorListener, gravitySensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    /**
     * 封装好注销加速度传感器监听器的操作,需要在Activity的onPause()方法中调用
     */
    public void unregisterSensorListener() {
        mSensorManager.unregisterListener(sensorListener);
    }

    /**
     * 重新声明一个接口供外部调用
     */
    public interface OnPageChangeListener {

        void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

        void onPageSelected(int position);

        void onPageScrollStateChanged(int state);

    }
}
