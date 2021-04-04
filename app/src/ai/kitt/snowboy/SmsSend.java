package ai.kitt.snowboy;

import android.content.Context;
import android.telephony.SmsManager;
import android.widget.Toast;

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

    //Intent sendIntent = new Intent(Intent.ACTION_SEND);
    //sendIntent.putExtra("sms_body", "hi\nSend u Audio File.");
    //sendIntent.setType("audio/*");  // here is for Audio file.
    //sendIntent.setClassName("com.android.mms", "com.android.mms.ui.ComposeMessageActivity");
    //sendIntent.putExtra("address", senderNum);

    //File file1 = new File(recordFile); // file u want to attach.
    //Uri uri = Uri.fromFile(file1);
    //Log.e("Path:---", "" + uri);

    //sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
    //startActivity(sendIntent);

}
