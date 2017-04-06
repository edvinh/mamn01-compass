package com.edvinhavic.mamn01.compass;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void onCompassClick (View v) {
        Intent i = new Intent(this, CompassActivity.class);
        this.startActivity(i);
    }

    public void onAccelerometerClick (View v) {
        Intent i = new Intent(this, AccelerometerActivity.class);
        this.startActivity(i);
    }
}
