package com.icsseseguridad.locationsecurity.controller;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

class APIClient {

    private static Retrofit retrofit = null;

    static Retrofit getClient() {

        //HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        //interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        //OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl("http://icsseseguridad.com/api/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                //.client(client)
                .build();

        return retrofit;
    }

    protected static Interceptor interceptor = new Interceptor() {
        @Override
        public Response intercept(Interceptor.Chain chain) throws IOException {
            Response response = chain.proceed(chain.request());
            ResponseBody body = response.body();
            String bodyString = body.string();
            MediaType contentType = body.contentType();
            Log.d("Response", bodyString);
            return response.newBuilder().body(ResponseBody.create(contentType, bodyString)).build();
        }
    };

}