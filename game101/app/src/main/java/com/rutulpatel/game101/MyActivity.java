package com.rutulpatel.game101;

import android.content.Context;
import android.content.res.AssetManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;


public class MyActivity extends ActionBarActivity implements View.OnTouchListener, SensorEventListener {

    StringBuilder stringBuilder = new StringBuilder();
    TextView touchInfo, accInfo, fileInfo;
    float[] x = new float[10];
    float[] y = new float[10];
    boolean[] touched = new boolean[10];
    int[] id = new int[10];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        touchInfo = (TextView) findViewById(R.id.touchInfo);
        accInfo = (TextView) findViewById(R.id.accelerometerInfo);
        fileInfo = (TextView) findViewById(R.id.fileInfo);
        touchInfo.setText("Touch and drag one finger only");
        touchInfo.setOnTouchListener(this);

        /**
         * ACCESS A FILE IN ASSETS FOLDER /src/main/assets/
         */
        AssetManager assetManager = getAssets();
        InputStream inputStream = null;
        try {
            inputStream = assetManager.open("texts/myawesometext.txt");
            String text = loadTextFile(inputStream);
            textView.setText(text);
        } catch (IOException e) {

        }

        /**
         * SENSOR INITIALIZATION
         */
        SensorManager sensorManager = (SensorManager)
                getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).size() == 0) {
            accInfo.setText("No accelerometer installed");
        } else {
            Sensor accelerometer = sensorManager.getSensorList(
                    Sensor.TYPE_ACCELEROMETER).get(0);
            if (!sensorManager.registerListener(this, accelerometer,
                    SensorManager.SENSOR_DELAY_GAME)) {
                accInfo.setText("Couldn't register sensor listener");
            }
        }


        for (int i = 0; i < 10; i++) {
            id[i] = -1;
        }
        updateTextView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateTextView() {
        stringBuilder.setLength(0);
        for (int i = 0; i < 10; i++) {
            stringBuilder.append(touched[i]);
            stringBuilder.append(", ");
            stringBuilder.append(id[i]);
            stringBuilder.append(", ");
            stringBuilder.append(x[i]);
            stringBuilder.append(", ");
            stringBuilder.append(y[i]);
            stringBuilder.append("\n");
        }
        touchInfo.setText(stringBuilder.toString());
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_ID_MASK) >>
                MotionEvent.ACTION_POINTER_ID_SHIFT;
        int pointerCount = event.getPointerCount();
        for (int i = 0; i < 10; i++) {
            if (i >= pointerCount) {
                touched[i] = false;
                id[i] = -1;
                continue;
            }
            if (event.getAction() != MotionEvent.ACTION_MOVE && i != pointerIndex) {
                continue;
            }
            int pointerId = event.getPointerId(i);
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN:
                    touched[i] = true;
                    id[i]=pointerId;
                    x[i] = (int) event.getX(i);
                    y[i] = (int) event.getY(i);
                    break;


                case MotionEvent.ACTION_MOVE:
                    touched[i] = true;
                    id[i]=pointerId;
                    x[i] = (int) event.getX(i);
                    y[i] = (int) event.getY(i);
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                case MotionEvent.ACTION_OUTSIDE:
                case MotionEvent.ACTION_CANCEL:
                    touched[i] = true;
                    id[i]=pointerId;
                    x[i] = (int) event.getX(i);
                    y[i] = (int) event.getY(i);
                    break;
            }

        }

        updateTextView();
        return true;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        stringBuilder.setLength(0);
        stringBuilder.append("x: ");
        stringBuilder.append(event.values[0]);
        stringBuilder.append(", y: ");
        stringBuilder.append(event.values[1]);
        stringBuilder.append(", z: ");
        stringBuilder.append(event.values[2]);
        accInfo.setText(stringBuilder.toString());
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
