package cn.palmap.jilinscience.api;

import cn.palmap.jilinscience.model.ApiCode;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by stone on 2017/5/11.
 */

public interface HeadImageService {
    @Multipart
    @POST("/jlsci/user/updateHeadPath")
    Call<ApiCode> updateImage(@Header("authentication-customer") String customer,
                              @Part("headPath") RequestBody  path);
}
