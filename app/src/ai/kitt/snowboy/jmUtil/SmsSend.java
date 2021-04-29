package ai.kitt.snowboy.jmUtil;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.klinker.android.send_message.Message;
import com.klinker.android.send_message.Settings;
import com.klinker.android.send_message.Transaction;

import org.tensorflow.lite.support.common.FileUtil;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;

import static android.content.ContentValues.TAG;

public class SmsSend {
    private final Context mContext;

    public SmsSend(Context context){
        this.mContext = context;
    }

    public void sendMsg(String msg, String phoneNo){
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo,null,msg,null,null);
            Toast.makeText(mContext, "전송 완료!", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(mContext, e.toString(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public void sendMMS(String phone, String filepath, String msg) {

        Log.d(TAG, "sendMMS(Method) : " + "start");

        String subject = "도움이 필요합니다.";
        String text = msg;

        String audioPath = filepath;

        Log.d(TAG, "subject : " + subject);
        Log.d(TAG, "text : " + text);
        Log.d(TAG, "audioPath : " + audioPath);

        Settings settings = new Settings();
        settings.setUseSystemSending(true);

        Transaction transaction = new Transaction(mContext, settings);

        // 제목이 있을경우
        Message message = new Message(text, phone, subject);

        // 제목이 없을경우
        // Message message = new Message(text, number);

        if (!(audioPath == null)) {
            try {
                File afile = new File(filepath);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                BufferedInputStream in = new BufferedInputStream(new FileInputStream(afile));

                int read;
                byte[] buff = new byte[1024];
                while ((read = in.read(buff)) > 0)
                {
                    out.write(buff, 0, read);
                }
                out.flush();
                byte[] audioBytes = out.toByteArray();
                message.addAudio(audioBytes);

            }catch (IOException e) {
                e.printStackTrace();
            }
        }

        long id = android.os.Process.getThreadPriority(android.os.Process.myTid());

        transaction.sendNewMessage(message, id);
    }
    /*public void sendMms(String filepath, String msg, String phoneNo){

        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.putExtra("sms_body", msg);
        sendIntent.setType("audio/*");  // here is for Audio file.
        sendIntent.setClassName("com.android.mms", "com.android.mms.ui.ComposeMessageActivity");
        sendIntent.putExtra("address", phoneNo);

        File file1 = new File(filepath); // file u want to attach.
        Uri uri = FileProvider.getUriForFile(mContext,mContext.getApplicationContext().getPackageName()+".provider",file1);
        Log.e("Path:---", "" + uri.getPath());

        sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
        mContext.startActivity(sendIntent);
    }*/
}
