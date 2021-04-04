package ai.kitt.snowboy;

import ai.kitt.snowboy.audio.RecordingThread;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

<<<<<<< HEAD
import java.io.IOException;
=======
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
>>>>>>> origin/jongmin

import ai.kitt.snowboy.audio.AudioDataSaver;
import ai.kitt.snowboy.demo.R;

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

    SoundPool soundPool;
    int soundID;
    int streamID;
    Boolean is_ing = false;

    private SmsSend smsSend;
    private GpsTracker gpsTracker;

    private static long activeTimes = 0;

    private RecordingThread recordingThread;

    private TimerThread timerThread;
    private Classifier mClassifier; // commit message를 위한 변경

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        setUI(); // button textview 등 기본 구성

        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC,0);
        soundID = soundPool.load(Demo.this, R.raw.siren,1);

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

    // 수동 신고
    public void report(){
        gpsTracker = new GpsTracker(Demo.this);
        double latitude = gpsTracker.getLatitude();
        double longitude =gpsTracker.getLongitude();
        String loc = getCurrentAddress(latitude,longitude);

        //문자 전송
        smsSend = new SmsSend(Demo.this);
        String msg = "살려주세요! 제 위치는 "+loc+" 입니다.";
        String phnum = "01026670860";
        smsSend.sendMsg(msg,phnum);
    }

    private OnClickListener selfsue_button_handle = new OnClickListener() {
        @Override
        public void onClick(View v) {

            //경고창
            AlertDialog.Builder ad = new AlertDialog.Builder(Demo.this);
            ad.setIcon(R.mipmap.ic_launcher);
            ad.setTitle("긴급 신고");//제목
            ad.setMessage("5초 뒤 신고메시지가 전송됩니다.");//내용

            //final EditText et = new EditText(Demo.this);
            //ad.setView(et);

            ad.setPositiveButton("바로 전송", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    report();
                    dialog.dismiss();
                }
            });

            ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            final AlertDialog alertDialog = ad.create();
            alertDialog.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(alertDialog.isShowing()){
                        report();
                        alertDialog.dismiss();
                    }
                }
            }, 5000);



        }
    };
    
    private void startSiren(){
        streamID = soundPool.play(soundID,1f,1f,0,-1,1f);
        siren_button.setText("사이렌 끄기");
        is_ing=true;
    }

    private void stopSiren(){
        soundPool.stop(streamID);
        siren_button.setText("사이렌 울리기");
        is_ing=false;
    }

    private OnClickListener siren_button_handle = new OnClickListener() {
        // @Override
        public void onClick(View arg0) {
            if(is_ing==false){
                startSiren();
                Log.d("is_ing : ",is_ing.toString());
            }
            else if(is_ing==true){
                stopSiren();
                Log.d("is_ing : ",is_ing.toString());
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

    public String getCurrentAddress( double latitude, double longitude) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }



        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        }

        Address address = addresses.get(0);
        return address.getAddressLine(0).toString()+"\n";

    }


    @Override
     public void onDestroy() {
         recordingThread.stopRecording();
         super.onDestroy();
     }
}
