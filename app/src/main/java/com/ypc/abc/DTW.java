package com.ypc.abc;

import java.util.ArrayList;

/**
 * Created by user on 2016/12/21.
 */

public class DTW {
    private final static float INFINITY=100000;
    public static float DTWDistance(ArrayList<Point> s,ArrayList<Point> t){
        float[][] matrix=new float[s.size()][t.size()];
        final int maxS=s.size()-1;
        final int maxT=t.size()-1;
        for(int i=1;i<s.size();i++)
            matrix[i][0]=INFINITY;
        for(int i=1;i<t.size();i++)
            matrix[0][i]=INFINITY;
        matrix[0][0]=0;
        for(int i=1;i<s.size();i++){
            for (int j=1;j<t.size();j++){
                matrix[i][j]=getDistance(s.get(i),t.get(j))+Math.min(Math.min(matrix[i-1][j],matrix[i][j-1]),matrix[i-1][j-1]);
            }
        }
        return matrix[maxS][maxT];
    }
    private static float getDistance(Point X,Point Y){
        return (float) Math.sqrt((X.x-Y.x)*(X.x-Y.x)+(X.y-Y.y)*(X.y-Y.y));
    }
    public static float CompareTemplate(DataSegmentation calculated,DataSegmentation template){
        float result=0.0f;
        int i=0;
        if(calculated.dataLists.size()!=template.dataLists.size())
            return 999999;
        while (true){
            ArrayList<Point> calculatedPoint=calculated.CoordinateChange(i);
            ArrayList<Point> templatePoint=template.CoordinateChange(i);
            if (calculatedPoint==null||templatePoint==null)
                break;
            result+=DTW.DTWDistance(calculatedPoint,templatePoint);
            i++;
        }
        return result;
    }
    public static float Compare3DTemplate(Data3DSegmentation calculated,Data3DSegmentation template){
        float result=0.0f;
        int i=0;
        if(calculated.dataLists.size()!=template.dataLists.size())
            return 999999;
        while (true){
            ArrayList<Point> calculatedPoint=calculated.CoordinateChange(i);
            ArrayList<Point> templatePoint=template.CoordinateChange(i);
            if (calculatedPoint==null||templatePoint==null)
                break;
            result+=DTW.DTWDistance(calculatedPoint,templatePoint);
            i++;
        }
        return result;
    }
}
