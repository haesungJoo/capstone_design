package ai.kitt.snowboy.explainSplash;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import ai.kitt.snowboy.HotwordSetupActivity;
import ai.kitt.snowboy.demo.R;

public class ExplainSplash extends AppCompatActivity {

    TextView tv_explain1;

    ImageView iv_three_dots;
    LinearLayout ll_next_btn;

    Boolean three_dots_activating = true;
    Boolean three_dots_flag = true;

    Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explain_splash_main);

        tv_explain1 = findViewById(R.id.tv_explain1);

        iv_three_dots = findViewById(R.id.iv_three_dots);
        ll_next_btn = findViewById(R.id.ll_next_btn);

        ll_next_btn.setOnClickListener(ll_next_btn_handler);

        mHandler = new Handler();

        three_dots_thread.start();
        ll_next_button_thread.start();
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
}