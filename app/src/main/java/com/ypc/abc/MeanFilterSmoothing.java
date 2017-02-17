package com.ypc.abc;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by user on 2016/11/5.
 */

public class MeanFilterSmoothing {
    private int filterWindow=20;
    private boolean dataInit;
    private float [] means;
    private ArrayList<LinkedList<Float>> dataLists;

    public MeanFilterSmoothing(){
        dataLists=new ArrayList<LinkedList<Float>>();
        dataInit=false;
    }

    public float[] addSamples(float [] data){
        for(int i=0;i<data.length;i++){
            if(!dataInit){
                dataLists.add(new LinkedList<Float>());
            }
            dataLists.get(i).addLast(data[i]);
            if(dataLists.get(i).size()>filterWindow){
                dataLists.get(i).removeFirst();
            }
        }
        if(!dataInit)
            means=new float[dataLists.size()];
        dataInit= true;
        for(int i=0;i<dataLists.size();i++){
            means[i]=getMean(dataLists.get(i));
        }
        return means;
    }
    public float[] getVariance(){
        float[] result=new float[means.length];
        if(dataLists.get(0).size()<5)
            return result;
        for(int i=0;i<dataLists.size();i++){
            int lastindex=dataLists.get(i).size()-1;
            for(int j=0;j<5;j++){
                result[i]+=(dataLists.get(i).get(lastindex-j)-means[i])*(dataLists.get(i).get(lastindex-j)-means[i]);
            }
            result[i]=(float)Math.sqrt(result[i]/dataLists.get(i).size());
        }
        return result;
    }
    private float getMean(List<Float> data){
        float m=0;
        float count=0;
        for(int i=0;i<data.size();i++){
            m+=data.get(i).floatValue();
            count++;
        }
        if(count!=0){
            m=m/count;
        }
        return m;
    }
}
