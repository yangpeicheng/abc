package com.ypc.abc;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by user on 2016/12/20.
 */

public class DataSegmentation {
        public ArrayList<LinkedList<Point>> dataLists;
        private boolean init;
        private int count=0;
        public DataSegmentation(){
            dataLists=new ArrayList<LinkedList<Point>>();
            init=false;
        }
        public void addSample(float data,float data2,int flag){
            if(!init){
                dataLists.add(new LinkedList<Point>());
                dataLists.get(count).addLast(new Point(data,data2));
                init=true;
                return;
            }
            if(flag==1){
                count++;
                dataLists.add(new LinkedList<Point>());
                dataLists.get(count).addLast(new Point(data,data2));
            }else {
                dataLists.get(count).addLast(new Point(data,data2));
            }
        }
        public void DataEnd(){
            if(init) {
                dataLists.add(new LinkedList<Point>());
                dataLists.get(count + 1).addLast(dataLists.get(count).getLast());
                count++;
            }
        }
        public ArrayList<Point> CoordinateChange(int start){
            if(start+2>count)
                return null;
            ArrayList<Point> result=new ArrayList<>();
            Point A,B,C,eX,eY;
            A=dataLists.get(start).get(0);
            B=dataLists.get(start+1).get(0);
            C=dataLists.get(start+2).get(0);
            eX=new Point((C.x-A.x)/getDistance(A,C),(C.y-A.y)/getDistance(A,C));
            Point temp=new Point(eX.y,-eX.x);
           /*if(dotProduct(temp,B)>=0)
                eY=temp;
            else
                eY=new Point(-eX.y,eX.x);*/
            eY=temp;
            for(int i=0;i<2;i++){
                for(int j=0;j<dataLists.get(i+start).size();j++){
                    Point t=dataLists.get(i+start).get(j);
                    Point k=new Point(t.x-A.x,t.y-A.y);
                    result.add(new Point(dotProduct(k,eX),dotProduct(k,eY)));
                }
            }
            return result;
        }
         public float getDistance(Point X,Point Y){
            return (float)Math.sqrt((X.x-Y.x)*(X.x-Y.x)+(X.y-Y.y)*(X.y-Y.y));
        }
        public float dotProduct(Point X,Point Y){
            return X.x*Y.x+X.y*Y.y;
        }
}
