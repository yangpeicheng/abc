package com.ypc.abc;

/**
 * Created by user on 2016/11/30.
 */

public class LpfFilter {
    private static final float alpha=0.7f;
    private boolean init;
    private float[] lastValue;
    public LpfFilter(){
        init=false;
    }
    public float[] LowPassfliter(float[] input){
        if(init==false){
            init=true;
            lastValue=new float[input.length];
            System.arraycopy(input,0,lastValue,0,input.length);
            return lastValue;
        }
        else{
            for(int i=0;i<input.length;i++)
                lastValue[i]=lastValue[i]*alpha+(1-alpha)*input[i];
            return lastValue;
        }
    }
    public float[] HighPassfliter(float[] input){
        float[] result=new float[input.length];
        if(init==false){
            init=true;
            lastValue=new float[input.length];
            System.arraycopy(input,0,lastValue,0,input.length);
        }
        else{
            for(int i=0;i<input.length;i++) {
                lastValue[i] = lastValue[i] * alpha + (1 - alpha) * input[i];
                result[i]=input[i]-lastValue[i];
            }
        }
        return result;
    }
}
