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
    private ArrayList<LinkedList<Number>> dataLists;

    public MeanFilterSmoothing(){
        dataLists=new ArrayList<LinkedList<Number>>();
        dataInit=false;
    }

    public float[] addSamples(float [] data){
        for(int i=0;i<data.length;i++){
            if(!dataInit){
                dataLists.add(new LinkedList<Number>());
            }
            dataLists.get(i).addLast(data[i]);
            if(dataLists.get(i).size()>filterWindow){
                dataLists.get(i).removeFirst();
            }
        }
        dataInit= true;
        float [] means=new float[dataLists.size()];
        for(int i=0;i<dataLists.size();i++){
            means[i]=getMean(dataLists.get(i));
        }
        return means;
    }

    private float getMean(List<Number> data){
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
