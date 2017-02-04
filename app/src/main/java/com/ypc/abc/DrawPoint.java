package com.ypc.abc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by 10959 on 2017/2/2.
 */

public class DrawPoint extends View {
    private Paint linePaint;
    private Bitmap bitmap;
    private boolean initFlag=false;
    private float lastx=0,lasty=0;
    private Paint plotPaint,bestPaint;
    private ArrayList<Point> map;
    public DrawPoint(Context context, AttributeSet attrs) {
        super(context, attrs);
        linePaint=new Paint();
        plotPaint=new Paint();
        plotPaint.setAntiAlias(true);
        plotPaint.setStyle(Paint.Style.FILL);
        plotPaint.setStyle(Paint.Style.STROKE);
        plotPaint.setStrokeJoin(Paint.Join.ROUND);
        plotPaint.setStrokeWidth(4f);
        plotPaint.setColor(Color.BLUE);
        bestPaint.setColor(Color.RED);
    }
    private void drawAxis(Canvas canvas){
        float gridX=getWidth()/2,gridY=getHeight()/2;
        canvas.drawLine(gridX  ,0,gridX,getHeight(),linePaint);
        canvas.drawLine(gridX,0,gridX-10,16,linePaint);
        canvas.drawLine(gridX,0,gridX+10,16,linePaint);
        canvas.drawText("N",gridX+30,30,linePaint);
        canvas.drawLine(0,gridY,getWidth(),gridY,linePaint);
        canvas.drawLine(getWidth(),gridY,getWidth()-16,gridY-10,linePaint);
        canvas.drawLine(getWidth(),gridY,getWidth()-16,gridY+10,linePaint);
        canvas.drawText("E",getWidth()-30,gridY-30,linePaint);
        for(int i=0;i<10;i++) {
            if (gridX + (i + 1) * 50 < getWidth() - 50) {
                canvas.drawLine(gridX + (i + 1) * 50, gridY, gridX + (i + 1) * 50, gridY - 10, linePaint);
                canvas.drawText(String.valueOf(2*(i+1)),gridX+(i+1)*50,gridY-25,linePaint);
                canvas.drawLine(gridX - (i + 1) * 50, gridY, gridX - (i + 1) * 50, gridY - 10, linePaint);
                canvas.drawText(String.valueOf((-2*(i+1))),gridX-(i+1)*50,gridY-25,linePaint);
            }
            else
                break;
        }
        for(int i=0;i<10;i++) {
            if (gridY - (i + 1) * 50 >= 50) {
                canvas.drawLine(gridX ,gridY - (i + 1) * 50, gridX +10, gridY - (i + 1) * 50, linePaint);
                canvas.drawText(String.valueOf(2*(i+1)),gridX+25,gridY - (i + 1) * 50+5,linePaint);
                canvas.drawLine(gridX , gridY +(i + 1) * 50, gridX +10, gridY + (i + 1) * 50, linePaint);
                canvas.drawText(String.valueOf((-2*(i+1))),gridX+25,gridY + (i + 1) * 50+5,linePaint);
            }
            else
                break;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float w=getWidth()/2.0f,h=getHeight()/2.0f;
        if(!initFlag) {
            initFlag=true;
            bitmap=Bitmap.createBitmap(getWidth(),getHeight(),Bitmap.Config.ARGB_8888);
            Canvas tempcanvas=new Canvas(bitmap);
            drawAxis(tempcanvas);
            canvas.drawBitmap(bitmap,0,0,null);
        }
        else {
            Canvas tempcanvas=new Canvas(bitmap);
            tempcanvas.drawCircle(w+lastx*25,h+lasty*25,5,plotPaint);
            canvas.drawBitmap(bitmap,0,0,null);
        }
        if(map!=null){
            for(int i=0;i<map.size();i++)
                canvas.drawCircle(w+map.get(i).x*25,h-map.get(i).y*25,5,bestPaint);
        }
    }
    public void updatePoint(float x,float y){
        if(Math.sqrt((lastx-x)*(lastx-x)+(lasty-y)*(lasty-y))>0.5){
            lastx=x;
            lasty=y;
            invalidate();
        }
    }
    public void drawBest(ArrayList<Point> p){
        map=p;
        invalidate();
    }
}
