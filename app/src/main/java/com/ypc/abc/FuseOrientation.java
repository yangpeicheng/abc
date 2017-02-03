package com.ypc.abc;

/**
 * Created by user on 2016/12/16.
 */

public class FuseOrientation {
    public static final float FILTER_COEFFICIENT=0.98f;
    public static float[] fuseOrientation(float[] accMagOrientation,float[] gyroscopeOrientation) {
        float[] fusedOrientation=new float[3];
        float oneMinusCoeff = 1.0f - FILTER_COEFFICIENT;
        if (gyroscopeOrientation[0] < -0.5 * Math.PI && accMagOrientation[0] > 0.0) {
            fusedOrientation[0] = (float) (FILTER_COEFFICIENT * (gyroscopeOrientation[0] + 2.0 * Math.PI) + oneMinusCoeff * accMagOrientation[0]);
            fusedOrientation[0] -= (fusedOrientation[0] > Math.PI) ? 2.0 * Math.PI : 0;
        }
        else if (accMagOrientation[0] < -0.5 * Math.PI && gyroscopeOrientation[0] > 0.0) {
            fusedOrientation[0] = (float) (FILTER_COEFFICIENT * gyroscopeOrientation[0] + oneMinusCoeff * (accMagOrientation[0] + 2.0 * Math.PI));
            fusedOrientation[0] -= (fusedOrientation[0] > Math.PI)? 2.0 * Math.PI : 0;
        }
        else {
            fusedOrientation[0] = FILTER_COEFFICIENT * gyroscopeOrientation[0] + oneMinusCoeff * accMagOrientation[0];
        }

        // pitch
        if (gyroscopeOrientation[1] < -0.5 * Math.PI && accMagOrientation[1] > 0.0) {
            fusedOrientation[1] = (float) (FILTER_COEFFICIENT * (gyroscopeOrientation[1] + 2.0 * Math.PI) + oneMinusCoeff * accMagOrientation[1]);
            fusedOrientation[1] -= (fusedOrientation[1] > Math.PI) ? 2.0 * Math.PI : 0;
        }
        else if (accMagOrientation[1] < -0.5 * Math.PI && gyroscopeOrientation[1] > 0.0) {
            fusedOrientation[1] = (float) (FILTER_COEFFICIENT * gyroscopeOrientation[1] + oneMinusCoeff * (accMagOrientation[1] + 2.0 * Math.PI));
            fusedOrientation[1] -= (fusedOrientation[1] > Math.PI)? 2.0 * Math.PI : 0;
        }
        else {
            fusedOrientation[1] = FILTER_COEFFICIENT * gyroscopeOrientation[1] + oneMinusCoeff * accMagOrientation[1];
        }

        // roll
        if (gyroscopeOrientation[2] < -0.5 * Math.PI && accMagOrientation[2] > 0.0) {
            fusedOrientation[2] = (float) (FILTER_COEFFICIENT * (gyroscopeOrientation[2] + 2.0 * Math.PI) + oneMinusCoeff * accMagOrientation[2]);
            fusedOrientation[2] -= (fusedOrientation[2] > Math.PI) ? 2.0 * Math.PI : 0;
        }
        else if (accMagOrientation[2] < -0.5 * Math.PI && gyroscopeOrientation[2] > 0.0) {
            fusedOrientation[2] = (float) (FILTER_COEFFICIENT * gyroscopeOrientation[2] + oneMinusCoeff * (accMagOrientation[2] + 2.0 * Math.PI));
            fusedOrientation[2] -= (fusedOrientation[2] > Math.PI)? 2.0 * Math.PI : 0;
        }
        else {
            fusedOrientation[2] = FILTER_COEFFICIENT * gyroscopeOrientation[2] + oneMinusCoeff * accMagOrientation[2];
        }
        //  currentRotationMatrix = getRotationMatrixFromOrientation(fusedOrientation);
        // System.arraycopy(fusedOrientation, 0, gyroscopeOrientation, 0, 3);
        return fusedOrientation;
    }
}
