package gasaaf.gsf;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class MicServiceTheSequel extends Service{


    boolean isThreatDetectionOn;


    class MicServiceBinder extends Binder {
        public MicServiceTheSequel getService()
        {
            return MicServiceTheSequel.this;
        }
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopThreatDetection();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isThreatDetectionOn = true;




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

    }

    private void stopThreatDetection()
    {

    }






}
