package com.example.clock;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.Calendar;

public class Clock extends View {

    Paint blackPaint;
    Paint textPaint;
    Paint linePaint;
    Paint handPaint;
    float[] middle=new float[2];
    int radius;
    float ratio;
    int SYSTEM_HEIGHT;
    int SYSTEM_WIDTH;

    public Clock(Context context) {
        this(context,null);
    }

    public Clock(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public Clock(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        int color = Color.parseColor("#000000");
        initblackPaint(color);
        inittextPaint(color);
        initlinePaint(color);
        inithandPaint(color);
    }

    public Clock(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        SYSTEM_WIDTH= (getResources().getDisplayMetrics().widthPixels)>>1;
        SYSTEM_HEIGHT= (getResources().getDisplayMetrics().heightPixels)>>1;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //onDarw中尽量不要创建对象，因为视图更新都会走onDarw(),而不断开辟空间
        //中心点
        middle[0]=getMeasuredWidth()>>1;
        middle[1]=getMeasuredHeight()>>1;
        radius= (int) Math.min(middle[0],middle[1]);

        if(middle[0]<middle[1]) ratio=middle[0]/SYSTEM_WIDTH;
        else ratio=middle[1]/SYSTEM_HEIGHT;

        canvas.translate(middle[0],middle[1]);

        initClockOutlook(canvas);
        canvas.save();

        int[] currentTime=getTime(); //[hour,minute,second]

        drawHands(canvas,currentTime);
        postInvalidateDelayed(1000);
    }

    private void drawHands(Canvas canvas, int[] currentTime) {
        canvas.save();
        canvas.rotate(currentTime[1]);
        handPaint.setStrokeWidth(12*ratio);
        canvas.drawLine(0,0,0,-(radius-100*ratio),handPaint);
        canvas.restore();

        canvas.save();
        canvas.rotate(currentTime[2]);
        handPaint.setStrokeWidth(7*ratio);
        canvas.drawLine(0,0,0,-(radius-50*ratio),handPaint);
        canvas.restore();

        canvas.save();
        canvas.rotate(currentTime[0]);
        handPaint.setStrokeWidth(30*ratio);
        canvas.drawLine(0,0,0,-(radius-200*ratio),handPaint);
        canvas.restore();
    }

    private int[] getTime() {
        Calendar mCalendar=Calendar.getInstance();
        int tempHour=mCalendar.get(Calendar.HOUR);
        int tempMinute=mCalendar.get(Calendar.MINUTE);
        int tempSecond=mCalendar.get(Calendar.SECOND);
        int hourRotate=Float.valueOf(360*((float)tempHour/12)).intValue();
        int minuteRotate=Float.valueOf(360*((float)tempMinute/60)).intValue();
        hourRotate+=Float.valueOf(30*((float)minuteRotate/360)).intValue();
        int secondRotate=Float.valueOf(360*((float)tempSecond/60)).intValue();
        return new int[] {hourRotate,minuteRotate,secondRotate};
    }

    private void initClockOutlook(Canvas canvas) {
        //表框
        blackPaint.setStyle(Paint.Style.STROKE);
        blackPaint.setStrokeWidth(14*ratio);
        canvas.drawCircle(0,0,radius-7*ratio,blackPaint);
        linePaint.setStrokeWidth(10*ratio);
        for(int i=0;i<12;i++){
            canvas.drawLine(0,radius,0,radius-50*ratio,linePaint);
            canvas.rotate(30,0,0);
        }

        for(int i=0;i<60;i++){
            canvas.drawLine(0,radius,0,radius-30*ratio,linePaint);
            canvas.rotate(6,0,0);
        }

        int[] dx={-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};
        int[] dy={1,1,1,1,1,1,1,1,1,1,1,1};
        textPaint.setTextSize(55*ratio);
        for(int i=1;i<13;i++){
            canvas.rotate(30,0,0);
            canvas.save();
            canvas.rotate(-30*i,0,0);
            double radians = Math.toRadians(i);
            float x= (float) Math.sin(30*radians)*(radius-90*ratio);
            float y=(float) Math.cos(30*radians)*(radius-90*ratio);
            float bias=10*ratio;
            canvas.drawText(String.valueOf(i),x+dx[i-1]*bias,-y+dy[i-1]*bias,textPaint);
            canvas.restore();//角度变为原来的
        }

        //表心
        blackPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(0,0,20*ratio,blackPaint);
    }

    private void inithandPaint(int color) {
        handPaint= new Paint(Paint.ANTI_ALIAS_FLAG);
        handPaint.setColor(color);
    }

    private void initlinePaint(int color) {
        linePaint= new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(color);
    }

    private void inittextPaint(int color) {
        textPaint= new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(color);
        textPaint.setFakeBoldText(true);
    }

    private void initblackPaint(int color) {
        blackPaint= new Paint(Paint.ANTI_ALIAS_FLAG);
        blackPaint.setStyle(Paint.Style.STROKE);
        blackPaint.setColor(color);
    }


}

