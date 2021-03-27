package ai.kitt.snowboy.util;

import android.os.Handler;
import android.os.Message;

import ai.kitt.snowboy.MsgEnum;

public class TimerThread {

    Handler handler = null;

    public TimerThread(Handler handler) {
        this.handler = handler;
    }

    public void timer(){
        Thread timer = new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0;i<2;i++){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if(handler != null){
                    Message msg = handler.obtainMessage(MsgEnum.MSG_STOP.ordinal(), null);
                    handler.sendMessage(msg);
                }
            }
        });
        timer.start();
    }
}
