package ai.kitt.snowboy.hotWordSetupUtil;

import android.os.Environment;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import ai.kitt.snowboy.util.PcmToWavFile;

public class AudioDataSaverForHotwordSetup {
    private File saveFile = null;
    private DataOutputStream dataOutputStream = null;
    private PcmToWavFile pcmToWavFile;

    private static String mPath;

    public AudioDataSaverForHotwordSetup(String fileName) {
        mPath = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separatorChar+fileName;
        pcmToWavFile = new PcmToWavFile();
        this.saveFile = new File(mPath);
    }

    public void start(){
        if(saveFile != null){
            if(saveFile.exists()){
                saveFile.delete();
            }
            try{
                saveFile.createNewFile();
            }catch (Exception e){
                e.printStackTrace();
            }

            try{
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(this.saveFile));
                dataOutputStream = new DataOutputStream(bufferedOutputStream);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void onAudioDataReceived(byte[] data, int length){
        try{
            if(dataOutputStream != null){
                dataOutputStream.write(data, 0, length);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void stop(){

        String pFilePath = null;

        if(dataOutputStream != null){
            try{
                dataOutputStream.close();
            }catch (Exception e){
                e.printStackTrace();
            }

            pFilePath = mPath.replace("pcm", "wav");

            File f1 = new File(mPath);
            File f2 = new File(pFilePath);

            // 기존 pcm 파일을 wav로 변환
            try{
                pcmToWavFile.rawToWave(f1, f2);
            }catch (Exception e){
                e.printStackTrace();
            }

            // 기존 pcm 파일 제거
            try{
                if(f1.exists()){
                    f1.delete();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
