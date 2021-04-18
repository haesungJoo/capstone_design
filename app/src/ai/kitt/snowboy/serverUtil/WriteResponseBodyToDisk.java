package ai.kitt.snowboy.serverUtil;

import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;

import ai.kitt.snowboy.Constants;
import okhttp3.ResponseBody;

import static android.content.ContentValues.TAG;

public class WriteResponseBodyToDisk {

    public static String HOTWORD_PATH = Constants.DEFAULT_WORK_SPACE + File.separatorChar;

    public boolean writeFileToAsset(ResponseBody body){

        File dir = new File(HOTWORD_PATH);

        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Log.e(TAG, "mkdir failed: "+HOTWORD_PATH);
//                return
            } else {
                Log.i(TAG, "mkdir ok: "+HOTWORD_PATH);
            }
        } else {
            Log.w(TAG, HOTWORD_PATH+" already exists!");
        }

        try{
            File fromResponseBodyFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separatorChar+"testHorword.pmdl");

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try{
                byte[] fileReader = new byte[1024];

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(fromResponseBodyFile);

                while(true){
                    int read = inputStream.read(fileReader);

                    if(read==-1){
                        break;
                    }

                    outputStream.write(fileReader,0,read);

                }

                outputStream.flush();

                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            } finally{
                if(inputStream != null){
                    inputStream.close();
                }
                if(outputStream != null){
                    outputStream.close();
                }
            }
        } catch (Exception e){
            return false;
        }
    }
}