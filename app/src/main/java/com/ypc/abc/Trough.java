package com.ypc.abc;

import android.util.Log;

import java.util.LinkedList;

/**
 * Created by user on 2016/11/20.
 */

public class Trough {
    private int length=7,lengthtemp=20;
    private int midnum=3;
    private LinkedList<Float> dataList=new LinkedList<>();
    private LinkedList<Float> longdatalist=new LinkedList<>();
    public Trough(){
    }
    public void insert(float data){
        dataList.addLast(data);
        longdatalist.addLast(data);
        if(dataList.size()>length)
            dataList.removeFirst();
        if(longdatalist.size()>lengthtemp)
            longdatalist.removeFirst();

    }
    private float getVariance(){
        float max=-10f,min=10f;
        for(int i=0;i<longdatalist.size();i++){
            if(longdatalist.get(i)>max)
                max=longdatalist.get(i);
            if(longdatalist.get(i)<min)
                min=longdatalist.get(i);
        }
        return max-min;
    }
    public int judge(){
        if(getVariance()<0.05f)
            return 1;
        else if(isMin())
            return 2;
        else
            return 0;
    }
    public boolean armJudge(){
        if(dataList.size()<length)
            return false;
        else if(ArmisMin())
            return true;
        else
            return false;
    }
    private boolean isMin(){
        for(int i=0;i<midnum-2;i++){
            if(dataList.get(i)<dataList.get(i+2))
                return false;
            else
                continue;
        }
        for(int i=midnum;i<dataList.size()-2;i++){
            if(dataList.get(i)>dataList.get(i+2))
                return false;
            else
                continue;
        }
        return true;
    }
    private boolean ArmisMin(){
        for(int i=0;i<midnum;i++){
            if(dataList.get(i)<dataList.get(i+1))
                return false;
            else if(dataList.get(i)==0.0f&&dataList.get(i+1)==0.0f)
                return false;
            else
                continue;
        }
        for(int i=midnum;i<dataList.size()-1;i++){
            if(dataList.get(i)>dataList.get(i+1))
                return false;
            else if(dataList.get(i)==0.0f&&dataList.get(i+1)==0.0f)
                return false;
            else
                continue;
        }
        return true;
    }
}
