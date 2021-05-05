package ai.kitt.snowboy.info;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;

import ai.kitt.snowboy.demo.R;

public class InfoCustomDialog extends Dialog {

    private Context ctx;
    private CustomDialogClickListener dialogClickListener;
    private Button btn_custom_dialog_dismiss;

    public InfoCustomDialog(@NonNull Context ctx, CustomDialogClickListener dialogClickListener) {
        super(ctx);
        this.ctx = ctx;
        this.dialogClickListener = dialogClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_model_generate);

        btn_custom_dialog_dismiss = findViewById(R.id.btn_custom_dialog_dismiss);
        btn_custom_dialog_dismiss.setOnClickListener(v->{
            this.dialogClickListener.onPositiveClick();
            dismiss();
        });
    }
}
