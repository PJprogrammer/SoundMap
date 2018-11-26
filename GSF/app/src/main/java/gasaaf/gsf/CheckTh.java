package gasaaf.gsf;

import android.view.Display;

public class CheckTh extends Thread {
    int counter;


    public void run(SoundMeter soundMeter){

        try
        {
            if (soundMeter.getAmplitude() > 35) {
                counter++;
                System.out.println("Danger " + counter);
            }


            sleep(1000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }



}
