# SesameCreditScore
芝麻信用分雷达图

# **1.介绍** #

首先看下支付宝上芝麻信用分的效果图：

![芝麻分](http://img.blog.csdn.net/20160929152402059)

# **2.思路** #

 1. 确定雷达图中心点坐标
 2. 绘制多边形及连接线
 3. 根据维度值绘制覆盖区域
 4. 绘制分数
 5. 绘制每个维度的标题文字和图标

# **3.实现** #

## **获取布局的中心坐标** ##

在onSizeChanged(int w, int h, int oldw, int oldh)方法里面，根据View的长宽，计算出雷达图的半径（这里取布局宽高最小值的四分之一，可以自定义），获取整个布局的中心坐标。

```
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
    private float[] data = {170, 180, 160, 170, 180};
    //数据最大值
    private float maxValue = 190;
    //雷达图与标题的间距
    private int radarMargin = DensityUtils.dp2px(getContext(), 15);
    //雷达区画笔
    private Paint mainPaint;
    //数据区画笔
    private Paint valuePaint;
    //分数画笔
    private Paint scorePaint;
    //标题画笔
    private Paint titlePaint;
    //图标画笔
    private Paint iconPaint;
    //分数大小
    private int scoreSize = DensityUtils.dp2px(getContext(), 28);
    //标题文字大小
    private int titleSize = DensityUtils.dp2px(getContext(), 13);
    
	...

	@Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //雷达图半径
        radius = Math.min(h, w) / 2 * 0.5f;
        //中心坐标
        centerX = w / 2;
        centerY = h / 2;
        postInvalidate();
        super.onSizeChanged(w, h, oldw, oldh);
    }

	...
}
```

## **绘制多边形和连接线** ##

主要看下getPoint方法，此方法封装了获取雷达图上各个点坐标的计算逻辑。

```
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

```

getPoint方法，参数radarMargin与percent在此步骤赋予默认值。

```
/**
 * 获取雷达图上各个点的坐标
 *
 * @param position 坐标位置（右上角为0，顺时针递增）
 * @return 坐标
 */
private Point getPoint(int position) {
	return getPoint(position, 0, 1);
}

/**
 * 获取雷达图上各个点的坐标（包括维度标题与图标的坐标）
 *
 * @param position    坐标位置
 * @param radarMargin 雷达图与维度标题的间距
 * @param percent     覆盖区的的百分比
 * @return 坐标
 */
private Point getPoint(int position, int radarMargin, float percent) {
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

```

![多边形和连接线](http://img.blog.csdn.net/20160929172728600)

## **绘制覆盖区域** ##

```
/**
 * 绘制覆盖区域
 *
 * @param canvas 画布
 */
private void drawRegion(Canvas canvas) {
	Path path = new Path();

	for (int i = 0; i < dataCount; i++) {
		//计算百分比
		float percent = data[i] / maxValue;
		int x = getPoint(i, 0, percent).x;
		int y = getPoint(i, 0, percent).y;
		if (i == 0) {
			path.moveTo(x, y);
		} else {
			path.lineTo(x, y);
		}
	}

	//绘制填充区域的边界
	path.close();
	valuePaint.setStyle(Paint.Style.STROKE);
	canvas.drawPath(path, valuePaint);

	//绘制填充区域
	valuePaint.setStyle(Paint.Style.FILL_AND_STROKE);
	canvas.drawPath(path, valuePaint);
}
```

![覆盖区域](http://img.blog.csdn.net/20160929173030526)

## **绘制分数** ##

```
/**
 * 绘制分数
 *
 * @param canvas 画布
 */
private void drawScore(Canvas canvas) {
	int score = 0;
	//计算总分
	for (int i = 0; i < dataCount; i++) {
		score += data[i];
	}
	canvas.drawText(score + "", centerX, centerY + scoreSize / 2, scorePaint);
}
```

![分数](http://img.blog.csdn.net/20160929173101167)

## **绘制标题** ##

```
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

		//底下两个角的坐标需要向下移动半个图片的位置（1、2）
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
```

![标题](http://img.blog.csdn.net/20160929173117620)

## **绘制图标** ##

```
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

		//上面获取到的x、y坐标是标题左下角的坐标
		//需要将图标移动到标题上方居中位置
		if (i == 0) {
			x += (titleWidth - iconWidth) / 2;
			y -= (iconHeight + getTextHeight(titlePaint));
		} else if (i == 1) {
			x += (titleWidth - iconWidth) / 2;
			y -= (iconHeight / 2 + getTextHeight(titlePaint));
		} else if (i == 2) {
			x -= (iconWidth + (titleWidth - iconWidth) / 2);
			y -= (iconHeight / 2 + getTextHeight(titlePaint));
		} else if (i == 3) {
			x -= (iconWidth + (titleWidth - iconWidth) / 2);
			y -= (iconHeight + getTextHeight(titlePaint));
		} else if (i == 4) {
			x -= iconWidth / 2;
			y -= (iconHeight + getTextHeight(titlePaint));
		}

		canvas.drawBitmap(bitmap, x, y, titlePaint);
	}
}
```

```
/**
 * 获取文本的高度
 *
 * @param paint 文本绘制的画笔
 * @return 文本高度
 */
private int getTextHeight(Paint paint) {
	Paint.FontMetrics fontMetrics = paint.getFontMetrics();
	return (int) (fontMetrics.descent - fontMetrics.ascent);
}
```

![图标](http://img.blog.csdn.net/20160929173138886)

OK，到这里主要的绘制工作就完成了，图标是在[>戳这里<](http://www.iconfont.cn/)下载的，有些图标实在找不到，就用相似的代替了。

# **4.写在最后** #

还没有做适配，以后会慢慢加上的，源码已托管到GitHub上，欢迎Fork，觉得还不错就Start一下吧！

GitHub地址：https://github.com/alidili/SesameCreditScore

感谢：
http://blog.csdn.net/u013831257/article/details/50784565
http://blog.csdn.net/crazy__chen/article/details/50163693





