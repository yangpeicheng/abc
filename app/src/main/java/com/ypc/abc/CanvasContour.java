package com.ypc.abc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewDebug;

import java.util.ArrayList;

/**
 * Created by user on 2016/11/8.
 */

public class CanvasContour extends View {
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint linePaint,xyChartPaint,chartLinePaint;
    Context context;
    private Paint[] mPaint=new Paint[3];
    private float mX, mY;
    private ArrayList<Float> contourX;
    private ArrayList<Float> contourY;
    private ArrayList<Integer> typeList;
    private ArrayList<Point> map;
    boolean init;
    public CanvasContour(Context c, AttributeSet attrs) {
        super(c, attrs);
        context=c;
        for(int i=0;i<mPaint.length;i++) {
            mPaint[i] = new Paint();
            mPaint[i].setAntiAlias(true);
            mPaint[i].setStyle(Paint.Style.FILL);
            mPaint[i].setStyle(Paint.Style.STROKE);
            mPaint[i].setStrokeJoin(Paint.Join.ROUND);
            mPaint[i].setStrokeWidth(4f);
        }
        mPaint[0].setColor(Color.RED);
        mPaint[1].setColor(Color.BLUE);
        mPaint[2].setColor(Color.GREEN);
        contourX=new ArrayList<>();
        contourY=new ArrayList<>();
        typeList=new ArrayList<>();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initView();
        float gridX=getWidth()/2,gridY=getHeight()/2;
        canvas.drawLine(gridX  ,0,gridX,getHeight(),linePaint);
        canvas.drawLine(gridX,0,gridX-10,16,linePaint);
        canvas.drawLine(gridX,0,gridX+10,16,linePaint);
        canvas.drawText("N",gridX+30,30,xyChartPaint);
        canvas.drawLine(0,gridY,getWidth(),gridY,linePaint);
        canvas.drawLine(getWidth(),gridY,getWidth()-16,gridY-10,linePaint);
        canvas.drawLine(getWidth(),gridY,getWidth()-16,gridY+10,linePaint);
        canvas.drawText("E",getWidth()-30,gridY-30,xyChartPaint);
        for(int i=0;i<10;i++) {
            if (gridX + (i + 1) * 50 < getWidth() - 50) {
                canvas.drawLine(gridX + (i + 1) * 50, gridY, gridX + (i + 1) * 50, gridY - 10, linePaint);
                canvas.drawText(String.valueOf(2*(i+1)),gridX+(i+1)*50,gridY-25,xyChartPaint);
                canvas.drawLine(gridX - (i + 1) * 50, gridY, gridX - (i + 1) * 50, gridY - 10, linePaint);
                canvas.drawText(String.valueOf((-2*(i+1))),gridX-(i+1)*50,gridY-25,xyChartPaint);
            }
            else
                break;
        }
        for(int i=0;i<10;i++) {
            if (gridY - (i + 1) * 50 >= 50) {
                canvas.drawLine(gridX ,gridY - (i + 1) * 50, gridX +10, gridY - (i + 1) * 50, linePaint);
                canvas.drawText(String.valueOf(2*(i+1)),gridX+25,gridY - (i + 1) * 50+5,xyChartPaint);
                canvas.drawLine(gridX , gridY +(i + 1) * 50, gridX +10, gridY + (i + 1) * 50, linePaint);
                canvas.drawText(String.valueOf((-2*(i+1))),gridX+25,gridY + (i + 1) * 50+5,xyChartPaint);
            }
            else
                break;
        }
        float w=getWidth()/2.0f,h=getHeight()/2.0f;
        for(int i=0;i<contourX.size();i++){
            canvas.drawCircle(w+contourX.get(i)*25,h+contourY.get(i)*25,5,mPaint[typeList.get(i)]);
        }
        if(map!=null){
            for(int i=0;i<map.size();i++)
                canvas.drawCircle(w+map.get(i).x*25,h-map.get(i).y*25,5,mPaint[1]);
        }
        init=false;


    }
    protected void initView(){
        linePaint=new Paint();
        xyChartPaint=new Paint();
        chartLinePaint=new Paint();
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth((float)0.7);
        linePaint.setColor(Color.BLACK);
        linePaint.setAntiAlias(true);// 锯齿不显示
        //绘制XY轴上的字：Y开关状态、X时间
        xyChartPaint.setStyle(Paint.Style.FILL);
        xyChartPaint.setStrokeWidth(1);
        xyChartPaint.setColor(Color.BLACK);
        xyChartPaint.setAntiAlias(true);
        xyChartPaint.setTextAlign(Paint.Align.CENTER);
        xyChartPaint.setTextSize(20);
        //绘制的折线
        chartLinePaint.setStyle(Paint.Style.FILL);
        chartLinePaint.setStrokeWidth(3);
        chartLinePaint.setColor(Color.RED);//(1)黄色
        chartLinePaint.setAntiAlias(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // your Canvas will draw onto the defined Bitmap
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }
    public void move(float x,float y,int type){
        if(!init){
            mX=x;
            mY=y;
            init=true;
        }else {
            if(Math.sqrt((x-mX)*(x-mX)+(y-mY)*(y-mY))>0.05){
                mX=x;
                mY=y;
                contourX.add(x);
                contourY.add(y);
                typeList.add(type);
                invalidate();
            }
        }
    }
    public void drawMap(ArrayList<Point> p){
        map=p;
        invalidate();
    }

}
