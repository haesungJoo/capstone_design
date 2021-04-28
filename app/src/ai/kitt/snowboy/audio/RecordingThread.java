package ai.kitt.snowboy.audio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ai.kitt.snowboy.Constants;
import ai.kitt.snowboy.MsgEnum;

import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.LinearLayout;

import ai.kitt.snowboy.SnowboyDetect;
import ai.kitt.snowboy.util.TimerDataSaver;
import ai.kitt.snowboy.util.TimerThread;

public class RecordingThread {
    static {
        try{
            System.loadLibrary("snowboy-detect-android");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static final String TAG = RecordingThread.class.getSimpleName();
//    private static final String TAG = "RecordingThread";
    private static final String ACTIVE_RES = Constants.ACTIVE_RES;
    // TODO 테스트 하기 위한 모델
    private static final String ACTIVE_MDL = Constants.TEST_PMDL;
    private static final String GENERATED_UMDL = Constants.PERSONAL_MODEL_GENERATED;
//    private static final String ACTIVE_UMDL = Constants.ACTIVE_UMDL;

    private TimerThread timerThread = null;

    private boolean shouldContinue;
    private boolean isWordDetected = false; // TimerThread에 의해서 2초동안의 파일 저장을 위한 flag
    private AudioDataReceivedListener listener = null;
    private TimerDataSaver timer_listener = null;
    private Handler handler = null;
    private Thread thread;
    
    private static String strEnvWorkSpace = Constants.DEFAULT_WORK_SPACE;
    //private String activeModel = GENERATED_UMDL;
    private String activeModel = strEnvWorkSpace+ACTIVE_MDL;
    private String commonRes = strEnvWorkSpace+ACTIVE_RES;
    
    private SnowboyDetect detector = new SnowboyDetect(commonRes, activeModel);
    private MediaPlayer player = new MediaPlayer();

    public RecordingThread(Handler handler, AudioDataReceivedListener listener) {
        this.handler = handler;
        this.listener = listener;

//        detector.SetSensitivity("0.51");
        detector.SetSensitivity("0.6");
        detector.SetAudioGain(1f);
        detector.ApplyFrontend(true);
        try {
            player.setDataSource(strEnvWorkSpace+"ding.wav");
            player.prepare();
        } catch (IOException e) {
            Log.e(TAG, "Playing ding sound error", e);
        }

        timerThread = new TimerThread(timer_handler);
        timer_listener = new TimerDataSaver();
    }

    private void sendMessage(MsgEnum what, Object obj){
        if (null != handler) {
            Message msg = handler.obtainMessage(what.ordinal(), obj);
            handler.sendMessage(msg);
        }
    }

    public void startRecording() {
        if (thread != null)
            return;

        shouldContinue = true;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                record();
            }
        });
        thread.start();
    }

    public void stopRecording() {
        if (thread == null)
            return;

        shouldContinue = false;
        thread = null;
    }

    public void timerRecording(){
        shouldContinue = false;
    }

    private void record() {
        Log.v(TAG, "Start");
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);

        // Buffer size in bytes: for 0.1 second of audio
        int bufferSize = (int)(Constants.SAMPLE_RATE * 0.1 * 2);
        if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
            bufferSize = Constants.SAMPLE_RATE * 2;
        }

        byte[] audioBuffer = new byte[bufferSize];
        AudioRecord record = new AudioRecord(
            MediaRecorder.AudioSource.MIC,
            Constants.SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize);

//        if (record.getState() != AudioRecord.STATE_INITIALIZED) {
//            Log.e(TAG, "Audio Record can't initialize!");
//            return;
//        }

        record.startRecording();
        if (null != listener) {
            listener.start();
        }
        Log.v(TAG, "Start recording");

        long shortsRead = 0;
        detector.Reset();
        while (shouldContinue) {
            record.read(audioBuffer, 0, audioBuffer.length);

            if (null != listener) {
                listener.onAudioDataReceived(audioBuffer, audioBuffer.length);
            }

            if(isWordDetected){
                timer_listener.onAudioDataReceived(audioBuffer, audioBuffer.length);
            }

            // Converts to short array.
            short[] audioData = new short[audioBuffer.length / 2];
            ByteBuffer.wrap(audioBuffer).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(audioData);

            shortsRead += audioData.length;

            // Snowboy hotword detection.
            int result = detector.RunDetection(audioData, audioData.length);

            if (result == -2) {
                // post a higher CPU usage:
                // sendMessage(MsgEnum.MSG_VAD_NOSPEECH, null);
            } else if (result == -1) {
                sendMessage(MsgEnum.MSG_ERROR, "Unknown Detection Error");
            } else if (result == 0) {
                // post a higher CPU usage:
                // sendMessage(MsgEnum.MSG_VAD_SPEECH, null);
            } else if (result > 0) {
                sendMessage(MsgEnum.MSG_ACTIVE, null);

                // ding 소리 나는 부분
                player.start();

                timerThread.timer();
                timer_listener.start();
                isWordDetected = true; // 타이머의 시작과 동시에 녹음이 시작하게 한다.

                Log.i("Snowboy: ", "Hotword " + Integer.toString(result) + " detected!");
            }
        }

        record.stop();
        record.release();

        if (null != listener) {
            listener.stop();
        }
        Log.v(TAG, String.format("Recording stopped. Samples read: %d", shortsRead));
    }

    public Handler timer_handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            MsgEnum message = MsgEnum.getMsgEnum(msg.what);
            Message msg_timer = null;

            switch (message){
                case MSG_STOP:
                    msg_timer = handler.obtainMessage(MsgEnum.MSG_STOP.ordinal(), timer_listener.stop_timer());
                    isWordDetected = false; // 3초 뒤에 녹음이 멈추게끔 한다.
                    break;
                default:
                    msg_timer = handler.obtainMessage(MsgEnum.MSG_TIMER_ERROR.ordinal(), null);
                    break;
            }
            handler.sendMessage(msg_timer);

            super.handleMessage(msg);
        }
    };
}
