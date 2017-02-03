package com.ypc.abc;

import java.util.LinkedList;

/**
 * Created by user on 2016/12/19.
 */

public class Turn {
    private static final int length=5;
    private LinkedList<Number> datalist;
    private boolean init=false;
    private float max;
    private float min;
    public Turn(){
        datalist=new LinkedList<>();
    }
    public boolean addSample(float data){
        if(!init){
            init=true;
            max=data;
            min=data;
            return false;
        }
        if(data>max)
            max=data;
        if(data<min)
            min=data;
        if(max-min>0.75f){
            init=false;
            datalist.clear();
            return true;
        }
        datalist.add(data);
        if(datalist.size()>length)
            datalist.removeFirst();
        return false;
    }
}
