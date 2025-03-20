package com.example.carparkfinder.api;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Interceptor;
import okhttp3.logging.HttpLoggingInterceptor;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class RetrofitClient {
    private static final String BASE_URL = "https://api.data.gov.sg/"; // ✅ Ensure trailing slash
    private static Retrofit retrofit = null;

    public static CarParkApiService getClient() {
        if (retrofit == null) {
            // ✅ Add logging interceptor for debugging
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // ✅ Build OkHttpClient with timeouts and interceptors
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS) // 30s Connection Timeout
                    .readTimeout(30, TimeUnit.SECONDS) // 30s Read Timeout
                    .writeTimeout(30, TimeUnit.SECONDS) // 30s Write Timeout
                    .retryOnConnectionFailure(true) // Auto retry on failure
                    .addInterceptor(loggingInterceptor) // Logs API requests/responses
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request original = chain.request();
                            Request.Builder requestBuilder = original.newBuilder()
                                    .header("User-Agent", "CarParkFinderApp")
                                    .header("Accept", "application/json");

                            Request request = requestBuilder.build();
                            return chain.proceed(request);
                        }
                    })
                    .build();

            // ✅ Build Retrofit instance
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(CarParkApiService.class);
    }
}
