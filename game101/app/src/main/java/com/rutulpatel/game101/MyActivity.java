package com.rutulpatel.game101;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Environment;
import android.os.PowerManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;


public class MyActivity extends ActionBarActivity implements View.OnTouchListener, SensorEventListener, View.OnClickListener {

    StringBuilder stringBuilder = new StringBuilder();
    TextView touchInfo, accInfo, fileInfo, soundInfo;
    Button renderView;
    float[] x = new float[10];
    float[] y = new float[10];
    boolean[] touched = new boolean[10];
    int[] id = new int[10];
    SoundPool soundpool;
    MediaPlayer mediaPlayer;
    int walkingId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * Getrid of actionbar
         */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_my);
        touchInfo = (TextView) findViewById(R.id.touchInfo);
        accInfo = (TextView) findViewById(R.id.accelerometerInfo);
        fileInfo = (TextView) findViewById(R.id.fileInfo);
        soundInfo = (TextView) findViewById(R.id.soundInfo);
        renderView = (Button) findViewById(R.id.render_view_btn);
        renderView.setOnClickListener(this);

        touchInfo.setText("Touch and drag one finger only");
        getWindow().getDecorView().setOnTouchListener(this);

        /**
         * Enabling wake lock
         */
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        /**
         * ACCESS A FILE IN ASSETS FOLDER /src/main/assets/

        AssetManager assetManager = getAssets();
        InputStream inputStream = null;
        try {
            inputStream = assetManager.open("texts/myawesometext.txt");
            String text = loadTextFile(inputStream);
            fileInfo.setText(text);
        } catch (IOException e) {
            fileInfo.setText("Couldn't load file");
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    fileInfo.setText("Couldn't close file");
                }
            }
        }
         */


        /**
         * Access files from external storage
         */
        String state = Environment.getExternalStorageState();
        if (!state.equals(Environment.MEDIA_MOUNTED)) {
            fileInfo.setText("No external storage mounted");
        } else {
            File externalDir = Environment.getExternalStorageDirectory();
            File textFile = new File(externalDir.getAbsolutePath() + File.separator + "text.txt");
            try {
                writeTextFile(textFile, "this is a test, roger...");
                String text = readTextFile(textFile);
                fileInfo.setText(text);
                if (!textFile.delete()) {
                    fileInfo.setText("Couldn't remove temporary file...");
                }
            } catch (IOException e) {
                fileInfo.setText("Something went wrong! " + e.getMessage());
            }
        }


        /**
         * Music initialization
         */
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mediaPlayer = MediaPlayer.create(this, R.raw.walking);
        /*
        soundpool = new SoundPool(20, AudioManager.STREAM_MUSIC, 0);
        try {
            AssetManager assetManager = getAssets();
            AssetFileDescriptor descriptor = assetManager.openFd("music/walking.ogg");
            walkingId = soundpool.load(descriptor, 1);
            Toast.makeText(this, "walking id: " + walkingId, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
           soundInfo.setText("couldn't load sound effect from asset, " + e.getMessage());
        }
        */
        soundInfo.setOnTouchListener(this);

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

    public String loadTextFile(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        byte[] bytes = new byte[4096];
        int len = 0;
        while ((len = inputStream.read(bytes)) > 0) {
            byteStream.write(bytes, 0, len);
        }
        return new String(byteStream.toByteArray(), "UTF-8");
    }

    private void writeTextFile(File file, String text) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(text);
        writer.close();
    }

    public String readTextFile(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        StringBuilder text = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            text.append(line);
            text.append("/n");
        }
        reader.close();
        return text.toString();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_ID_MASK) >>
                MotionEvent.ACTION_POINTER_ID_SHIFT;
        int pointerCount = event.getPointerCount();

        if (v.getId() == R.id.soundInfo) {
            switch(event.getAction()) {
                case MotionEvent.ACTION_UP:
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.stop();
                            mediaPlayer.release();
                            mediaPlayer = null;
                        }
                        if (mediaPlayer == null) {
                            mediaPlayer = MediaPlayer.create(this, R.raw.walking);
                        }
                        mediaPlayer.start();
                        break;
            }
            return true;
        }

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

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.render_view_btn:
                Intent render = new Intent(this, com.rutulpatel.game101.RenderViewTest.class);
                startActivity(render);
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            if (isFinishing()) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        }
    }

}
