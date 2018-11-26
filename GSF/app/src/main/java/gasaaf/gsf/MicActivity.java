package gasaaf.gsf;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static java.lang.Thread.sleep;

public class MicActivity extends AppCompatActivity {


    Button buttonStart, buttonStop,showAmp;
    MediaRecorder mediaRecorder;
    SoundMeter sm;
    CheckTh cT;
    TextView Display;
    volatile Boolean toExit;
    public Boolean isDanger;
    final int REQUEST_PERMISSION_CODE = 1000;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mic);
        buttonStart = (Button) findViewById(R.id.button6);
        buttonStop = (Button) findViewById(R.id.button6);
        showAmp = (Button) findViewById(R.id.button6);
        Display = (TextView) findViewById(R.id.textView2);
        isDanger = false;
        requestRecordAudioPermission();
        mediaRecorder = new MediaRecorder();
        Display.setText("Little Bo Beep");
        sm = new SoundMeter();
        toExit = false;



        final Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                int counter = 0;
                while(!toExit){

                    try {
                        if (sm.getAmplitude() > 40) {


                            isDanger = true;
                            counter++;
                            final int finalCounter = counter;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Display.setText("bad! " + finalCounter);
                                }
                            });
                        }
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    isDanger = false;
                }
            }
        });

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                sm.start();
                t.start();
                Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();

            }
        });


        showAmp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sm.ar != null)
                {
                    Toast.makeText(getApplicationContext(), "Sound Intensity: " + sm.getAmplitude() + "db", Toast.LENGTH_LONG).show();
                }
            }
        });


        buttonStop.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sm.stop();
                toExit = true;



                Toast.makeText(getApplicationContext(), "Recording ended", Toast.LENGTH_LONG).show();
            }
        }));

    }




    private void requestRecordAudioPermission() {
        //check API version, do nothing if API version < 23!
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion > android.os.Build.VERSION_CODES.LOLLIPOP){

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {

                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.d("Activity", "Granted!");

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d("Activity", "Denied!");
                    finish();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }



    public void startService(View view)
    {
        Intent intent = new Intent(this,MicService.class);
        startService(intent);
    }


    public void stopService(View view)
    {
        Intent intent = new Intent(this,MicService.class);
        stopService(intent);
    }



}


