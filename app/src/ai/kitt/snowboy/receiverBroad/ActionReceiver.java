package ai.kitt.snowboy.receiverBroad;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.service.notification.StatusBarNotification;

import ai.kitt.snowboy.Constants;
import ai.kitt.snowboy.jmUtil.AlertTime;

public class ActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

//        String action = intent.getAction();
//
//        if(Constants.YES_ACTION.equals(action)){
//            String filePath = intent.getStringExtra("filePath");
//
//            AlertTime alertTime = new AlertTime(context);
//            alertTime.mms_report(filePath);
//        }else if(Constants.NO_ACTION.equals(action)){
//            // nothing
//        }

        String check = intent.getStringExtra("check");
        String test = "test";

        if(check.equals("yes")){
            String filePath = intent.getStringExtra("filePath");

            AlertTime alertTime = new AlertTime(context);
            alertTime.mms_report(filePath);

            turnOffNotificationRemain(context);
        }else if(check.equals("no")){
            // nothing
            turnOffNotificationRemain(context);
        }
    }

    public void turnOffNotificationRemain(Context ctx){
        NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        StatusBarNotification[] statusBarNotifications = notificationManager.getActiveNotifications();

        for(StatusBarNotification statusBarNotification: statusBarNotifications){
            if(statusBarNotification.getId() == 1235){
                notificationManager.cancel(1235);
            }
        }
    }
}
