package com.ypc.abc;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;

public abstract class SensorData extends Activity implements SensorEventListener {
    protected static final float NS2S = 1.0f / 1000000000.0f;
    protected static final float EPSILON = 0.000000001f;
    protected boolean hasInitialOrientation = false;
    protected boolean stateInitializedCalibrated=false;
    private long timestampold=0;
    protected SensorManager sensorManager;
    protected float[] vMagnetic = new float[3];
    protected float[] vAcceleration = new float[3];
    protected float[] vGravity=new float[3];
    protected float[] vGyroscope=new float[3];
    protected float[] rmAccelMag = new float[9];
    protected float[] deltaRotationVector = new float[4];
    protected float[] deltaRotationMatrix=new float[9];
    protected float[] currentRotationMatrix=new float[9];
    protected float[] RotationMatrixFromVector=new float[9];
    protected float[] gyroscopeOrientation=new float[3];
    protected float[] fusedOrientation = new float[3];
    protected float[] rmAccelMagOrientation=new float[3];
    protected long lasttime;
    protected int count=0;
    protected int troughCount=0;
    protected Trough trough;
    protected Turn turn=new Turn();
    protected int turnFlag=0;
    private MeanFilterSmoothing meanFilterAcceleration;
    private MeanFilterSmoothing meanFilterMagnetic;
    private MeanFilterSmoothing meanFilterGyroscope;
    //protected Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        meanFilterAcceleration=new MeanFilterSmoothing();
        meanFilterGyroscope=new MeanFilterSmoothing();
        meanFilterMagnetic=new MeanFilterSmoothing();
        trough=new Trough();
        sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(SensorData.this,sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY),SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(SensorData.this,sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(SensorData.this,sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(SensorData.this,sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType()==Sensor.TYPE_GRAVITY){
            System.arraycopy(event.values, 0, this.vGravity, 0,
                    this.vGravity.length);
            calculateInitialOrientation();
        }
        if(event.sensor.getType()==Sensor.TYPE_LINEAR_ACCELERATION){
            System.arraycopy(event.values,0,this.vAcceleration,0,this.vAcceleration.length);
            lasttime=event.timestamp;
            vAcceleration=meanFilterAcceleration.addSamples(vAcceleration);
        }
        if(event.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD){
            System.arraycopy(event.values,0,this.vMagnetic,0,this.vMagnetic.length);
            vMagnetic=meanFilterMagnetic.addSamples(vMagnetic);
        }
        if(event.sensor.getType()==Sensor.TYPE_GYROSCOPE) {
            System.arraycopy(event.values, 0, this.vGyroscope, 0,
                    this.vGyroscope.length);
            this.vGyroscope = meanFilterGyroscope.addSamples(this.vGyroscope);
            onGyroscopeChange(this.vGyroscope, event.timestamp);
        }
        if(event.sensor.getType()==Sensor.TYPE_ROTATION_VECTOR){
            SensorManager.getRotationMatrixFromVector(RotationMatrixFromVector,event.values);
        }
    }

    private void onGyroscopeChange(float [] gyroscope,long timestamp){
        if(!hasInitialOrientation)
            return;
        if (!stateInitializedCalibrated)
        {
            System.arraycopy(rmAccelMag,0,currentRotationMatrix,0,rmAccelMag.length);
            stateInitializedCalibrated = true;
        }
        if(timestampold!=0){
            final float dT=(timestamp-timestampold)*NS2S;
            float axisX=gyroscope[0];
            float axisY=gyroscope[1];
            float axisZ=gyroscope[2];
            float magnitude=(float)Math.sqrt(axisX*axisX+axisY*axisY+axisZ*axisZ);
            if(magnitude>EPSILON){
                axisX/=magnitude;
                axisY/=magnitude;
                axisZ/=magnitude;
            }
            float theta=magnitude*dT/2.0f;
            float sinTheta=(float)Math.sin(theta);
            float cosTheta=(float)Math.cos(theta);
            deltaRotationVector[0]=sinTheta*axisX;
            deltaRotationVector[1]=sinTheta*axisY;
            deltaRotationVector[2]=sinTheta*axisZ;
            deltaRotationVector[3]=cosTheta;
            SensorManager.getRotationMatrixFromVector(deltaRotationMatrix,deltaRotationVector);
            currentRotationMatrix=VectorOperation.matrixMultiplication(currentRotationMatrix,deltaRotationMatrix);
            SensorManager.getOrientation(currentRotationMatrix,gyroscopeOrientation);
            fusedOrientation=FuseOrientation.fuseOrientation(rmAccelMagOrientation,gyroscopeOrientation);
            currentRotationMatrix = VectorOperation.getRotationMatrixFromOrientation(fusedOrientation);
            trough.insert(fusedOrientation[1]);
        }
        timestampold=timestamp;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void calculateInitialOrientation()
    {
        hasInitialOrientation =SensorManager.getRotationMatrix(
                rmAccelMag, null, vGravity, vMagnetic);
        SensorManager.getOrientation(rmAccelMag,rmAccelMagOrientation );
    }

}
