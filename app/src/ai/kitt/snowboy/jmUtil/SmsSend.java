package ai.kitt.snowboy.jmUtil;

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
}
