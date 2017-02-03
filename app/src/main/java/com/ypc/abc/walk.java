package com.ypc.abc;

import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class walk extends SensorData {
    DrawPoint plot;
    LpfFilter acclpf=new LpfFilter();
    float[] vacc=new float[3];
    float[] distance=new float[3];
    Button endButton;
    GetInteralAcc getInteralAcc=new GetInteralAcc();
    Handler handler=new Handler();
    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            Log.d("distance", String.valueOf(System.currentTimeMillis()));
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
            }
        });
    }
    private void calculate(){
        vacc=acclpf.HighPassfliter(vAcceleration);
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
                turnFlag=0;
            }

        }
        else {
            distance=getInteralAcc.calculateDistance(VectorOperation.matrixMultiVector(currentRotationMatrix,vacc),lasttime);
            handler.post(updateDistanceDisplayTask);
        }
    }
    public void updateDistanceDisplay() {
        // writeOrientation.writeData(new float[]{1,1});
        //turnFlag=0;
        plot.updatePoint(distance[0], -distance[1]);
    }
    private Runnable updateDistanceDisplayTask = new Runnable() {
        public void run() {
            updateDistanceDisplay();
        }
    };
}
