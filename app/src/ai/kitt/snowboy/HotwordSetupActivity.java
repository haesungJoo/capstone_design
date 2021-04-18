package ai.kitt.snowboy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.checkerframework.checker.units.qual.A;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ai.kitt.snowboy.demo.R;
import ai.kitt.snowboy.hotWordSetupUtil.FileExistCheck;
import ai.kitt.snowboy.hotWordSetupUtil.RecordAudioForServer;
import ai.kitt.snowboy.serverUtil.AudioClient;
import ai.kitt.snowboy.serverUtil.RetrofitService;
import ai.kitt.snowboy.serverUtil.ServerService;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HotwordSetupActivity extends AppCompatActivity {

    String[] PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.SEND_SMS,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.INTERNET
    };
    private final static int REQUEST_PERMISSION_CODE = 1000;
    boolean btn_result_flag = false;

    Button btn_record_first;
    Button btn_record_second;
    Button btn_record_third;
    Button btn_result;
    TextView tv_result;

    private RecordAudioForServer recordAudioForServer = null;
    private FileExistCheck fileExistCheck = null;
    private ServerService serverService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotword_setup);

//        if(checkPermissionFromDevice(PERMISSIONS)){
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                requestPermissions(PERMISSIONS, REQUEST_PERMISSION_CODE);
//            }
//        }else{
//            finish();
//        }

        setUI();
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if(requestCode == REQUEST_PERMISSION_CODE){
//
//        }
//    }

    public void setUI(){
        btn_record_first = findViewById(R.id.btn_record_first);
        btn_record_second = findViewById(R.id.btn_record_second);
        btn_record_third = findViewById(R.id.btn_record_third);
        btn_result = findViewById(R.id.btn_result);
        tv_result = findViewById(R.id.tv_result);

        btn_result.setEnabled(btn_result_flag);
    }

    private void sleep() {
        try { Thread.sleep(500);
        } catch (Exception e) {}
    }

    public void recordAudioThreadInit(String fileName){
        recordAudioForServer = new RecordAudioForServer(fileName);
    }

    public void startRecording(String fileName, Button button_record){
        recordAudioThreadInit(fileName);
        recordAudioForServer.startRecording();
        button_record.setText(R.string.stop_record);
    }

    public void stopRecording(Button button_record){
        recordAudioForServer.stopRecording();
        button_record.setText(R.string.start_record);
    }

    public void first_btn_record(View view){
        if(btn_record_first.getText().toString().equals(getResources().getString(R.string.start_record))){
            sleep();
            startRecording("record1.pcm", btn_record_first);
        }else{
            stopRecording(btn_record_first);
            sleep();

            // 3개의 파일이 존재하는지 확인
            fileExistCheck = new FileExistCheck("record1.wav", "record2.wav", "record3.wav");
            btn_result.setEnabled(fileExistCheck.fileExist());
        }
    }

    public void second_btn_record(View view){
        if(btn_record_second.getText().toString().equals(getResources().getString(R.string.start_record))){
            sleep();
            startRecording("record2.pcm", btn_record_second);
        }else{
            stopRecording(btn_record_second);
            sleep();

            fileExistCheck = new FileExistCheck("record1.wav", "record2.wav", "record3.wav");
            btn_result.setEnabled(fileExistCheck.fileExist());
        }
    }

    public void third_btn_record(View view){
        if(btn_record_third.getText().toString().equals(getResources().getString(R.string.start_record))){
            sleep();
            startRecording("record3.pcm", btn_record_third);
        }else{
            stopRecording(btn_record_third);
            sleep();

            fileExistCheck = new FileExistCheck("record1.wav", "record2.wav", "record3.wav");
            btn_result.setEnabled(fileExistCheck.fileExist());
        }
    }

    public void result_btn_clicked(View view){

        FileExistCheck fileExistCheck = new FileExistCheck("record1.wav", "record2.wav", "record3.wav");

        File file1 = fileExistCheck.filePathConnector("record1.wav");
        File file2 = fileExistCheck.filePathConnector("record2.wav");
        File file3 = fileExistCheck.filePathConnector("record3.wav");

        serverService = new ServerService(HotwordSetupActivity.this, handler);
        serverService.requestUploadMultiple(file1, file2, file3);

//        녹음한 파일 3개 제거
//        fileExistCheck.fileDelete(file1, file2, file3);
    }

    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            MsgEnum message = MsgEnum.getMsgEnum(msg.what);

            switch (message){
                case MSG_MODEL_GENERATED:
//                    Toast.makeText(HotwordSetupActivity.this, "화면 넘김 성공.", Toast.LENGTH_SHORT).show();
                    Intent spIntent = new Intent(HotwordSetupActivity.this, Demo.class);
                    startActivity(spIntent);
                    finish();
                    break;
                default:
                    Toast.makeText(HotwordSetupActivity.this, "화면 넘김을 실패했습니다.", Toast.LENGTH_SHORT).show();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private boolean checkPermissionFromDevice(String[] permissions){

        for(String permission: permissions){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }
}