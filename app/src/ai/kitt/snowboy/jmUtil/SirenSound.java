package ai.kitt.snowboy.jmUtil;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import androidx.annotation.RequiresApi;

import ai.kitt.snowboy.demo.R;

public class SirenSound {

    private final Context mContext;
    SoundPool soundPool;
    int soundID;
    int streamID;
    public Boolean is_ing = false;

    public SirenSound(Context context) {
        this.mContext = context;
        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        soundID = soundPool.load(mContext, R.raw.siren, 1);
    }

    public void startSiren(){
        streamID = soundPool.play(soundID,1f,1f,0,-1,1f);
        is_ing=true;
    }

    public void stopSiren(){
        soundPool.stop(streamID);
        is_ing=false;
    }

}
