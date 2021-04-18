package ai.kitt.snowboy.serverUtil;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface RetrofitService {
    @Multipart
    @POST("test/")
    Call<ResponseBody> uploadAttachmentMutilple(@Part List<MultipartBody.Part> fileParts);
}
