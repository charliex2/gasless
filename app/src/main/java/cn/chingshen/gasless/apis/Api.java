package cn.chingshen.gasless.apis;

import android.content.Context;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Api {
    public GasNowApi gasNowApi;
    Context context;


    public Api(Builder builder) {
        context = builder.context;
        OkHttpClient client = new OkHttpClient().newBuilder()
                .retryOnConnectionFailure(true)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(builder.baseUrl)
                .client(client)
                .build();
        gasNowApi = retrofit.create(GasNowApi.class);
    }

    public static class Builder {
        private final Context context;
        private final String baseUrl;

        public Builder(Context context, String baseUrl) {
            this.context = context;
            this.baseUrl = baseUrl;
        }

        public Builder(Context context) {
            this.context = context;
            this.baseUrl = "https://www.gasnow.org";
        }
    }
}
