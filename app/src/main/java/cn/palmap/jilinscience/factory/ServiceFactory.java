package cn.palmap.jilinscience.factory;

import android.os.Environment;

import com.google.gson.Gson;

import java.io.File;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

import cn.palmap.jilinscience.config.ServereConfig;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by 王天明 on 2015/12/18 0018.
 */
public class ServiceFactory {

    private static final String endpoint = ServereConfig.HOST;
    private static Retrofit retrofit;
    private static Gson gson = new Gson();
    private static WeakHashMap<String, Object> serviceCache = new WeakHashMap<>();

    static {

        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.connectTimeout(15, TimeUnit.SECONDS);

        builder.cache(new Cache(new File(Environment.getExternalStorageDirectory() + ServereConfig.CECHE_FILE), ServereConfig.CECHE_SIZE));

        builder.interceptors().add(new HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY));

        OkHttpClient okHttpClient = builder.build();

        retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(endpoint)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public static <T> T create(Class<T> service) {
        T t = (T) serviceCache.get(service.getSimpleName());
        if (null == t) {
            long time = System.currentTimeMillis();
            t = retrofit.create(service);
            serviceCache.put(service.getSimpleName(), t);
        }
        return t;
    }

    public static Gson getGson() {
        return gson;
    }
}