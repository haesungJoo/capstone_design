package ai.kitt.snowboy.jmUtil;

import android.media.SoundPool;

public class SirenSound {

    SoundPool soundPool;
    int soundID;
    int streamID;
    public Boolean is_ing = false;

    public void startSiren(){
        streamID = soundPool.play(soundID,1f,1f,0,-1,1f);
        is_ing=true;
    }

    public void stopSiren(){
        soundPool.stop(streamID);
        is_ing=false;
    }

}
