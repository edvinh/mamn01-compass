package com.edvinhavic.mamn01.compass;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Code based on:
 * http://www.techrepublic.com/article/pro-tip-create-your-own-magnetic-compass-using-androids-internal-sensors/
 */

public class CompassActivity extends AppCompatActivity implements SensorEventListener {


    private ImageView mImage;
    private float currDeg = 0f;
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private float[] mR = new float[9];
    private float[] mOrientation = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    private SensorManager mSensorManager;
    private Sensor mMagnetometer;
    private Sensor mAccelerometer;
    private TextView mHeading;
    private final static float LOWPASS_ALPHA = 0.10f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_compass);

        mImage = (ImageView) findViewById(R.id.compass_img);
        mHeading = (TextView) findViewById(R.id.heading);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this, mMagnetometer);
        mSensorManager.unregisterListener(this, mAccelerometer);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == mAccelerometer) {
            mLastAccelerometerSet = true;
            mLastAccelerometer = lowPass(event.values.clone(), mLastAccelerometer);
//            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
        } else if (event.sensor == mMagnetometer) {
            mLastMagnetometerSet = true;
            mLastMagnetometer = lowPass(event.values.clone(), mLastMagnetometer);
//            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
        }

        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(mR, mOrientation);
            float azimuthInRadians = mOrientation[0];
            float azimuthInDegrees = (float) (Math.toDegrees(azimuthInRadians) + 360) % 360;
            int roundedAzimuth = Math.round(azimuthInDegrees);


            mHeading.setText(Integer.toString(roundedAzimuth) + "° " + getHeadingDir(roundedAzimuth));
            RotateAnimation ra = new RotateAnimation(
                    currDeg,
                    -azimuthInDegrees,
                    Animation.RELATIVE_TO_SELF,
                    0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f
            );

            ra.setDuration(250);
            ra.setFillAfter(true);
            mImage.startAnimation(ra);
            currDeg = -azimuthInDegrees;
        }
    }

    private String getHeadingDir(int direction) {
        if (direction < 22 || direction > 336) {
            return "N";
        } else if (direction < 67) {
            return "NE";
        } else if (direction < 113) {
            return "E";
        } else if (direction < 158) {
            return "SE";
        } else if (direction < 202) {
            return "S";
        } else if (direction < 247) {
            return "SW";
        } else if (direction < 291) {
            return "W";
        } else {
            return "NW";
        }
    }

    private float[] lowPass(float[] input, float[] output) {
        if (output == null) return input;

        for (int i = 0; i < input.length; i++) {
            output[i] = output[i] + LOWPASS_ALPHA * (input[i] - output[i]);
        }

        return output;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
