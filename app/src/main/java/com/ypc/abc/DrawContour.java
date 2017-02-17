package com.ypc.abc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by 10959 on 2017/2/5.
 */

public class DrawContour extends View {
    private Paint linePaint,plotPaint,bestPaint;
    private boolean initFlag;
    private Bitmap bitmap;
    private float[] lastdata=new float[3];
    private float[] currentdata=new float[3];
    private Point3D[] map;
    public DrawContour(Context context, AttributeSet attrs) {
        super(context, attrs);
        linePaint=new Paint();
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth((float)0.7);
        linePaint.setColor(Color.BLACK);
        linePaint.setAntiAlias(true);
        plotPaint=new Paint();
        plotPaint.setAntiAlias(true);
        plotPaint.setStyle(Paint.Style.FILL);
        plotPaint.setStyle(Paint.Style.STROKE);
        plotPaint.setStrokeJoin(Paint.Join.ROUND);
        plotPaint.setStrokeWidth(4f);
        plotPaint.setColor(Color.BLUE);
        bestPaint=new Paint();
        bestPaint.setColor(Color.RED);
        initFlag=false;
    }
    private void DrawAxis(Canvas canvas){
        float gridX=getWidth()/2,gridY=getHeight()/2;
        canvas.drawLine(gridX  ,0,gridX,gridY,linePaint);
        canvas.drawLine(gridX,0,gridX-10,16,linePaint);
        canvas.drawLine(gridX,0,gridX+10,16,linePaint);
        canvas.drawText("Y",gridX+30,30,linePaint);
        canvas.drawLine(gridX,gridY,getWidth(),gridY,linePaint);
        canvas.drawLine(getWidth(),gridY,getWidth()-16,gridY-10,linePaint);
        canvas.drawLine(getWidth(),gridY,getWidth()-16,gridY+10,linePaint);
        canvas.drawText("X",getWidth()-30,gridY-30,linePaint);
        canvas.drawLine(gridX,gridY,gridX-310,gridY+310,linePaint);
        for(int i=0;i<10;i++) {
            if (gridX + (i + 1) * 50 < getWidth() - 50) {
                canvas.drawLine(gridX + (i + 1) * 50, gridY, gridX + (i + 1) * 50, gridY - 10, linePaint);
                canvas.drawText(String.format("%.1f",0.1f*(i+1)),gridX+(i+1)*50,gridY-25,linePaint);
            }
            else
                break;
        }
        for(int i=0;i<10;i++) {
            if (gridY - (i + 1) * 50 >= 50) {
                canvas.drawLine(gridX ,gridY - (i + 1) * 50, gridX +10, gridY - (i + 1) * 50, linePaint);
                canvas.drawText(String.format("%.1f",0.1f*(i+1)),gridX+25,gridY - (i + 1) * 50+5,linePaint);
            }
            else
                break;
        }
        for(int i=0;i<8;i++) {
            if (gridX - (i + 1) * 35.36 >= 50) {
                canvas.drawLine(gridX - (i + 1) * 35f,gridY + (i + 1) * 35f, gridX - (i + 1) * 35f+7,gridY +(i + 1) * 35f+7, linePaint);
                canvas.drawText(String.format("%.1f",0.1f*(i+1)),gridX - (i + 1) * 35f-17.68f,gridY +(i + 1) * 35f-17.68f,linePaint);
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
            bitmap= Bitmap.createBitmap(getWidth(),getHeight(),Bitmap.Config.ARGB_8888);
            Canvas tempcanvas=new Canvas(bitmap);
            DrawAxis(tempcanvas);
            canvas.drawBitmap(bitmap,0,0,null);
        }else{
            Canvas tempCanvas=new Canvas(bitmap);
            //tempCanvas.drawCircle((w-350*lastdata[0]+500*lastdata[1]),(h+350*lastdata[0]-500*lastdata[2]),5,plotPaint);
            tempCanvas.drawLine((w-350*lastdata[0]+500*lastdata[1]),(h+350*lastdata[0]-500*lastdata[2]),(w-350*currentdata[0]+500*currentdata[1]),(h+350*currentdata[0]-500*currentdata[2]),plotPaint);
            System.arraycopy(currentdata,0,lastdata,0,currentdata.length);
            canvas.drawBitmap(bitmap,0,0,null);
            //tempCanvas.drawCircle();
        }
        if(map!=null){
            Point3D last=new Point3D(0,0,0);
            for(int i=0;i<map.length;i++){
                Point3D current=map[i];
                canvas.drawLine((w-350*last.x+500*last.y),(h+350*last.x-500*last.z),(w-350*current.x+500*current.y),(h+350*current.x-500*current.z),bestPaint);
                canvas.drawCircle((w-350*current.x+500*current.y),(h+350*current.x-500*current.z),5,bestPaint);
                last=new Point3D(current.x,current.y,current.z);
            }
        }
    }

    public void updatePoint(float[] data){
        if(calculate(data,lastdata)>0.005){
           // System.arraycopy(data,0,lastdata,0,data.length);
            System.arraycopy(data,0,currentdata,0,data.length);
            invalidate();
        }
    }
    public float calculate(float[] a, float[] b){
        float result=0;
        for(int i=0;i<a.length;i++){
            result+=(a[i]-b[i])*(a[i]-b[i]);
        }
        return (float)Math.sqrt(result);
    }
    public void drawbest(Point3D[] p){
        map=p;
        invalidate();
    }
}
