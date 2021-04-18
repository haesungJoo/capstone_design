package ai.kitt.snowboy.serverUtil;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ai.kitt.snowboy.Constants;
import ai.kitt.snowboy.MsgEnum;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ServerService {
    RetrofitService retrofitService;
    WriteResponseBodyToDisk toDisk;
    Context ctx;
    Handler handler;

    public ServerService(Context ctx, Handler handler) {
        this.ctx = ctx;
        this.handler = handler;
        retrofitService = AudioClient.getRetrofitService(ctx);
        toDisk = new WriteResponseBodyToDisk();
    }

    public void requestUploadMultiple(File file1, File file2, File file3) {

        List<MultipartBody.Part> parts = new ArrayList<>();

        MultipartBody.Part filePart1 = MultipartBody.Part.createFormData("audioFile1", file1.getName(), RequestBody.create(MediaType.parse("audio/*"), file1));
        MultipartBody.Part filePart2 = MultipartBody.Part.createFormData("audioFile2", file2.getName(), RequestBody.create(MediaType.parse("audio/*"), file2));
        MultipartBody.Part filePart3 = MultipartBody.Part.createFormData("audioFile3", file3.getName(), RequestBody.create(MediaType.parse("audio/*"), file3));

        parts.add(filePart1);
        parts.add(filePart2);
        parts.add(filePart3);

        Call<ResponseBody> call = retrofitService.uploadAttachmentMultiple(parts);

        call.clone().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                File hotwordExist = new File(Constants.PERSONAL_MODEL_GENERATED);
                if(hotwordExist.exists()){
                    hotwordExist.delete();
                }
                toDisk.writeFileToAsset(response.body());
//                Toast.makeText(ctx,"연결됨", Toast.LENGTH_SHORT).show();
                sendMessage(MsgEnum.MSG_MODEL_GENERATED,"");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Toast.makeText(ctx,"연결안됨", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessage(MsgEnum what, Object obj){
        if (null != handler) {
            Message msg = handler.obtainMessage(what.ordinal(), obj);
            handler.sendMessage(msg);
        }
    }
}
