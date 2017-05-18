package com.ypc.abc;

import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by yangpeicheng on 2017/3/28.
 * Zero Velocity Compensation
 */

public class ZeroVelocity {
    private static final int WINDOWSIZE=5;
    private static final float T=0.02f;
    private LinkedList<Float> window=new LinkedList<>();
    private ArrayList<float []> data=new ArrayList<>();
    private ArrayList<float []> velocity=new ArrayList<>();
    private ArrayList<float []> distance=new ArrayList<>();
    private float[] zero;
    private boolean initFlag=false;
    public ZeroVelocity(){

    }
    private float getMagnitude(float []input){
        float result=0;
        for(float i:input){
            result+=i*i;
        }
        return (float)Math.sqrt(result);
    }
    private float getVariance(){
        float result=0,sum=0,average;
        for(int i=0;i<window.size();i++)
            sum+=window.get(i);
        average=sum/window.size();
        for(int i=0;i<window.size();i++)
            result+=(window.get(i)-average)*(window.get(i)-average);
        return (float)Math.sqrt(result);
    }
    public void handleData(float []input){
        if(input.length<2)
            return;
        if(!initFlag){
            initFlag=true;
            zero=new float[input.length];
            data.add(zero);
        }
        float accelerationMagnitude=getMagnitude(input);
        window.addLast(accelerationMagnitude);
        if(window.size()<WINDOWSIZE)
            return;
        else
            window.removeFirst();
        float variance=getVariance();
        if(variance<0.09&&accelerationMagnitude<0.5)
            return;
        else {
            //Log.d("input",String.valueOf(input[0]));
            float[] temp=new float[input.length];
            System.arraycopy(input,0,temp,0,input.length);
            data.add(temp);
            //Log.d("data",String.valueOf(data.get(data.size()-1)[0]));
        }
    }
    public void compensation(){
        if(!initFlag)
            return;
        data.add(zero);
        float[] delta=new float[zero.length];
        for(int i=0;i<data.size();i++){
            for(int j=0;j<delta.length;j++){
                delta[j]+=data.get(i)[j];
            }
        }
        int num=data.size()-2;
        for(int i=0;i<delta.length;i++){
            delta[i]/=num;
        }
        for(int i=1;i<data.size()-1;i++){
            float[] temp=new float[delta.length];
            for(int j=0;j<temp.length;j++){
                temp[j]=data.get(i)[j]-delta[j];
            }
            data.set(i,temp);
        }
    }
    public ArrayList<float[]> calculateVelocity(){
        velocity.add(zero);
        for(int i=1;i<data.size();i++){
            float []tempvelocity=new float[zero.length];
            float[] lastvelocity=velocity.get(velocity.size()-1);
            for(int j=0;j<tempvelocity.length;j++){
                tempvelocity[j]=lastvelocity[j]+(data.get(i-1)[j]+data.get(i)[j])*T/2;
            }
            velocity.add(tempvelocity);
        }
        return velocity;
    }
    public ArrayList<float[]> calculateDistance(){
        distance.add(zero);
        for(int i=1;i<velocity.size();i++){
            float []tempdistance=new float[zero.length];
            float[] lastdistance=distance.get(distance.size()-1);
            for(int j=0;j<tempdistance.length;j++){
                tempdistance[j]=lastdistance[j]+(velocity.get(i-1)[j]+velocity.get(i)[j])*T/2;
            }
            distance.add(tempdistance);
        }
        return distance;
    }
    public ArrayList<float[]> getAcceleration(){
        return data;
    }
}
