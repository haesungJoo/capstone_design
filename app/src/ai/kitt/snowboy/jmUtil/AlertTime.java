package ai.kitt.snowboy.jmUtil;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import ai.kitt.snowboy.demo.R;

public class AlertTime {

    private final Context mContext;
    private SmsSend smsSend;
    private GpsTracker gpsTracker;
    private final String phnum = "01054062436";

    public AlertTime(Context context) {
        this.mContext = context;
    }

    public void showDialog(){
        //경고창
        AlertDialog.Builder ad = new AlertDialog.Builder(mContext);
        ad.setIcon(R.mipmap.ic_launcher);
        ad.setTitle("긴급 신고");//제목
        ad.setMessage("10초 뒤 신고메시지가 전송됩니다.");//내용

        //final EditText et = new EditText(Demo.this);
        //ad.setView(et);

        ad.setPositiveButton("바로 전송", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                report();
                dialog.dismiss();
            }
        });

        ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final AlertDialog alertDialog = ad.create();
        alertDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(alertDialog.isShowing()){
                    report();
                    alertDialog.dismiss();
                }
            }
        }, 10000);
    }

    public void sendMms_alert(String filepath){
        //경고창
        AlertDialog.Builder ad = new AlertDialog.Builder(mContext);
        ad.setIcon(R.mipmap.ic_launcher);
        ad.setTitle("긴급 신고");//제목
        ad.setMessage("10초 뒤 신고메시지가 전송됩니다.");//내용

        //final EditText et = new EditText(Demo.this);
        //ad.setView(et);

        ad.setPositiveButton("바로 전송", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mms_report(filepath);
                dialog.dismiss();
            }
        });

        ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final AlertDialog alertDialog = ad.create();
        alertDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(alertDialog.isShowing()){
                    mms_report(filepath);
                    alertDialog.dismiss();
                }
            }
        }, 10000);
    }

    public void mms_report(String filepath){
        gpsTracker = new GpsTracker(mContext);
        double latitude = gpsTracker.getLatitude();
        double longitude =gpsTracker.getLongitude();
        String loc = getCurrentAddress(latitude,longitude);

        //문자 전송
        smsSend = new SmsSend(mContext);
        String msg = "살려주세요! 제 위치는 "+loc+" 입니다. 주변상황 녹음파일과 함께 전송합니다.";
        //String phnum = "01026670860";
        smsSend.sendMMS(phnum,filepath,msg);
    }

    public void report(){
        gpsTracker = new GpsTracker(mContext);
        double latitude = gpsTracker.getLatitude();
        double longitude =gpsTracker.getLongitude();
        String loc = getCurrentAddress(latitude,longitude);

        //문자 전송
        smsSend = new SmsSend(mContext);
        String msg = "살려주세요! 제 위치는 "+loc+" 입니다.";
        //String phnum = "01026670860";
        smsSend.sendMsg(msg,phnum);
    }

    public String getCurrentAddress( double latitude, double longitude) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());

        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(mContext, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(mContext, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";
        }

        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(mContext, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";
        }

        Address address = addresses.get(0);
        return address.getAddressLine(0).toString()+"\n";

    }

}
