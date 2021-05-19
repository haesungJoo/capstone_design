package ai.kitt.snowboy.util;

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ai.kitt.snowboy.Constants;
import ai.kitt.snowboy.audio.AudioDataReceivedListener;

public class TimerDataSaver implements AudioDataReceivedListener {

    private static final String TAG = TimerDataSaver.class.getSimpleName();

    private File saveFile = null;
    private DataOutputStream dataOutputStream = null;

    private String mFilePath = null;
    private PcmToWavFile pcmToWavFile;

    public TimerDataSaver() {
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MMdd_HH_mm_ss_");

        pcmToWavFile = new PcmToWavFile();
        mFilePath = Constants.SAVE_AUDIO_PROJECT + sdf.format(today) + "project.pcm";

        this.saveFile = new File(mFilePath);
    }

    @Override
    public void start() {
        // TODO notification 진동으로 인한 간섭으로 녹음 타이밍 늦춤
        try
        {
            Thread.sleep(2000);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        if(saveFile != null){
            if(saveFile.exists()){
                saveFile.delete();
            }
            try{
                saveFile.createNewFile();
            }catch (Exception e){
                e.printStackTrace();
            }

            try{
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(this.saveFile));
                dataOutputStream = new DataOutputStream(bufferedOutputStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onAudioDataReceived(byte[] data, int length) {
        try{
            if(dataOutputStream != null){
                dataOutputStream.write(data, 0, length);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {

    }

    public String stop_timer() {

        String pFilePath = null;

        if(dataOutputStream != null){
            try{
                dataOutputStream.close();
            }catch (Exception e){
                e.printStackTrace();
            }

            pFilePath = mFilePath.replace("pcm", "wav");

            File f1 = new File(mFilePath);
            File f2 = new File(pFilePath);

            try{
                pcmToWavFile.rawToWave(f1, f2);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return pFilePath;
    }
}
