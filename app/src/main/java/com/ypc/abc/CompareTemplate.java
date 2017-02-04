package com.ypc.abc;

import java.util.ArrayList;

/**
 * Created by 10959 on 2017/2/4.
 */

public class CompareTemplate {
    private int templateNum,templateLen;
    private Point[][] templatePoints;
    private DataSegmentation[] templates;
    public CompareTemplate(DataSegmentation dataSegmentation){
        initTemplate(dataSegmentation);
    }
    private void initTemplate(DataSegmentation dataSegmentation){
        ReadCsv configFile=new ReadCsv("config.csv");
        if(configFile==null)
            return;
        String[] first=configFile.readNext();
        if(first==null||first.length!=2)
            return;
        templateNum=Integer.parseInt(first[0]);
        templateLen=Integer.parseInt(first[1]);
        if(templateNum==0||templateLen==0)
            return;
        templatePoints=new Point[templateNum][];
        templates=new DataSegmentation[templateNum];
        for(int i=0;i<templateNum;i++){
            templates[i]=new DataSegmentation();
            templatePoints[i]=new Point[templateLen];
            for(int j=0;j<templateLen;j++) {
                String[] data = configFile.readNext();
                if (data.length != 2)
                    return;
                templatePoints[i][j]=new Point(Integer.parseInt(data[0]),Integer.parseInt(data[1]));
            }
        }
        for(int k=0;k<templates.length;k++){
            for(int i=0;i<templatePoints[k].length-1;i++){
                int size=dataSegmentation.dataLists.get(k).size();
                float avgX=(templatePoints[k][i+1].x-templatePoints[k][i].x)/size;
                float avgY=(templatePoints[k][i+1].y-templatePoints[k][i].y)/size;
                templates[k].addSample(templatePoints[k][i].x,templatePoints[k][i].y,1);
                for(int j=1;j<size;j++){
                    templates[k].addSample(templatePoints[k][i].x+j*avgX,templatePoints[k][i].y+j*avgY,0);
                }
            }
        }
        for(int i=0;i<templates.length;i++)
            templates[i].DataEnd();
    }
    public void findBestTemplate(DataSegmentation dataSegmentation,DrawPoint drawPoint){
        int bestNo=-1;
        float minDistance=999999;
        for(int i=0;i<templates.length;i++){
            float tempDistance=DTW.CompareTemplate(dataSegmentation,templates[i]);
            if(tempDistance<minDistance){
                bestNo=i;
                minDistance=tempDistance;
            }
        }
        ArrayList<Point> bestPoints=new ArrayList<Point>();
        for(int k=0;k<templates[bestNo].dataLists.size();k++){
            for(int j=0;j<templates[bestNo].dataLists.get(k).size();j++)
                bestPoints.add(templates[bestNo].dataLists.get(k).get(j));
        }
        drawPoint.drawBest(bestPoints);
    }
}
