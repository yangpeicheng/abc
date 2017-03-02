package com.ypc.abc;

import android.util.Log;
import android.widget.Toast;

/**
 * Created by 10959 on 2017/2/13.
 */

public class Compare3DTemplate {
    private int templateNum;
    private Data3DSegmentation[] templates;
    private Data3DSegmentation srcData3DSegmentation;
    private Point3D[][] templatePoints;
    private boolean templateInitFlag=false;
    private WriteCsv resultOfCompare=new WriteCsv("result.csv");
    public Compare3DTemplate(Data3DSegmentation data3DSegmentation){
        srcData3DSegmentation=data3DSegmentation;
        iniTemplate(data3DSegmentation.getSize());
    }
    private void iniTemplate(int[] size){
        if(size==null)
            return;
        ReadCsv configFile=new ReadCsv("armconfig.csv");
        String[] first=configFile.readNext();
        if(first==null||first.length!=1)
            return;
        templateNum=Integer.parseInt(first[0]);
        //templateLen=Integer.parseInt(first[1]);
        if(templateNum==0)
            return;
        // if(size.length+1<templateLen)
        //    return;
        templatePoints=new Point3D[templateNum][];
        templates=new Data3DSegmentation[templateNum];
        for(int i=0;i<templateNum;i++){
            String[] temp=configFile.readNext();
            if(temp==null||temp.length!=1)
                break;
            int templateLen=Integer.parseInt(temp[0]);
            Log.d("length",String.valueOf(templateLen));
            templates[i]=new Data3DSegmentation();
            templatePoints[i]=new Point3D[templateLen];
            for(int j=0;j<templateLen;j++) {
                String[] data = configFile.readNext();
                if (data.length != 3)
                    return;
                templatePoints[i][j]=new Point3D(Float.parseFloat(data[0]),Float.parseFloat(data[1]),Float.parseFloat(data[2]));
            }
        }
        for(int k=0;k<templates.length;k++){
            for(int i=0;i<templatePoints[k].length-1;i++){
                if(i>=size.length) {
                    templates[k].addSample(templatePoints[k][i].x,templatePoints[k][i].y,templatePoints[k][i].z,1);
                    continue;
                }
                float avgX=(templatePoints[k][i+1].x-templatePoints[k][i].x)/size[i];
                float avgY=(templatePoints[k][i+1].y-templatePoints[k][i].y)/size[i];
                float avgZ=(templatePoints[k][i+1].z-templatePoints[k][i].z)/size[i];
                templates[k].addSample(templatePoints[k][i].x,templatePoints[k][i].y,templatePoints[k][i].z,1);
                for(int j=1;j<size[i]+1;j++){
                    templates[k].addSample(templatePoints[k][i].x+j*avgX,templatePoints[k][i].y+j*avgY,templatePoints[k][i].z+j*avgZ,0);
                }
            }
        }
        for(int i=0;i<templates.length;i++)
            templates[i].DataEnd();
        templateInitFlag=true;
    }
    public int findbest(DrawContour drawContour){
        if(!templateInitFlag) {
            Log.d("compare","fail");
            return -1;
        }
        int bestNo=-1;
        float minDistance=99999;
        Log.d("length",String.valueOf(srcData3DSegmentation.dataLists.size()));
        for(int i=0;i<templates.length;i++){
            float tempDistance=DTW.Compare3DTemplate(srcData3DSegmentation,templates[i]);
            Log.d("value",String.valueOf(tempDistance));
            resultOfCompare.writeData(new float[]{tempDistance});
            if(tempDistance<minDistance){
                bestNo=i;
                minDistance=tempDistance;
            }
        }
        resultOfCompare.closeFile();
        Log.d("index",String.valueOf(bestNo));
        if(bestNo!=-1)
            drawContour.drawbest(templatePoints[bestNo]);
        return bestNo;
    }
}
