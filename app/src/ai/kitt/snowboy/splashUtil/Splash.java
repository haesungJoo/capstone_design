package ai.kitt.snowboy.splashUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;

import java.io.File;

import ai.kitt.snowboy.Constants;
import ai.kitt.snowboy.activity.Demo;
import ai.kitt.snowboy.demo.R;

public class Splash extends Activity {

    private final static int SPLASH_DISPLAY_LENGTH = 2000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        File file = new File(Constants.PERSONAL_MODEL_GENERATED);

        // 모델을 이미 생성했다면, Demo쪽으로 바로 넘어가게
        if(file.exists()){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent spIntent = new Intent(Splash.this, Demo.class);
                    startActivity(spIntent);
                    finish();
                }
            }, SPLASH_DISPLAY_LENGTH);
        }
        // 모델 생성 안했다면 모델을 생성하게끔..
        else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
//                    Intent spIntent = new Intent(Splash.this, HotwordSetupActivity.class);
//                    startActivity(spIntent);
//                    finish();
                    Intent spIntent = new Intent(Splash.this, ExplainSplash.class);
                    startActivity(spIntent);
                    finish();
                }
            }, SPLASH_DISPLAY_LENGTH);
        }
    }
}
