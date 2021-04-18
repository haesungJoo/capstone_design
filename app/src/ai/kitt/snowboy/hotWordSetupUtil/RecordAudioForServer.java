package ai.kitt.snowboy.hotWordSetupUtil;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import ai.kitt.snowboy.Constants;
import ai.kitt.snowboy.hotWordSetupUtil.AudioDataSaverForHotwordSetup;

public class RecordAudioForServer {
    private AudioDataSaverForHotwordSetup audioDataSaverForHotwordSetup = null;
    private Thread thread = null;

    private final static int SAMPLE_RATE = Constants.SAMPLE_RATE;
    boolean shouldContinue;

    public RecordAudioForServer(String fileName) {
        this.audioDataSaverForHotwordSetup = new AudioDataSaverForHotwordSetup(fileName);
    }

    public void startRecording(){
        if(thread != null){
            return;
        }

        shouldContinue = true;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                record();
            }
        });
        thread.start();
    }

    public void stopRecording(){
        if(thread == null)
            return;

        shouldContinue = false;
        thread = null;
    }

    private void record(){

        int bufferSize = (int)(SAMPLE_RATE * 0.1 * 2);
        if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
            bufferSize = SAMPLE_RATE * 2;
        }
        byte[] audioBuffer = new byte[bufferSize];

        AudioRecord audioRecord = new AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        );

        audioRecord.startRecording();

        if(audioDataSaverForHotwordSetup != null){
            audioDataSaverForHotwordSetup.start();
        }

        while(shouldContinue){
            audioRecord.read(audioBuffer,0,bufferSize);
            audioDataSaverForHotwordSetup.onAudioDataReceived(audioBuffer, bufferSize);
        }

        audioDataSaverForHotwordSetup.stop();
    }
}
