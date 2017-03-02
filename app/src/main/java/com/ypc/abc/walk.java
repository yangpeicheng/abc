package com.ypc.abc;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.res.TypedArrayUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class walk extends SensorData {
    DrawPoint plot;
    LpfFilter acclpf=new LpfFilter();
    float[] vacc=new float[3];
    float[] gravity=new float[3];
    LpfFilter gravitylpf=new LpfFilter();
    float[] distance=new float[3];
    float[] velocity=new float[3];
    float[] lastDistance=new float[3];
    float[] geoAcc=new float[3];
    Button endButton;
    GetInteralAcc getInteralAcc=new GetInteralAcc();
    DataSegmentation srcDataSegmentation=new DataSegmentation();
    Handler handler=new Handler();
    Handler writerHandler=new Handler();
    private WriteCsv writeAcceleration=new WriteCsv("Acceleration.csv");
    private WriteCsv writeDistance=new WriteCsv("distance.csv");
    private WriteCsv writeVelocity=new WriteCsv("velocity.csv");
    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(this,20);
            calculate();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk);
        endButton=(Button)findViewById(R.id.walk_end);
        plot=(DrawPoint)findViewById(R.id.plot);
        handler.postDelayed(runnable,20);
        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sensorManager.unregisterListener(walk.this);
                handler.removeCallbacks(runnable);
                srcDataSegmentation.DataEnd();
                CompareTemplate compareTemplate=new CompareTemplate(srcDataSegmentation);
                boolean result=compareTemplate.findBestTemplate(srcDataSegmentation,plot);
                if(!result)
                    Toast.makeText(walk.this,"error",Toast.LENGTH_SHORT);
            }
        });
    }
    private void calculate(){
        //System.arraycopy(vAcceleration,0,vacc,0,vAcceleration.length);
        vacc=acclpf.HighPassfliter(vAcceleration);
        gravity=gravitylpf.LowPassfliter(vAcceleration);
        if(hasInitialOrientation==false)
            return;
        count++;
        if(trough.judge()>0){
            getInteralAcc.Reset();
            if(count-troughCount>1){
                troughCount=count;
                if(turn.addSample(fusedOrientation[0]))
                    turnFlag=1;
            }
            if(trough.judge()==2){
                if(different(distance,lastDistance)){
                    srcDataSegmentation.addSample(distance[0],distance[1],turnFlag);
                    System.arraycopy(distance,0,lastDistance,0,distance.length);
                }
                turnFlag=0;
            }
        }
        else {
            geoAcc=VectorOperation.matrixMultiVector(currentRotationMatrix,vacc);
            distance=getInteralAcc.calculateDistance(VectorOperation.matrixMultiVector(currentRotationMatrix,vacc),lasttime);
            velocity=getInteralAcc.getVelocityLast();
            handler.post(updateDistanceDisplayTask);
        }
        writerHandler.post(updateDataTask);
    }
    public void updateDistanceDisplay() {
        turnFlag=0;
        plot.updatePoint(distance[0], -distance[1]);
    }
    private Runnable updateDistanceDisplayTask = new Runnable() {
        public void run() {
            updateDistanceDisplay();
        }
    };
    protected boolean different(float [] a,float [] b){
        if(Math.abs(a[0]-b[0])<0.0001f&&Math.abs(a[1]-b[1])<0.0001f){
            return false;
        }
        return true;
    }
    public void writeData(){
        writeDistance.writeData(distance);
        writeVelocity.writeData(velocity);
        writeAcceleration.writeData(new float[]{geoAcc[0],geoAcc[1],geoAcc[2],trough.judge(),fusedOrientation[1],(float)Math.sqrt(gravity[0]*gravity[0]
        +gravity[1]*gravity[1]+gravity[2]*gravity[2])});
    }
    private Runnable updateDataTask=new Runnable() {
        @Override
        public void run() {
            writeData();
        }
    };
}
