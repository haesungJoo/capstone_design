package ai.kitt.snowboy.serverUtil;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ai.kitt.snowboy.demo.R;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AudioClient {

    private static Retrofit retrofit = null;

    public static Retrofit getClient(Context context){
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        if(retrofit == null){
            retrofit = new Retrofit.Builder()
//                    .baseUrl(context.getString(R.string.base_url)).client(client)
                    .baseUrl(context.getString(R.string.base_url))
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }

        return retrofit;
    }

    public static RetrofitService getRetrofitService(Context context){
        return getClient(context).create(RetrofitService.class);
    }
}
