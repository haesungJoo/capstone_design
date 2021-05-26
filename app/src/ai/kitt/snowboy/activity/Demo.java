package ai.kitt.snowboy.activity;

import ai.kitt.snowboy.AppResCopy;
import ai.kitt.snowboy.Constants;
import ai.kitt.snowboy.MsgEnum;
import ai.kitt.snowboy.audio.RecordingThread;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import ai.kitt.snowboy.info.MainInfoCustomDialog;
import ai.kitt.snowboy.receiverBroad.ActionReceiver;
import ai.kitt.snowboy.util.BackPressedHandler;

import androidx.core.app.NotificationCompat;

import java.io.File;
import java.io.IOException;

import ai.kitt.snowboy.audio.AudioDataSaver;
import ai.kitt.snowboy.demo.R;

import ai.kitt.snowboy.jmUtil.AlertTime;
import ai.kitt.snowboy.jmUtil.SirenSound;
import ai.kitt.snowboy.modelUtil.Classifier;
import ai.kitt.snowboy.modelUtil.FileFormatNotSupportedException;
import ai.kitt.snowboy.modelUtil.JLibrosa;
import ai.kitt.snowboy.modelUtil.Result;
import ai.kitt.snowboy.modelUtil.WavFileException;
import ai.kitt.snowboy.util.TimerThread;

public class Demo extends Activity {

    private static final String NOTIFICATION_CHANNEL_ID = "1001";
    private CharSequence channelName = "구해줘";
    private String description = channelName+"  설명";
    private int importance = NotificationManager.IMPORTANCE_HIGH;

    private Button record_button;
    private Button btn_self_sue;
    private Button siren_button;
    private Button btn_model_regenerate;
    private Button btn_main_info;

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

    private BackPressedHandler backPressedHandler;

    private Boolean onPauseFlag = true;
    private Boolean onPuaseVibrateFlag = false;

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

        backPressedHandler = new BackPressedHandler(this);


    }

    @Override
    public void onBackPressed()
    {
        backPressedHandler.onBackPressed();
    }
    
    private void setUI() {
        record_button = (Button) findViewById(R.id.btn_start);
        record_button.setOnClickListener(record_button_handle);
        record_button.setEnabled(true);

        btn_self_sue = (Button) findViewById(R.id.btn_sue);
        btn_self_sue.setOnClickListener(selfsue_button_handle);
        btn_self_sue.setEnabled(true);

        siren_button = (Button) findViewById(R.id.btn_siren);
        siren_button.setOnClickListener(siren_button_handle);
        siren_button.setEnabled(true);

        tv_model_regenerate = findViewById(R.id.tv_model_regenerate);
        tv_siren = findViewById(R.id.tv_siren);
        tv_sue = findViewById(R.id.tv_sue);
        tv_start = findViewById(R.id.tv_start);

        btn_model_regenerate = (Button) findViewById(R.id.btn_model_regenerate);
        btn_model_regenerate.setOnClickListener(btn_model_regenerate_handle);

        btn_main_info = (Button) findViewById(R.id.btn_main_info);
        btn_main_info.setOnClickListener(btn_main_info_clicked);
        btn_main_info.setEnabled(true);
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


    private OnClickListener btn_main_info_clicked = new OnClickListener() {
        // @Override
        public void onClick(View arg0) {
            Intent intent = new Intent(Demo.this, MainInfoCustomDialog.class);
            startActivity(intent);
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
                    if(audioFeatureValues.length == 0){
                        return;
                    }
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
//                        alertTime.sendMms_alert(filePath);
                        emotion = "중립";
                        break;
                    case 2:
                        emotion = "행복";
                        break;
                    case 3:
//                        alertTime.sendMms_alert(filePath);
                        notificationAlertEmotion("감정이 두려움이라고 판단되었습니다.\n신고하시겠습니까?\n(아무 동작도 안하신다면 10초뒤에 자동신고됩니다.)",
                                filePath);
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
//                  activeTimes++;
//                  Toast.makeText(Demo.this, "Active "+activeTimes, Toast.LENGTH_SHORT).show();
//                    vibrator.vibrate(500);
//                    onPuaseVibrateFlag = true;

                    notificationAlert("키워드를 호출했습니다.");

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

    public void notificationAlert(String contentText){
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(getApplicationContext(), Demo.class);
        intent.putExtra("notificationAlertEmotion", "");

        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_background)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(contentText)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, importance);
            notificationChannel.setDescription(description);

            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);
        }

        notificationManager.notify(1234, builder.build());
    }

    public void notificationAlertEmotion(String contentText, String filePath){
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intentToDemo = new Intent(this, Demo.class);
        intentToDemo.putExtra("notificationAlertEmotion", "scare");
        intentToDemo.putExtra("filePath", filePath);

        Intent intentToBroadCastYes = new Intent(this, ActionReceiver.class);
        intentToBroadCastYes.putExtra("check", "yes");
        intentToBroadCastYes.putExtra("filePath", filePath);

        Intent intentToBroadCastNo = new Intent(this, ActionReceiver.class);
        intentToBroadCastNo.putExtra("check", "no");

        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                1,
                intentToDemo,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        PendingIntent pendingIntentBroadCastYes = PendingIntent.getBroadcast(
                getApplicationContext(),
                2,
                intentToBroadCastYes,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        PendingIntent pendingIntentBroadCastNo = PendingIntent.getBroadcast(
                getApplicationContext(),
                3,
                intentToBroadCastNo,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_background)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(contentText)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(contentText))
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.logo_background, "확인", pendingIntentBroadCastYes)
                .addAction(R.drawable.logo_background, "취소", pendingIntentBroadCastNo)
                .setAutoCancel(true);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, importance);
            notificationChannel.setDescription(description);

            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);
        }

        notificationManager.cancel(1234);
        notificationManager.notify(1235, builder.build());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                StatusBarNotification[] statusBarNotifications = notificationManager.getActiveNotifications();

                for(StatusBarNotification statusBarNotification: statusBarNotifications){
                    if(statusBarNotification.getId() == 1235){
                        sendBroadcast(intentToBroadCastYes);
                    }
                }
            }
        }, 10000);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String emotion = intent.getStringExtra("notificationAlertEmotion");
        if(emotion != null && emotion.equals("scare")){
            String filePath = intent.getStringExtra("filePath");

            alertTime.sendMms_alert(filePath, "확인버튼을 누르시면\n신고메시지가 전송됩니다.");
        }
    }

    @Override
    protected void onPause() {

        // 한번 onPause 되면 계속 onPause 됨
        // onPauseFlag doesn't change
//        while(onPauseFlag){
//            if(onPuaseVibrateFlag){
//                vibrator.vibrate(500);
//                onPuaseVibrateFlag = false;
//            }
//        }
        super.onPause();
    }

    @Override
     public void onDestroy() {
//        onPauseFlag = false;
        recordingThread.stopRecording();
        super.onDestroy();
     }
}
