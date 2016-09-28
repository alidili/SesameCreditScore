package com.yang.creditscore;

import android.content.Context;
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
    private int count = 5;
    //每个角的弧度
    private float radian = (float) (Math.PI * 2 / count);
    //雷达图半径
    private float radius;
    //中心X坐标
    private int centerX;
    //中心Y坐标
    private int centerY;
    //各维度标题
    private String[] titles = {"履约能力", "信用历史", "人脉关系", "行为偏好", "身份特质"};
    //各维度分值
    private float[] data = {100, 80, 60, 40, 60};
    //数据最大值
    private float maxValue = 100;
    //雷达区画笔
    private Paint mainPaint;
    //数据区画笔
    private Paint valuePaint;
    //标题画笔
    private Paint titlePaint;

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
        valuePaint.setAntiAlias(true);
        titlePaint.setTextSize(25);
        titlePaint.setColor(Color.WHITE);
        titlePaint.setStyle(Paint.Style.FILL);
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
    }

    /**
     * 绘制正多边形
     */
    private void drawPolygon(Canvas canvas) {
        Path path = new Path();
        for (int i = 0; i < count; i++) {
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

    private void drawLines(Canvas canvas) {
        Path path = new Path();
        for (int i = 0; i < count; i++) {
            path.reset();
            path.moveTo(centerX, centerY);
            path.lineTo(getPoint(i).x, getPoint(i).y);
            canvas.drawPath(path, mainPaint);
        }
    }

    private void drawRegion(Canvas canvas) {
        Path path = new Path();

        for (int i = 0; i < count; i++) {
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

    private void drawTitle(Canvas canvas) {
        Paint.FontMetrics fontMetrics = titlePaint.getFontMetrics();
        float fontHeight = fontMetrics.descent - fontMetrics.ascent;

        for (int i = 0; i < count; i++) {
            float x = getPoint(i, fontHeight, 1).x;
            float y = getPoint(i, fontHeight, 1).y;

            if (radian * i >= 0 && radian * i <= Math.PI / 2) { //第4象限
                canvas.drawText(titles[i], x, y, titlePaint);

            } else if (radian * i >= 3 * Math.PI / 2 && radian * i <= Math.PI * 2) { //第3象限
                canvas.drawText(titles[i], x, y, titlePaint);

            } else if (radian * i > Math.PI / 2 && radian * i <= Math.PI) { //第2象限
                //文本长度
                float dis = titlePaint.measureText(titles[i]);
                canvas.drawText(titles[i], x - dis, y, titlePaint);

            } else if (radian * i >= Math.PI && radian * i < 3 * Math.PI / 2) { //第1象限
                float dis = titlePaint.measureText(titles[i]);
                canvas.drawText(titles[i], x - dis, y, titlePaint);
            }
        }
    }

    private Point getPoint(int position) {
        return getPoint(position, 0, 1);
    }

    private Point getPoint(int position, float fontHeight, float percent) {
        int x = 0;
        int y = 0;

        if (position == 0) {
            x = (int) (centerX + (radius + fontHeight) * Math.sin(radian) * percent);
            y = (int) (centerY - (radius + fontHeight) * Math.cos(radian) * percent);

        } else if (position == 1) {
            x = (int) (centerX + (radius + fontHeight) * Math.sin(radian / 2) * percent);
            y = (int) (centerY + (radius + fontHeight) * Math.cos(radian / 2) * percent);

        } else if (position == 2) {
            x = (int) (centerX - (radius + fontHeight) * Math.sin(radian / 2) * percent);
            y = (int) (centerY + (radius + fontHeight) * Math.cos(radian / 2) * percent);

        } else if (position == 3) {
            x = (int) (centerX - (radius + fontHeight) * Math.sin(radian) * percent);
            y = (int) (centerY - (radius + fontHeight) * Math.cos(radian) * percent);

        } else if (position == 4) {
            x = centerX;
            y = (int) (centerY - (radius + fontHeight) * percent);
        }

        return new Point(x, y);
    }
}
