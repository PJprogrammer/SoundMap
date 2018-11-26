package gasaaf.gsf;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

public class MicService extends Service{

    volatile Boolean toExit;
    public Boolean isDanger;

    SoundMeter sm;


    @Override
    public void onCreate() {
        super.onCreate();

        sm = new SoundMeter();
        isDanger = false;
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

                        }
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    isDanger = false;
                }

            }
        });










    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        Toast.makeText(this,"Service Started...", Toast.LENGTH_LONG).show();
        sm.start();






        return START_STICKY;


    }

    @Override
    public void onDestroy() {
        sm.stop();


        Toast.makeText(this,"Service Destroyed...", Toast.LENGTH_LONG).show();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }







}
