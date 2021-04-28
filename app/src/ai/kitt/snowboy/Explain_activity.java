package ai.kitt.snowboy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import ai.kitt.snowboy.demo.R;
import ai.kitt.snowboy.jmUtil.Explain1;
import ai.kitt.snowboy.jmUtil.Explain2;
import ai.kitt.snowboy.jmUtil.Explain3;

public class Explain_activity extends AppCompatActivity {

    private Button jadong;
    private Button sudong;
    private Button record;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explain_activity);

        Intent intent = getIntent();

        jadong=(Button)findViewById(R.id.btn_jadong);
        sudong=(Button)findViewById(R.id.btn_sudong);
        record=(Button)findViewById(R.id.btn_record);

        jadong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                Fragment explain1 = new Explain1();
                transaction.replace(R.id.lo_frame,explain1);
                transaction.commit();

            }
        });

        sudong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                Fragment explain2 = new Explain2();
                transaction.replace(R.id.lo_frame,explain2);
                transaction.commit();
            }
        });

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                Fragment explain3 = new Explain3();
                transaction.replace(R.id.lo_frame,explain3);
                transaction.commit();
            }
        });
    }
}