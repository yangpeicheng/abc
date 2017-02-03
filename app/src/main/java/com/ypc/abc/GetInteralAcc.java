package com.ypc.abc;

import android.util.Log;

/**
 * Created by user on 2016/11/7.
 */

public class GetInteralAcc {
    protected static final float EPSILON = 0.000000001f;
    protected static final float NS2S = 1.0f / 1000000000.0f;
    private long timestampLast;
    private float dT;
    private float[] accLast=new float[3];
    private float[] velocityLast=new float[3];
    private float[] distanceLast=new float[3];
    public GetInteralAcc(){
        timestampLast=0;
        for(int i=0;i<velocityLast.length;i++){
            velocityLast[i]=0;
            distanceLast[i]=0;
        }
    }
    public float[] calculateDistance(float[] linacc,long timestamp){
        if(timestampLast==0){
            timestampLast=timestamp;
            System.arraycopy(linacc,0,accLast,0,linacc.length);

            return distanceLast;
        }
        dT=(timestamp-timestampLast)*NS2S;
        timestampLast=timestamp;
        for(int i=0;i<linacc.length;i++){
            float tempV=velocityLast[i]+dT*(linacc[i]+accLast[i])/2.0f;
            this.distanceLast[i]+=dT*(velocityLast[i]+tempV);
            velocityLast[i]=tempV;
        }
        System.arraycopy(linacc,0,accLast,0,linacc.length);
        return distanceLast;
    }
    public void Reset(){
        timestampLast=0;
        for(int i=0;i<velocityLast.length;i++){
            velocityLast[i]=0;
        }
    }
    public float[] getDistance(){
        return distanceLast;
    }
}
