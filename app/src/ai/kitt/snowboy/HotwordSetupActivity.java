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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ai.kitt.snowboy.demo.R;
import ai.kitt.snowboy.hotWordSetupUtil.FileExistCheck;
import ai.kitt.snowboy.hotWordSetupUtil.RecordAudioForServer;

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
        fileExistCheck.fileDelete();

        Intent spIntent = new Intent(HotwordSetupActivity.this, Demo.class);
        startActivity(spIntent);
        finish();
    }

    private boolean checkPermissionFromDevice(String[] permissions){

        for(String permission: permissions){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }
}