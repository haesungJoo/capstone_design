package ai.kitt.snowboy.hotWordSetupUtil;

import android.os.Environment;

import java.io.File;

public class FileExistCheck {

    private String fileName1 = null;
    private String fileName2 = null;
    private String fileName3 = null;

    public FileExistCheck(String fileName1, String fileName2, String fileName3) {
        this.fileName1 = fileName1;
        this.fileName2 = fileName2;
        this.fileName3 = fileName3;
    }

    public boolean fileExist(){
        File file1 = filePathConnector(this.fileName1);
        File file2 = filePathConnector(this.fileName2);
        File file3 = filePathConnector(this.fileName3);
        if(file1.exists() && file2.exists() && file3.exists()){
            return true;
        }else{
            return false;
        }
    }

    public File filePathConnector(String name){
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separatorChar+name;
        File file = new File(path);
        return file;
    }

    public void fileDelete(File... files){

        try{
            for(File file: files){
                if(file.exists()){
                    file.delete();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
