package ai.kitt.snowboy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;

import ai.kitt.snowboy.demo.R;

public class Splash extends Activity {

    private final static int SPLASH_DISPLAY_LENGTH = 2000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent spIntent = new Intent(Splash.this, HotwordSetupActivity.class);
                startActivity(spIntent);
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
