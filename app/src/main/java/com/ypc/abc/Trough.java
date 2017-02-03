package com.ypc.abc;

/**
 * Created by user on 2016/11/20.
 */

public class Trough {
    private float[] gyro;
    private float[] gyrotemp;
    private int length=7,lengthtemp=20;
    private int index,indextemp;
    int endflag;
    int startflag;
    int midflag;
    public float maxvalue;
    public float lasttrough;
    public Trough(){
        index=0;
        maxvalue=0;
        lasttrough=0;
        gyro=new float[length];
        gyrotemp=new float[lengthtemp];
    }
    public void insert(float data){
        if(data>maxvalue)
            maxvalue=data;
        gyro[index]=data;
        index=(index+1)%length;
        gyrotemp[indextemp]=data;
        indextemp=(indextemp+1)%lengthtemp;
        endflag=index==0?length-1:index-1;
        startflag=(endflag+1)%length;
        midflag=((endflag-length/2)<0)?endflag-length/2+length:endflag-length/2;
    }
    public float getVariance(){
        float max=-10.0f,min=10.0f;
        for(int i=0;i<gyrotemp.length;i++){
            if(gyrotemp[i]>max)
                max=gyrotemp[i];
            if(gyrotemp[i]<min)
                min=gyrotemp[i];
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
    private boolean isMin(){
        int start=startflag,mid=midflag;
        for (int i=0;i<length/2-1;i++){
            if(gyro[start]<gyro[(start+2)%length]){
                return false;
            }
            start=(start+1)%length;
        }
        for(int i=0;i<length/2-1;i++){
            if(gyro[mid]>gyro[(mid+2)%length]){
                return false;
            }
            mid=(mid+1)%length;
        }
       // if((maxvalue-gyro[mid])<0.3f*(maxvalue-lasttrough))
        //    return false;
       // maxvalue=0;
        //lasttrough=gyro[mid];
        return true;
    }
    private boolean isMax(){
        int start=startflag,mid=midflag;
        for (int i=0;i<length/2-1;i++){
            if(gyro[start]>gyro[(start+2)%length]){
                return false;
            }
            start=(start+1)%length;
        }
        for(int i=0;i<length/2-1;i++){
            if(gyro[mid]<gyro[(mid+2)%length]){
                return false;
            }
            mid=(mid+1)%length;
        }
        return true;
    }
}
