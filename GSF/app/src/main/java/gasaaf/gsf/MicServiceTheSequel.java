package gasaaf.gsf;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MicServiceTheSequel extends Service{

    boolean toExit = false;
    boolean isThreatDetectionOn;
    SoundMeter sMs = new SoundMeter();
    boolean isDanger = false;
    int counter;

    boolean updateLocation;


    class MicServiceTheSequelBinder extends Binder {
        public MicServiceTheSequel getService()
        {
            return MicServiceTheSequel.this;
        }
    }

    private IBinder mBinder = new MicServiceTheSequelBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i("Bind", "thats good");
        return mBinder;
    }

    @Override
    public void onDestroy() {
        stopThreatDetection();
        sMs.stop();
        super.onDestroy();

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isThreatDetectionOn = true;
        sMs.start();



        new Thread(new Runnable() {
            @Override
            public void run() {
                startThreatDetection();
            }
        }).start();
        return START_STICKY;
    }




    private void startThreatDetection()
    {
        updateLocation = false;
        Log.i("Yes", "Starting Threat Detection");
        counter = 0;
        while(!toExit){

            try {
                if (sMs.getAmplitude() > 40) {

                    updateLocation = true;
                    isDanger = true;
                    counter++;

                }
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            isDanger = false;
        }




    }

    private void stopThreatDetection()
    {
        Log.i("No", "Stopping Threat Detection...");
    }

    public int getNumber()
    {
        return counter;
    }





}
