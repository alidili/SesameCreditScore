package com.yang.creditscore;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

/**
 * 芝麻信用分
 * Created by yangle on 2016/9/26.
 */
public class CreditScoreView extends View {

    //数据个数
    private int dataCount = 5;
    //每个角的弧度
    private float radian = (float) (Math.PI * 2 / dataCount);
    //雷达图半径
    private float radius;
    //中心X坐标
    private int centerX;
    //中心Y坐标
    private int centerY;
    //各维度标题
    private String[] titles = {"履约能力", "信用历史", "人脉关系", "行为偏好", "身份特质"};
    //各维度图标
    private int[] icons = {R.mipmap.ic_performance, R.mipmap.ic_history, R.mipmap.ic_contacts,
            R.mipmap.ic_predilection, R.mipmap.ic_identity};
    //各维度分值
    private float[] data = {1000, 800, 600, 400, 600};
    //数据最大值
    private float maxValue = 1000;
    //雷达图与标题的间距
    private int radarMargin = DensityUtils.dp2px(getContext(), 15);
    //雷达区画笔
    private Paint mainPaint;
    //数据区画笔
    private Paint valuePaint;
    //标题画笔
    private Paint titlePaint;
    //图标画笔
    private Paint iconPaint;
    //标题文字大小
    private int titleSize = DensityUtils.dp2px(getContext(), 13);

    public CreditScoreView(Context context) {
        this(context, null);
    }

    public CreditScoreView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CreditScoreView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mainPaint = new Paint();
        mainPaint.setAntiAlias(true);
        mainPaint.setColor(Color.WHITE);
        mainPaint.setStyle(Paint.Style.STROKE);

        valuePaint = new Paint();
        valuePaint.setAntiAlias(true);
        valuePaint.setColor(Color.WHITE);
        valuePaint.setAlpha(120);
        valuePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        titlePaint = new Paint();
        titlePaint.setAntiAlias(true);
        titlePaint.setTextSize(titleSize);
        titlePaint.setColor(Color.WHITE);
        titlePaint.setStyle(Paint.Style.FILL);

        iconPaint = new Paint();
        iconPaint.setAntiAlias(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        radius = Math.min(h, w) / 2 * 0.5f;
        //中心坐标
        centerX = w / 2;
        centerY = h / 2;
        postInvalidate();
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        drawPolygon(canvas);
        drawLines(canvas);
        drawRegion(canvas);
        drawTitle(canvas);
        drawIcon(canvas);
    }

    /**
     * 绘制多边形
     *
     * @param canvas 画布
     */
    private void drawPolygon(Canvas canvas) {
        Path path = new Path();
        for (int i = 0; i < dataCount; i++) {
            if (i == 0) {
                path.moveTo(getPoint(i).x, getPoint(i).y);
            } else {
                path.lineTo(getPoint(i).x, getPoint(i).y);
            }
        }

        //闭合路径
        path.close();
        canvas.drawPath(path, mainPaint);
    }

    /**
     * 绘制连接线
     *
     * @param canvas 画布
     */
    private void drawLines(Canvas canvas) {
        Path path = new Path();
        for (int i = 0; i < dataCount; i++) {
            path.reset();
            path.moveTo(centerX, centerY);
            path.lineTo(getPoint(i).x, getPoint(i).y);
            canvas.drawPath(path, mainPaint);
        }
    }

    /**
     * 绘制覆盖区域
     *
     * @param canvas 画布
     */
    private void drawRegion(Canvas canvas) {
        Path path = new Path();

        for (int i = 0; i < dataCount; i++) {
            float percent = data[i] / maxValue;
            int x = getPoint(i, 0, percent).x;
            int y = getPoint(i, 0, percent).y;
            if (i == 0) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
        }
        valuePaint.setStyle(Paint.Style.STROKE);
        path.close();
        canvas.drawPath(path, valuePaint);

        //绘制填充区域
        valuePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawPath(path, valuePaint);
    }

    /**
     * 绘制标题
     *
     * @param canvas 画布
     */
    private void drawTitle(Canvas canvas) {
        for (int i = 0; i < dataCount; i++) {
            int x = getPoint(i, radarMargin, 1).x;
            int y = getPoint(i, radarMargin, 1).y;

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), icons[i]);
            int iconHeight = bitmap.getHeight();
            float titleWidth = titlePaint.measureText(titles[i]);

            if (i == 1) {
                y += (iconHeight / 2);
            } else if (i == 2) {
                x -= titleWidth;
                y += (iconHeight / 2);
            } else if (i == 3) {
                x -= titleWidth;
            } else if (i == 4) {
                x -= titleWidth / 2;
            }
            canvas.drawText(titles[i], x, y, titlePaint);
        }
    }

    /**
     * 绘制图标
     *
     * @param canvas 画布
     */
    private void drawIcon(Canvas canvas) {
        for (int i = 0; i < dataCount; i++) {
            int x = getPoint(i, radarMargin, 1).x;
            int y = getPoint(i, radarMargin, 1).y;

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), icons[i]);
            int iconWidth = bitmap.getWidth();
            int iconHeight = bitmap.getHeight();
            float titleWidth = titlePaint.measureText(titles[i]);

            if (i == 0) {
                x += (titleWidth - iconWidth) / 2;
                y -= (iconHeight + getTitleHeight());
            } else if (i == 1) {
                x += (titleWidth - iconWidth) / 2;
                y -= (iconHeight / 2 + getTitleHeight());
            } else if (i == 2) {
                x -= (iconWidth + (titleWidth - iconWidth) / 2);
                y -= (iconHeight / 2 + getTitleHeight());
            } else if (i == 3) {
                x -= (iconWidth + (titleWidth - iconWidth) / 2);
                y -= (iconHeight + getTitleHeight());
            } else if (i == 4) {
                x -= iconWidth / 2;
                y -= (iconHeight + getTitleHeight());
            }

            canvas.drawBitmap(bitmap, x, y, titlePaint);
        }
    }

    private Point getPoint(int position) {
        return getPoint(position, 0, 1);
    }

    private Point getPoint(int position, float radarMargin, float percent) {
        int x = 0;
        int y = 0;

        if (position == 0) {
            x = (int) (centerX + (radius + radarMargin) * Math.sin(radian) * percent);
            y = (int) (centerY - (radius + radarMargin) * Math.cos(radian) * percent);

        } else if (position == 1) {
            x = (int) (centerX + (radius + radarMargin) * Math.sin(radian / 2) * percent);
            y = (int) (centerY + (radius + radarMargin) * Math.cos(radian / 2) * percent);

        } else if (position == 2) {
            x = (int) (centerX - (radius + radarMargin) * Math.sin(radian / 2) * percent);
            y = (int) (centerY + (radius + radarMargin) * Math.cos(radian / 2) * percent);

        } else if (position == 3) {
            x = (int) (centerX - (radius + radarMargin) * Math.sin(radian) * percent);
            y = (int) (centerY - (radius + radarMargin) * Math.cos(radian) * percent);

        } else if (position == 4) {
            x = centerX;
            y = (int) (centerY - (radius + radarMargin) * percent);
        }

        return new Point(x, y);
    }

    private int getTitleHeight() {
        Paint.FontMetrics fontMetrics = titlePaint.getFontMetrics();
        return (int) (fontMetrics.descent - fontMetrics.ascent);
    }
}
