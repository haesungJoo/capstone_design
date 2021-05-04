package ai.kitt.snowboy.util;

import android.content.Context;
import android.widget.Toast;

public class UtilMethods
{
    public static void toastMessage(String message, Context ctx){
        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
    }
}
