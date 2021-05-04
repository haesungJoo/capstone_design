package ai.kitt.snowboy.util;

import android.app.Activity;
import android.widget.Toast;

public class BackPressedHandler {
    private long backPressedTime = 0;
    private Toast toast;
    private Activity activity;

    public BackPressedHandler(Activity activity) {
        this.activity = activity;
    }

    public void onBackPressed(){
        if(System.currentTimeMillis() > backPressedTime+1500){
            backPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }

        if(System.currentTimeMillis() < backPressedTime+1500){
            activity.finish();
            toast.cancel();
        }
    }

    private void showGuide() {
        toast = Toast.makeText(activity, "뒤로 버튼을 한번더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
        toast.show();
    }
}
