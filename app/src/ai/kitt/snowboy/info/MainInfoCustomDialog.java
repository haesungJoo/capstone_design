package ai.kitt.snowboy.info;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import ai.kitt.snowboy.demo.R;

public class MainInfoCustomDialog extends AppCompatActivity {

    private Button jadong;
    private Button sudong;
    private Button record;
    private Button dismiss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_main);

        Intent intent = getIntent();

        jadong=(Button)findViewById(R.id.btn_jadong);
        sudong=(Button)findViewById(R.id.btn_sudong);
        record=(Button)findViewById(R.id.btn_record);
        dismiss=(Button)findViewById(R.id.btn_dismiss);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment explain1 = new Explain1();
        transaction.replace(R.id.lo_frame,explain1);
        transaction.commit();

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

        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
