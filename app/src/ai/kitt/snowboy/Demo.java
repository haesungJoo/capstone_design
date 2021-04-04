package ai.kitt.snowboy;

import ai.kitt.snowboy.audio.RecordingThread;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;


import java.io.IOException;

import java.util.List;
import java.util.Locale;

import ai.kitt.snowboy.audio.AudioDataSaver;
import ai.kitt.snowboy.demo.R;

import ai.kitt.snowboy.jmUtil.AlertTime;
import ai.kitt.snowboy.jmUtil.GpsTracker;
import ai.kitt.snowboy.jmUtil.SirenSound;
import ai.kitt.snowboy.jmUtil.SmsSend;
import ai.kitt.snowboy.modelUtil.Classifier;
import ai.kitt.snowboy.modelUtil.FileFormatNotSupportedException;
import ai.kitt.snowboy.modelUtil.JLibrosa;
import ai.kitt.snowboy.modelUtil.Result;
import ai.kitt.snowboy.modelUtil.WavFileException;
import ai.kitt.snowboy.util.TimerThread;

public class Demo extends Activity {

    private Button record_button;
    private Button play_button;
    private Button siren_button;

    private AlertTime alertTime;
    private SirenSound sirenSound;

    private static long activeTimes = 0;

    private RecordingThread recordingThread;

    private TimerThread timerThread;
    private Classifier mClassifier; // commit message를 위한 변경

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        setUI(); // button textview 등 기본 구성

        alertTime = new AlertTime(this);
        sirenSound = new SirenSound(this);

//        setProperVolume(); //

        AppResCopy.copyResFromAssetsToSD(this);
        
        activeTimes = 0;
        // 여기부터
        recordingThread = new RecordingThread(handle, new AudioDataSaver());
        timerThread = new TimerThread(handle);
    }
    
    void showToast(CharSequence msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    // View? ???? ??
    private void setUI() {
        record_button = (Button) findViewById(R.id.btn_start);
        record_button.setOnClickListener(record_button_handle);
        record_button.setEnabled(true);

        play_button = (Button) findViewById(R.id.btn_sue);
        play_button.setOnClickListener(selfsue_button_handle);
        play_button.setEnabled(true);

        siren_button = (Button) findViewById(R.id.btn_ciren);
        siren_button.setOnClickListener(siren_button_handle);
        siren_button.setEnabled(true);
    }
    
    private void startRecording() {
        recordingThread.startRecording();
        record_button.setText(R.string.btn_actionstop);
    }

    private void stopRecording() {
        recordingThread.stopRecording();
        record_button.setText(R.string.btn_actionstart);
    }

    private void sleep() {
        try { Thread.sleep(500);
        } catch (Exception e) {}
    }

    
    private OnClickListener record_button_handle = new OnClickListener() {
        // @Override
        public void onClick(View arg0) {
            if(record_button.getText().equals(getResources().getString(R.string.btn_actionstart))) {
                sleep();
                startRecording();
            } else {
                stopRecording();
                sleep();
            }
        }
    };

    private OnClickListener selfsue_button_handle = new OnClickListener() {
        @Override
        public void onClick(View v) {
            alertTime.showDialog();
        }
    };

    private OnClickListener siren_button_handle = new OnClickListener() {
        // @Override
        public void onClick(View arg0) {
            if(sirenSound.is_ing==false){
                sirenSound.startSiren();
                siren_button.setText("사이렌 끄기");
            }
            else if(sirenSound.is_ing==true){
                sirenSound.stopSiren();
                siren_button.setText("사이렌 울리기");
            }
        }
    };

    public Handler handle = new Handler() {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {
            if(msg.obj != null){
                // TODO 준호형 (.wav 파일 경로를 반환하는 곳입니다.)
                String filePath = msg.obj.toString();

                JLibrosa jLibrosa = new JLibrosa();
                try {
                    float [] audioFeatureValues = jLibrosa.loadAndRead(filePath, -1,-1);
                    mClassifier = new Classifier(audioFeatureValues, Demo.this);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (FileFormatNotSupportedException e) {
                    e.printStackTrace();
                } catch (WavFileException e) {
                    e.printStackTrace();
                }

                Result result = mClassifier.classify();
                String emotion;
                int n_idx = result.getNumber();

                switch(n_idx){
                    case 0:
                        emotion = "화남";
                        break;
                    case 1:
                        emotion = "중립";
                        break;
                    case 2:
                        emotion = "행복";
                        break;
                    case 3:
                        emotion = "두려움";
                        //jm control
                        break;
                    default:
                        emotion = "오류";
                        break;
                }
            }
            MsgEnum message = MsgEnum.getMsgEnum(msg.what);
            switch(message) {
                case MSG_ACTIVE:
                    activeTimes++;
                     Toast.makeText(Demo.this, "Active "+activeTimes, Toast.LENGTH_SHORT).show();
                    break;
                case MSG_INFO:
                    Toast.makeText(Demo.this, "MSG_INFO", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_VAD_SPEECH:
                    Toast.makeText(Demo.this, "MSG_VAD_SPEECH", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_VAD_NOSPEECH:
                    Toast.makeText(Demo.this, "MSG_VAD_NOSPEECH", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_ERROR:
                    Toast.makeText(Demo.this, "MSG_ERROR", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_STOP:
                    Toast.makeText(Demo.this, "MSG_STOP", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_TIMER_ERROR:
                    Toast.makeText(Demo.this, "MSG_TIMER_ERROR", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    super.handleMessage(msg);
                    break;
             }
        }
    };

    @Override
     public void onDestroy() {
         recordingThread.stopRecording();
         super.onDestroy();
     }
}
