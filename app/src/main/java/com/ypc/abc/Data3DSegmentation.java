package com.ypc.abc;

import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by 10959 on 2017/2/8.
 */

public class Data3DSegmentation {
    public ArrayList<LinkedList<Point3D>> dataLists;
    private boolean init;
    private int count=0;
    private int[] dataSize;
    public Data3DSegmentation(){
        dataLists=new ArrayList<LinkedList<Point3D>>();
        init=false;
    }
    public void addSample(float dataX,float dataY,float dataZ,int flag){
        if(!init){
            dataLists.add(new LinkedList<Point3D>());
            dataLists.get(count).addLast(new Point3D(dataX,dataY,dataZ));
            init=true;
            return;
        }
        if(flag==1){
            if(dataLists.get(count).size()<10){
                dataLists.get(count).addLast(new Point3D(dataX,dataY,dataZ));
            }
            else {
                count++;
                dataLists.add(new LinkedList<Point3D>());
                dataLists.get(count).addLast(new Point3D(dataX, dataY, dataZ));
            }
        }else {
            dataLists.get(count).addLast(new Point3D(dataX,dataY,dataZ));
        }
    }
    public void DataEnd(){
        if(init) {
            dataLists.add(new LinkedList<Point3D>());
            dataLists.get(count + 1).addLast(dataLists.get(count).getLast());
            count++;
            dataSize=this.getSize();
        }
    }
    public ArrayList<Point> CoordinateChange(int start){
        if(start+2>count)
            return null;
        ArrayList<Point> result=new ArrayList<>();
        Point3D A,B,C,eX,eY,AB;
        A=dataLists.get(start).get(0);
        B=dataLists.get(start+1).get(0);
        C=dataLists.get(start+2).get(0);
        float lengthAC=getDistance(A,C);
        eX=new Point3D((C.x-A.x)/lengthAC,(C.y-A.y)/lengthAC,(C.z-A.z)/lengthAC);
        Log.d("ex",String.format("%f,%f,%f",eX.x,eX.y, eX.z));
        AB=new Point3D(B.x-A.x,B.y-A.y,B.z-A.z);
        Point3D temp=minusPoint(AB,multiple(eX,dotProduct(AB,eX)));
        eY=multiple(temp,1/getLength(temp));
        Log.d("eY",String.format("%f,%f,%f",eY.x,eY.y, eY.z));
        for(int i=0;i<2;i++){
            for(int j=0;j<dataLists.get(i+start).size();j++){
                Point3D t=dataLists.get(i+start).get(j);
                Point3D k=new Point3D(t.x-A.x,t.y-A.y,t.z-A.z);
                result.add(new Point(dotProduct(k,eX),dotProduct(k,eY)));
            }
        }
        return result;
    }
    public float getDistance(Point3D X,Point3D Y){
        return (float)Math.sqrt((X.x-Y.x)*(X.x-Y.x)+(X.y-Y.y)*(X.y-Y.y)+(X.z-Y.z)*(X.z-Y.z));
    }
    public float dotProduct(Point3D X,Point3D Y){
        return X.x*Y.x+X.y*Y.y+X.z*Y.z;
    }
    public Point3D minusPoint(Point3D X,Point3D Y){
        return new Point3D(X.x-Y.x,X.y-Y.y,X.z-Y.z);
    }
    public Point3D multiple(Point3D a,float b){
        return new Point3D(b*a.x,b*a.y,b*a.z);
    }
    public float getLength(Point3D a){
        return (float)Math.sqrt(a.x*a.x+a.y*a.y+a.z*a.z);
    }
    public int getDataLength(){ return count;}
    public int[] getSize(){
        if(dataLists.size()==0)
            return null;
        int[] size=new int[dataLists.size()-1];
        for(int i=0;i<size.length;i++){
            size[i]=dataLists.get(i).size();
        }
        return size;
    }
    public int[] getDataSize(){
        return dataSize;
    }
    public void WriteData(String name){
        WriteCsv writeCsv=new WriteCsv(name);
        for(int i=0;i<dataLists.size()-2;i++){
            ArrayList<Point> temp=this.CoordinateChange(i);
            //LinkedList<Point3D> temp=dataLists.get(i);
            for(int j=0;j<temp.size();j++){
                writeCsv.writeData(new float[]{temp.get(j).x,temp.get(j).y});
            }
            writeCsv.writeData(new float[]{-1,-1,-1});
        }
        writeCsv.closeFile();
    }

}
