package ai.kitt.snowboy.util;

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ai.kitt.snowboy.Constants;
import ai.kitt.snowboy.audio.AudioDataReceivedListener;

public class TimerDataSaver implements AudioDataReceivedListener {

    private static final String TAG = TimerDataSaver.class.getSimpleName();

    private File saveFile = null;
    private DataOutputStream dataOutputStream = null;

    private String mFilePath = null;

    public TimerDataSaver() {
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MMdd_HH_mm_ss_");
        mFilePath = Constants.SAVE_AUDIO_PROJECT +sdf.format(today)+"project.pcm";
        this.saveFile = new File(mFilePath);
        Log.e(TAG, sdf.format(today));
    }

    @Override
    public void start() {
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
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onAudioDataReceived(byte[] data, int length) {
        try{
            if(dataOutputStream != null){
                dataOutputStream.write(data, 0, length);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {

    }

    public String stop_timer() {

        String pFilePath = null;

        if(dataOutputStream != null){
            try{
                dataOutputStream.close();
            }catch (Exception e){
                e.printStackTrace();
            }

            pFilePath = mFilePath.replace("pcm", "wav");

            File f1 = new File(mFilePath);
            File f2 = new File(pFilePath);

            try{
                rawToWave(f1, f2);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return pFilePath;
    }

    private void rawToWave(final File rawFile, final File waveFile) throws IOException {

        byte[] rawData = new byte[(int) rawFile.length()];

        DataOutputStream output = null;
        try {
            output = new DataOutputStream(new FileOutputStream(waveFile));
            // WAVE header
            writeString(output, "RIFF"); // chunk id
            writeInt(output, 36 + rawData.length); // chunk size
            writeString(output, "WAVE"); // format
            writeString(output, "fmt "); // subchunk 1 id
            writeInt(output, 16); // subchunk 1 size
            writeShort(output, (short) 1); // audio format (1 = PCM)
            writeShort(output, (short) 1); // number of channels
            writeInt(output, Constants.SAMPLE_RATE); // sample rate
            writeInt(output, Constants.SAMPLE_RATE * 2); // byte rate
            writeShort(output, (short) 2); // block align
            writeShort(output, (short) 16); // bits per sample
            writeString(output, "data"); // subchunk 2 id
            writeInt(output, rawData.length); // subchunk 2 size

            output.write(fullyReadFileToBytes(rawFile));
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }

    byte[] fullyReadFileToBytes(File f) throws IOException {
        int size = (int) f.length();
        byte bytes[] = new byte[size];
        byte tmpBuff[] = new byte[size];
        FileInputStream fis= new FileInputStream(f);
        try {
            int read = fis.read(bytes, 0, size); // read는 size만큼의 데이터를 읽어서 bytes에 저장한다.
            if (read < size) {
                int remain = size - read;
                while (remain > 0) {
                    read = fis.read(tmpBuff, 0, remain);
                    System.arraycopy(tmpBuff, 0, bytes, size - remain, read);
                    remain -= read;
                }
            }
        }  catch (IOException e){
            throw e;
        } finally {
            fis.close();
        }

        return bytes;
    }

    private void writeInt(final DataOutputStream output, final int value) throws IOException {
        output.write(value >> 0);
        output.write(value >> 8);
        output.write(value >> 16);
        output.write(value >> 24);
    }

    private void writeShort(final DataOutputStream output, final short value) throws IOException {
        output.write(value >> 0);
        output.write(value >> 8);
    }

    private void writeString(final DataOutputStream output, final String value) throws IOException {
        for (int i = 0; i < value.length(); i++) {
            output.write(value.charAt(i));
        }
    }
}
