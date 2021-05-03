package ai.kitt.snowboy;

import ai.kitt.snowboy.audio.RecordingThread;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.RequiresApi;

import java.io.File;
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
    private Button btn_model_regenerate;

    private TextView tv_model_regenerate;
    private TextView tv_siren;
    private TextView tv_sue;
    private TextView tv_start;

    private AlertTime alertTime;
    private SirenSound sirenSound;
    private int preVolume = -1;

    private static long activeTimes = 0;

    private RecordingThread recordingThread;

    private TimerThread timerThread;
    private Classifier mClassifier; // commit message를 위한 변경

    private Vibrator vibrator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        setUI(); // button textview 등 기본 구성

        alertTime = new AlertTime(this);
        sirenSound = new SirenSound(this);

        AppResCopy.copyResFromAssetsToSD(this);
        
        activeTimes = 0;
        // 여기부터
        recordingThread = new RecordingThread(handle, new AudioDataSaver());
        timerThread = new TimerThread(handle);

        vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
    }
    
    void showToast(CharSequence msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void setUI() {
        record_button = (Button) findViewById(R.id.btn_start);
        record_button.setOnClickListener(record_button_handle);
        record_button.setEnabled(true);

        play_button = (Button) findViewById(R.id.btn_sue);
        play_button.setOnClickListener(selfsue_button_handle);
        play_button.setEnabled(true);

        siren_button = (Button) findViewById(R.id.btn_siren);
        siren_button.setOnClickListener(siren_button_handle);
        siren_button.setEnabled(true);

        tv_model_regenerate = findViewById(R.id.tv_model_regenerate);
        tv_siren = findViewById(R.id.tv_siren);
        tv_sue = findViewById(R.id.tv_sue);
        tv_start = findViewById(R.id.tv_start);

        btn_model_regenerate = (Button) findViewById(R.id.btn_model_regenerate);
        btn_model_regenerate.setOnClickListener(btn_model_regenerate_handle);
    }
    
    private void startRecording() {
        recordingThread.startRecording();
        tv_start.setText(R.string.btn_actionstop);
    }

    private void stopRecording() {
        recordingThread.stopRecording();
        tv_start.setText(R.string.btn_actionstart);
    }

    private void sleep() {
        try { Thread.sleep(500);
        } catch (Exception e) {}
    }

    
    private OnClickListener record_button_handle = new OnClickListener() {
        // @Override
        public void onClick(View arg0) {
            if(tv_start.getText().equals(getResources().getString(R.string.btn_actionstart))) {
                sleep();
                startRecording();
                record_button.setBackgroundResource(R.drawable.btn_detecting_function_background_aft);
            } else {
                stopRecording();
                sleep();
                record_button.setBackgroundResource(R.drawable.btn_detecting_function_background_bef);
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
        @Override
        public void onClick(View arg0) {
            if(sirenSound.is_ing==false){
                setProperVolume();
                sirenSound.startSiren();
                tv_siren.setText(R.string.siren_off);
                siren_button.setBackgroundResource(R.drawable.btn_siren_background_aft);
            }
            else if(sirenSound.is_ing==true){
                restoreVolume();
                sirenSound.stopSiren();
                tv_siren.setText(R.string.siren_on);
                siren_button.setBackgroundResource(R.drawable.btn_siren_background_bef);
            }
        }
    };

    private OnClickListener btn_model_regenerate_handle = new OnClickListener() {
        @Override
        public void onClick(View view) {

            AlertDialog.Builder builder = new AlertDialog.Builder(Demo.this);

            builder.setTitle("모델을 다시 생성하시겠습니까?")
                    .setMessage("다시 생성하실 경우, 기존 모델이 삭제됩니다.\n그래도 다시 생성하시겠습니까?")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            File file = new File(Constants.PERSONAL_MODEL_GENERATED);
                            if(file.exists()){
                                file.delete();
                            }

                            Intent intent = new Intent(Demo.this, HotwordSetupActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .create()
                    .show();
        }
    };

    private void setProperVolume() {
        AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        preVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int properVolume = (int) ((float) maxVolume);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, properVolume, 0);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    private void restoreVolume() {
        if(preVolume>=0) {
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, preVolume, 0);
            int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        }
    }

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
                        alertTime.sendMms_alert(filePath);
                        emotion = "두려움";
                        break;
                    default:
                        emotion = "오류";
                        break;
                }

                Toast.makeText(getApplicationContext(), "현재 감정 : "+emotion, Toast.LENGTH_SHORT).show();
            }
            MsgEnum message = MsgEnum.getMsgEnum(msg.what);
            switch(message) {
                case MSG_ACTIVE:
//                    activeTimes++;
//                     Toast.makeText(Demo.this, "Active "+activeTimes, Toast.LENGTH_SHORT).show();
                        vibrator.vibrate(500);
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
//                    Toast.makeText(Demo.this, "MSG_STOP", Toast.LENGTH_SHORT).show();
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
