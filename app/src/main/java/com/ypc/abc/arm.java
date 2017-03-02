package com.ypc.abc;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.LinkedList;

public class arm extends SensorData {
    private int LENGTH=5;
    private DrawContour drawContour;
    private Button gaugeButton,endButton,startButton;
    private Handler handler=new Handler();
    private Handler mainHandler=new Handler();
    private Handler writeHandler=new Handler();
    private float[] torsoOrientation=new float[3];
    private float[] torsoAcceleration=new float[3];
    private float[] distance=new float[3];
    private float[] lastdistance=new float[3];
    private float[] velocity=new float[3];
    private LpfFilter lpfFilter=new LpfFilter();
    private Handler uiHandler=new Handler();
    private MeanFilterSmoothing meanFilterSmoothing=new MeanFilterSmoothing();
    private MeanFilterSmoothing meanAcceleration=new MeanFilterSmoothing();
    private WriteCsv writeTorsoCsv=new WriteCsv("torsoAcc.csv");
    private WriteCsv writeDistance=new WriteCsv("distance.csv");
    private WriteCsv writeVelocity=new WriteCsv("velocity.csv");
    private float accelerationMagnitude;
    private LinkedList<Float> magnitudeList=new LinkedList<>();
    private GetInteralAcc getInteralAcc=new GetInteralAcc();
    private Trough trough=new Trough();
    private int count=0,gapcount=0;
    private boolean initOrientation=false;
    private boolean lockFlag=false;
    private Data3DSegmentation data3DSegmentation=new Data3DSegmentation();
    private Runnable calculateOrientationRun=new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(this,20);
            if(!initOrientation)
                calcualteOrientation();
         /*  else {
                Toast.makeText(arm.this,String.valueOf(torsoOrientation[0]),Toast.LENGTH_SHORT).show();
                handler.removeCallbacks(this);
            }*/
        }
    };
    private Runnable mainRun=new Runnable() {
        @Override
        public void run() {
            mainHandler.postDelayed(this,20);
            getContour();
        }
    };
    private void getContour(){
        torsoAcceleration=VectorOperation.matrixMultiVector(VectorOperation.matrixMultiplication(matrixFromAngle(torsoOrientation[0]),currentRotationMatrix),vAcceleration);
        torsoAcceleration=meanAcceleration.addSamples(torsoAcceleration);
        /*if(accelerationCount<50){
            for(int i=0;i<torsoAcceleration.length;i++)
                means[i]+=torsoAcceleration[i]/50;
            accelerationCount++;
            return;
        }
        if(accelerationCount==50){
            for(int i=0;i<means.length;i++)
                Log.d("means",String.valueOf(means[i]));
            accelerationCount++;
        }
        writeDistance.writeData(new float[]{torsoAcceleration[0],torsoAcceleration[1],torsoAcceleration[2]});
        for(int i=0;i<torsoAcceleration.length;i++)
            torsoAcceleration[i]-=means[i];*/
        accelerationMagnitude=getMagnitude(torsoAcceleration);
        magnitudeList.add(accelerationMagnitude);
        float variance=getVariance();
        if(lockFlag){
            if(variance<0.010f&&accelerationMagnitude<1f&&gapcount>3) {
                lockFlag = false;
                gapcount = 0;
            }
            else if(gapcount>25) {
                lockFlag = false;
                gapcount=0;
            }
            else
                gapcount++;
        }
        if(trough.armJudge())
            lockFlag=true;
        if((variance<0.010f&&accelerationMagnitude<0.4f)||lockFlag) {
            getInteralAcc.Reset();
        }
        else {
            getInteralAcc.calculateArmDistance(torsoAcceleration, lasttime);
        }
        velocity = getInteralAcc.getVelocityLast();
        distance=getInteralAcc.getDistance();
        trough.insert(getMagnitude(velocity));
       // Log.d("distance",String.valueOf(calculateDistance(lastdistance,distance)));
        if(calculateDistance(lastdistance,distance)>0.0002){
            data3DSegmentation.addSample(distance[0],distance[1],distance[2],trough.armJudge()?1:0);
        }
        System.arraycopy(distance,0,lastdistance,0,distance.length);
        //writeHandler.post(updateDataTask);
        //drawContour.updatePoint(new float[]{0.2f,0.2f,0.2f});
        uiHandler.post(updateDistanceDisplayTask);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arm);
        drawContour=(DrawContour)findViewById(R.id.arm_contour);
        gaugeButton=(Button)findViewById(R.id.gauge_orientation);
        startButton=(Button)findViewById(R.id.arm_start);
        endButton=(Button)findViewById(R.id.arm_end);
        gaugeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initOrientation=false;
                handler.postDelayed(calculateOrientationRun,20);
            }
        });
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(arm.this,String.valueOf(torsoOrientation[0]),Toast.LENGTH_SHORT).show();
                if(initOrientation) {
                    handler.removeCallbacks(calculateOrientationRun);
//                    Log.d("orientation",String.valueOf(torsoOrientation[0]));
                }
                else{
                    Toast.makeText(arm.this,"calculate orientation first",Toast.LENGTH_SHORT).show();
                    return;
                }
                mainHandler.postDelayed(mainRun,20);
            }
        });
        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainHandler.removeCallbacks(mainRun);
                sensorManager.unregisterListener(arm.this);
                writeTorsoCsv.closeFile();
                writeDistance.closeFile();
                writeVelocity.closeFile();
                if(getMagnitude(distance)>1){
                    Toast.makeText(arm.this,"error",Toast.LENGTH_SHORT).show();
                    return;
                }
               // data3DSegmentation.DataEnd();
                Compare3DTemplate compare3DTemplate=new Compare3DTemplate(data3DSegmentation);
                int index=compare3DTemplate.findbest(drawContour);
                if(index==-1){
                    Toast.makeText(arm.this,"匹配错误",Toast.LENGTH_SHORT).show();
                }
                else if(index==0){
                    Toast.makeText(arm.this,"Z",Toast.LENGTH_SHORT).show();
                }
                else if(index==1){
                    Toast.makeText(arm.this,"U",Toast.LENGTH_SHORT).show();
                }
                else if(index==2){
                    Toast.makeText(arm.this,"Opening the fridge",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void calcualteOrientation(){
        if(initOrientation)
            return;
        if(count<20){
            torsoOrientation=meanFilterSmoothing.addSamples(fusedOrientation);
            count++;
        }
        else{
            initOrientation=true;
        }
    }
    private float[] matrixFromAngle(float a){
        float[] result=new float[9];
        float cosa=(float)Math.cos(Math.abs(a));
        float sina=(float)Math.sin(Math.abs(a));
        result[0]=cosa;
        result[2]=0;
        result[4]=cosa;
        result[5]=0;
        result[6]=0;
        result[7]=0;
        result[8]=1;
        if(a>=0){
            result[1]=-sina;
            result[3]=sina;
        }
        else {
            result[1]=sina;
            result[3]=-sina;
        }
        return result;
    }
    public void updateDistanceDisplay() {
        drawContour.updatePoint(distance);
    }
    private Runnable updateDistanceDisplayTask = new Runnable() {
        public void run() {
            updateDistanceDisplay();
        }
    };
    public void writeData(){
        writeTorsoCsv.writeData(new float[]{torsoAcceleration[0],torsoAcceleration[1],torsoAcceleration[2],trough.armJudge()?1:0});
        writeDistance.writeData(distance);
        //writeTorsoCsv.writeData(new float[]{torsoAcceleration[0],torsoAcceleration[1],torsoAcceleration[2]});
        //writeDistance.writeData(new float[]{getMagnitude(torsoAcceleration),getMagnitude(distance),getMagnitude(velocity),trough.armJudge()?1:0});
        //writeDistance.writeData(new float[]{torsoAcceleration[1],distance[1],velocity[1]});
        writeVelocity.writeData(velocity);
    }
    private Runnable updateDataTask=new Runnable() {
        @Override
        public void run() {
            writeData();
        }
    };
    private float getMagnitude(float[] data){
        float result=0;
        for(int i=0;i<data.length;i++){
            result+=data[i]*data[i];
        }
        return (float)Math.sqrt(result);
    }
    private float getVariance(){
        if(magnitudeList.size()>LENGTH)
            magnitudeList.removeFirst();
        if(magnitudeList.size()<LENGTH)
            return 0;
        float mean=0;
        for(int i=0;i<magnitudeList.size();i++)
            mean+=magnitudeList.get(i);
        mean/=LENGTH;
        float result=0;
        for(int i=0;i<magnitudeList.size();i++)
            result+=(magnitudeList.get(i)-mean)*(magnitudeList.get(i)-mean);
        return (float)Math.sqrt(result/LENGTH);
    }
    public float calculateDistance(float[] a, float[] b){
        float result=0;
        for(int i=0;i<a.length;i++){
            result+=(a[i]-b[i])*(a[i]-b[i]);
        }
        return (float)Math.sqrt(result);
    }
}
