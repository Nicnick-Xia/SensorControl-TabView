package com.nicnick.sensortabindicator.view;

import android.graphics.CornerPathEffect;
import android.graphics.Paint;

/**
 * @author xiazicheng
 * @date 16/10/2.
 */

public class IndicatorPainter {

    /**
     * 默认颜色为白色
     */
    public static final int DEFAULT_COLOR = 0xffffffff;

    public static Paint getPaint(int color) {

        Paint paint;

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        paint.setPathEffect(new CornerPathEffect(3));

        return paint;

    }

}
