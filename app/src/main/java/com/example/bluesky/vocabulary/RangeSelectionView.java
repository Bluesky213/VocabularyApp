package com.example.bluesky.vocabulary;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.text.DecimalFormat;

public class RangeSelectionView extends View {

    //float price=1.2;
    DecimalFormat decimalFormat=new DecimalFormat("0");//构造方法的字符格式这里如果小数不足2位,会以0补足.
    //String p=decimalFormat.format(1.2);//format 返回的是字符串



    private Paint paintBackground;//背景线的画笔
    private Paint paintCircle;//起始点圆环的画笔
    private Paint paintWhileCircle;//起始点内圈白色区域的画笔
    private Paint paintText;//起始点数值的画笔
    private Paint paintConnectLine;//起始点连接线的画笔

    private int height = 40;//控件的高度
    private int width = 0;//控件的宽度

    private float centerVertical = 0;//y轴的中间位置

    private float backlineWidth = 2;//底线的宽度

    private float marginhorizontal = 50;//横向边距

    private float marginTop = 70;//文字距顶部的距离

    private float pointStart = 0;//起点的X轴位置

    private float pointEnd = 0;//始点的Y轴位置

    private float circleRadius = 15;//起始点圆环的半径

    //private float numStart = 0;//数值的开始值                 ----------调用的值

    private int numStart = 0;

    private int numEnd = 0;

    //private float numEnd = 0;//数值的结束值                    -----------调用的值

    private int textSize = 30;//文字的大小

    private String strUnit = "";//刻度的单位

    private boolean isRunning = false;//是否可以滑动

    private boolean isStart = true;//起点还是终点 true：起点；false：终点。


    public RangeSelectionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RangeSelectionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //获取控件的宽高、中线位置、起始点、起始数值
        height = MeasureSpec.getSize(heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        centerVertical = 2*height / 3;

        pointStart = marginhorizontal;
        pointEnd = width - marginhorizontal;

        numStart = getProgressNum(pointStart);

        numEnd = getProgressNum(pointEnd);

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //如果点击的点在第一个圆内就是起点,并且可以滑动
                if (event.getX() >= (pointStart - circleRadius) && event.getX() <= (pointStart + circleRadius)) {
                    isRunning = true;
                    isStart = true;

                    pointStart = event.getX();
                    //如果点击的点在第二个圆内就是终点,并且可以滑动
                } else if (event.getX() <= (pointEnd + circleRadius) && event.getX() >= (pointEnd - circleRadius)) {
                    isRunning = true;
                    isStart = false;

                    pointEnd = event.getX();
                } else {
                    //如果触控点不在圆环内，则不能滑动
                    isRunning = false;
                }

                break;
            case MotionEvent.ACTION_MOVE:
                if (isRunning) {
                    if (isStart) {
                        //起点滑动时，重置起点的位置和进度值
                        pointStart = event.getX();
                        numStart = getProgressNum(pointStart);
                    } else {
                        //始点滑动时，重置始点的位置和进度值
                        pointEnd = event.getX();
                        numEnd = getProgressNum(pointEnd);
                    }

                    flushState();//刷新状态
                }

                break;
            case MotionEvent.ACTION_UP:

                flushState();
                break;
        }

        return true;
    }

    /**
     * 刷新状态和屏蔽非法值
     */
    private void flushState() {

        //起点非法值
        if (pointStart < marginhorizontal) {
            pointStart = marginhorizontal;
        }
        //终点非法值
        if (pointEnd > width - marginhorizontal) {
            pointEnd = width - marginhorizontal;
        }

        //防止起点位置大于终点位置（规定：如果起点位置大于终点位置，则将起点位置放在终点位置前面,即：终点可以推着起点走，而起点不能推着终点走）
        if (pointStart + circleRadius > pointEnd - circleRadius) {

            pointStart = pointEnd - 2 * circleRadius;

        }

        //防止终点把起点推到线性范围之外
        if (pointEnd < marginhorizontal + 2 * circleRadius) {
            pointEnd = marginhorizontal + 2 * circleRadius;
            pointStart = marginhorizontal;
        }

        invalidate();//这个方法会导致onDraw方法重新绘制

    }

    //进度范围
    float beginNum = 0;
    float endNum = 100;

    //计算进度数值
//    private float getProgressNum(float progress) {
//
//        return (int) progress / (width - 2 * marginhorizontal) * (endNum - beginNum);
//
//    }

    private int getProgressNum(float progress) {

        return (int) ((int) progress / (width - 2 * marginhorizontal) * (endNum - beginNum));

    }

    //初始化画笔
    private void init() {

        paintBackground = new Paint();
        paintBackground.setColor(getResources().getColor(R.color.blue));//pink
        paintBackground.setStrokeWidth(backlineWidth);
        paintBackground.setAntiAlias(true);

        paintCircle = new Paint();
        paintCircle.setColor(getResources().getColor(R.color.red));//Orange
        paintCircle.setStrokeWidth(backlineWidth);
        paintCircle.setStyle(Paint.Style.STROKE);
        paintCircle.setAntiAlias(true);

        paintWhileCircle = new Paint();
        paintWhileCircle.setColor(getResources().getColor(R.color.white));//white
        paintCircle.setStyle(Paint.Style.FILL);
        paintWhileCircle.setAntiAlias(true);

        paintText = new Paint();
        paintText.setColor(getResources().getColor(R.color.colorPrimary));//red
        paintText.setTextSize(textSize);
        paintText.setAntiAlias(true);

        paintConnectLine = new Paint();
        paintConnectLine.setColor(getResources().getColor(R.color.red));//orange
        paintConnectLine.setStrokeWidth(backlineWidth + 5);
        paintConnectLine.setAntiAlias(true);

    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);

        //背景线
        canvas.drawLine(marginhorizontal, centerVertical, width - marginhorizontal, centerVertical, paintBackground);
        //起点位置的外圈圆
        canvas.drawCircle(pointStart, centerVertical, circleRadius, paintCircle);
        //起点位置的内圈圆
        canvas.drawCircle(pointStart, centerVertical, circleRadius - backlineWidth, paintWhileCircle);
        //终点位置的外圈圆
        canvas.drawCircle(pointEnd, centerVertical, circleRadius, paintCircle);
        //终点位置的内圈圆
        canvas.drawCircle(pointEnd, centerVertical, circleRadius - backlineWidth, paintWhileCircle);
        //起始点连接线
        canvas.drawLine(pointStart + circleRadius, centerVertical, pointEnd - circleRadius, centerVertical, paintConnectLine);
        //起点数值
        //canvas.drawText(numStart + strUnit, pointStart, marginTop, paintText);
        canvas.drawText(decimalFormat.format(numStart) + strUnit, pointStart, marginTop, paintText);
        //终点数值
        canvas.drawText(decimalFormat.format(numEnd) + strUnit, pointEnd, marginTop, paintText);

    }
    public int getNumStart(){
        return numStart;
    }

    public int getNumEnd() {
        return numEnd;
    }
}
