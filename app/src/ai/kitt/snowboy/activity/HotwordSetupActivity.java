package ai.kitt.snowboy.activity;

import ai.kitt.snowboy.MsgEnum;
import ai.kitt.snowboy.info.CustomDialogClickListener;
import ai.kitt.snowboy.info.InfoCustomDialog;
import ai.kitt.snowboy.util.BackPressedHandler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import ai.kitt.snowboy.demo.R;
import ai.kitt.snowboy.hotWordSetupUtil.FileExistCheck;
import ai.kitt.snowboy.hotWordSetupUtil.RecordAudioForServer;
import ai.kitt.snowboy.serverUtil.ServerService;

public class HotwordSetupActivity extends AppCompatActivity {

    String[] PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.SEND_SMS,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.INTERNET,
            Manifest.permission.READ_PHONE_STATE
    };
    private final static int REQUEST_PERMISSION_CODE = 1000;

    Button btn_record_first;
    Button btn_record_second;
    Button btn_record_third;
    Button btn_result;
    Button btn_splash_info;

    TextView tv_record_first;
    TextView tv_record_second;
    TextView tv_record_third;
    TextView tv_model_generate_result;
    TextView tv_hotword_setup_title;

    ImageView iv_rerecord;

    LinearLayout ll_model_generate_view;
    LinearLayout ll_record_btns_group;

    private RecordAudioForServer recordAudioForServer = null;
    private FileExistCheck fileExistCheck = null;
    private ServerService serverService;

    private BackPressedHandler backPressedHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotword_setup);

        if(checkPermissionFromDevice(PERMISSIONS)){
        }else{
            ActivityCompat.requestPermissions(HotwordSetupActivity.this, PERMISSIONS, REQUEST_PERMISSION_CODE);
        }

        setUI();
        backPressedHandler = new BackPressedHandler(this);
    }

    @Override
    public void onBackPressed()
    {
        backPressedHandler.onBackPressed();
    }

    public void setUI(){
        btn_record_first = findViewById(R.id.btn_record_first);
        btn_record_second = findViewById(R.id.btn_record_second);
        btn_record_third = findViewById(R.id.btn_record_third);
        btn_result = findViewById(R.id.btn_result);
        btn_splash_info = findViewById(R.id.btn_splash_info);

        tv_record_first = findViewById(R.id.tv_record_first);
        tv_record_second = findViewById(R.id.tv_record_second);
        tv_record_third = findViewById(R.id.tv_record_third);
        tv_model_generate_result = findViewById(R.id.tv_model_generate_result);
        tv_hotword_setup_title = findViewById(R.id.tv_hotword_setup_title);

        iv_rerecord = findViewById(R.id.iv_rerecord);

        ll_model_generate_view = findViewById(R.id.ll_model_generate_view);
        ll_record_btns_group = findViewById(R.id.ll_record_btns_group);

//        btn_result.setEnabled(false);
    }

    private void sleep() {
        try { Thread.sleep(500);
        } catch (Exception e) {}
    }

    public void recordAudioThreadInit(String fileName){
        recordAudioForServer = new RecordAudioForServer(fileName);
    }

    public void startRecording(String fileName, Button btn_record, TextView tv_record){
        recordAudioThreadInit(fileName);
        recordAudioForServer.startRecording();
        btn_record.setBackgroundResource(R.drawable.btn_recording_aft);
        tv_record.setText(R.string.stop_record);
    }

    public void stopRecording(Button btn_record, TextView tv_record){
        recordAudioForServer.stopRecording();
        btn_record.setBackgroundResource(R.drawable.btn_recording_bef);
        tv_record.setText(R.string.start_record);
    }

    public void first_btn_record(View view){
        if(tv_record_first.getText().toString().equals(getResources().getString(R.string.start_record))){
            sleep();
            startRecording("record1.pcm", btn_record_first, tv_record_first);

        }else{
            stopRecording(btn_record_first, tv_record_first);
            sleep();

            isFileExistThanAction();
        }
    }

    public void second_btn_record(View view){
        if(tv_record_second.getText().toString().equals(getResources().getString(R.string.start_record))){
            sleep();
            startRecording("record2.pcm", btn_record_second, tv_record_second);
        }else{
            stopRecording(btn_record_second, tv_record_second);
            sleep();

            isFileExistThanAction();
        }
    }

    public void third_btn_record(View view){
        if(tv_record_third.getText().toString().equals(getResources().getString(R.string.start_record))){
            sleep();
            startRecording("record3.pcm", btn_record_third, tv_record_third);
        }else{
            stopRecording(btn_record_third, tv_record_third);
            sleep();

            isFileExistThanAction();
        }
    }

    public void result_btn_clicked(View view){

        tv_model_generate_result.setText(R.string.hotword_generating_button);

        FileExistCheck fileExistCheck = new FileExistCheck("record1.wav", "record2.wav", "record3.wav");

        File file1 = fileExistCheck.filePathConnector("record1.wav");
        File file2 = fileExistCheck.filePathConnector("record2.wav");
        File file3 = fileExistCheck.filePathConnector("record3.wav");

        serverService = new ServerService(HotwordSetupActivity.this, handler);
        serverService.requestUploadMultiple(file1, file2, file3);
    }

    public void iv_rereocrd_clicked(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(HotwordSetupActivity.this);

        builder.setTitle("????????? ?????? ?????????????????????????")
                .setMessage("?????? ????????? ????????????, ???????????? ?????? ??????????????? ?????????.")
                .setPositiveButton("??????", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                        fileExistCheck = new FileExistCheck("record1.wav", "record2.wav", "record3.wav");
                        if(fileExistCheck.fileExist()){

                        }

                        fileExistDelete();
                        ll_model_generate_view.setVisibility(View.INVISIBLE);
                        tv_hotword_setup_title.setText(R.string.hotword_setup_title_bef);
                        ll_record_btns_group.setVisibility(View.VISIBLE);
                    }
                })
                .setNegativeButton("?????????", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                    }
                })
                .show();
    }

    public void btn_splash_info_clicked(View view){
        InfoCustomDialog customDialog = new InfoCustomDialog(HotwordSetupActivity.this, new CustomDialogClickListener() {
            @Override
            public void onPositiveClick() {

            }
        });

//        customDialog.getWindow().setLayout(
//            WindowManager.LayoutParams.MATCH_PARENT,
//            WindowManager.LayoutParams.MATCH_PARENT
//        );

        customDialog.show();
    }


    // ????????? ???????????? ?????? ????????? ?????? ???????????? ????????? ??????
    // 1. 3?????? ????????? ????????? ????????? ?????? ??????
    // 2. ????????? ?????? ????????? -> ???????????? layout INVISIBLE, ???????????? ????????? VISIBLE
    // 3. ????????? ?????? ????????? -> ????????????
    public void isFileExistThanAction(){
        fileExistCheck = new FileExistCheck("record1.wav", "record2.wav", "record3.wav");
        if(fileExistCheck.fileExist()){
            ll_record_btns_group.setVisibility(View.INVISIBLE);
            try{
                Thread.sleep(300);
            }catch (Exception e){
                e.printStackTrace();
            }
            tv_hotword_setup_title.setText(R.string.hotword_setup_title_aft);
            ll_model_generate_view.setVisibility(View.VISIBLE);
        }
    }

    // ????????? ??????????????? ?????????????????? ?????? ????????? ??????
    // 1. ?????? ?????? ?????????
    // 2. ?????? ?????? layout INVISIBLE
    // 3. ?????? ?????? ???????????? layout VISIBLE
    public void isGeneratingModelFailedThanAction(){
        fileExistDelete();
        tv_model_generate_result.setText(R.string.hotword_adapt_button);
        tv_hotword_setup_title.setText(R.string.hotword_setup_title_bef);
        ll_model_generate_view.setVisibility(View.INVISIBLE);
        try{
            Thread.sleep(300);
        }catch (Exception e){
            e.printStackTrace();
        }
        ll_record_btns_group.setVisibility(View.VISIBLE);
    }

    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            MsgEnum message = MsgEnum.getMsgEnum(msg.what);

            switch (message){
                case MSG_MODEL_GENERATED:
//                    Toast.makeText(HotwordSetupActivity.this, "?????? ?????? ??????.", Toast.LENGTH_SHORT).show();

                    tv_model_generate_result.setText(R.string.hotword_adapt_button);
                    fileExistDelete();

                    Intent spIntent = new Intent(HotwordSetupActivity.this, Demo.class);
                    startActivity(spIntent);
                    finish();
                    break;
                case MSG_ERROR_NOFILE:
                    Toast.makeText(getApplicationContext(), "????????? ?????? ???????????? ????????????.\n???????????? ?????? ??????????????????.", Toast.LENGTH_SHORT).show();

                    isGeneratingModelFailedThanAction();
                    break;
                case MSG_ERROR_SHORT_HOTWORD:
                    Toast.makeText(getApplicationContext(), "????????? ????????? ????????? ?????? ????????????.\n???????????? ?????? ??????????????????.", Toast.LENGTH_SHORT).show();

                    isGeneratingModelFailedThanAction();
                    break;
                case MSG_ERROR:
                    Toast.makeText(getApplicationContext(), "?????? ????????? ??????????????????.\n???????????? ?????? ??????????????????.", Toast.LENGTH_SHORT).show();

                    isGeneratingModelFailedThanAction();
                    break;
                default:
                    Toast.makeText(HotwordSetupActivity.this, "?????? ????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void fileExistDelete(){
        File file1 = fileExistCheck.filePathConnector("record1.wav");
        File file2 = fileExistCheck.filePathConnector("record2.wav");
        File file3 = fileExistCheck.filePathConnector("record3.wav");

        // ????????? ?????? 3??? ??????
        fileExistCheck.fileDelete(file1, file2, file3);
    }

    private boolean checkPermissionFromDevice(String[] permissions){

        for(String permission: permissions){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_PERMISSION_CODE:
                for(int grant: grantResults){
                    if(grant != PackageManager.PERMISSION_GRANTED){
                        finish();
                    }
                }
        }
    }
}