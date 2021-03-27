package ai.kitt.snowboy;

import ai.kitt.snowboy.audio.RecordingThread;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import ai.kitt.snowboy.audio.AudioDataSaver;
import ai.kitt.snowboy.demo.R;

import ai.kitt.snowboy.util.TimerThread;

public class Demo extends Activity {

    private Button record_button;
    private Button play_button;

    private static long activeTimes = 0;

    private RecordingThread recordingThread;

    private TimerThread timerThread;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        setUI(); // button textview 등 기본 구성
        
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

    // TODO 수동으로 신고하는 버튼 기능 추가
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

        }
    };
    
    public Handler handle = new Handler() {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {
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
