package com.nicnick.sensortabindicator.view;

import android.graphics.Path;

/**
 * @author xiazicheng
 * @date 16/10/2.
 */

public class TriangleIndicator {

    public static final float RADIO_TRIANGLE_WIDTH = 1 / 6F;//indicator三角形底边和Tab宽度的比例
    private static TriangleIndicator triangleIndicator = null;
    public int mTriangleWidth;
    public int mTriangleHeight;

    public int mInitTranslationX;
    private Path mPath;

    /**
     * 单例模式
     *
     * @return 返回一个TriangleIndicator的实例
     */
    public static TriangleIndicator getTriangleIndicator() {
        if (triangleIndicator == null) {
            synchronized (TriangleIndicator.class) {
                if (triangleIndicator == null) {
                    triangleIndicator = new TriangleIndicator();
                }
            }
        }
        return triangleIndicator;
    }

    public void setParameters(int w, int tabCount) {
        mTriangleWidth = (int) (w / tabCount * RADIO_TRIANGLE_WIDTH);
        mTriangleWidth = Math.min(mTriangleWidth, 60);
        mTriangleHeight = mTriangleWidth / 2;
        mInitTranslationX = w / tabCount / 2 - mTriangleWidth / 2;
    }

    public void initTriangle() {
        mPath = new Path();
        mPath.moveTo(0, 0);
        //画一个三角形
        mPath.lineTo(mTriangleWidth, 0);
        mPath.lineTo(mTriangleWidth / 2, -mTriangleHeight);
        mPath.lineTo(0, 0);
        mPath.close();
    }

    public Path getPath() {
        return mPath;
    }
}
