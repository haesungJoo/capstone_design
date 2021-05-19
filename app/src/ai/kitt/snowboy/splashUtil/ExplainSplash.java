package ai.kitt.snowboy.splashUtil;

import ai.kitt.snowboy.util.BackPressedHandler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ai.kitt.snowboy.activity.HotwordSetupActivity;
import ai.kitt.snowboy.demo.R;

public class ExplainSplash extends AppCompatActivity {

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

    TextView tv_explain1;

    ImageView iv_three_dots;
    LinearLayout ll_next_btn;

    Boolean three_dots_activating = true;
    Boolean three_dots_flag = true;

    Handler mHandler;

    private BackPressedHandler backPressedHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explain_splash_main);

        if(checkPermissionFromDevice(PERMISSIONS)){
        }else{
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSION_CODE);
        }

        tv_explain1 = findViewById(R.id.tv_explain1);

        iv_three_dots = findViewById(R.id.iv_three_dots);
        ll_next_btn = findViewById(R.id.ll_next_btn);

        ll_next_btn.setOnClickListener(ll_next_btn_handler);

        mHandler = new Handler();

        three_dots_thread.start();
        ll_next_button_thread.start();

        backPressedHandler = new BackPressedHandler(this);
    }

    @Override
    public void onBackPressed()
    {
        backPressedHandler.onBackPressed();
    }

    private View.OnClickListener ll_next_btn_handler = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent modelGenerateIntent = new Intent(ExplainSplash.this, HotwordSetupActivity.class);
            startActivity(modelGenerateIntent);
            finish();
        }
    };

    Thread three_dots_thread = new Thread(new Runnable() {
        @Override
        public void run() {
            while(three_dots_activating){
                if(three_dots_flag){
                    iv_three_dots.setImageResource(R.drawable.three_dots);
                }else{
                    iv_three_dots.setImageResource(R.drawable.three_dots_after);
                }

                three_dots_flag = !three_dots_flag;

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    });

    Thread ll_next_button_thread = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            three_dots_activating = false;

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    iv_three_dots.setVisibility(View.INVISIBLE);
                    tv_explain1.setText(R.string.explain_splash_title_aft);
                    ll_next_btn.setVisibility(View.VISIBLE);
                }
            });
        }
    });

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