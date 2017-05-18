package cn.palmap.jilinscience.api;

import cn.palmap.jilinscience.model.ApiCode;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by stone on 2017/5/15.
 */

public interface UserBirthdayService {
    @Multipart
    @POST("/jlsci/user/updateBirthday")
    Call<ApiCode> updateBirthday(@Header("authentication-customer") String customer, @Part("birthday") RequestBody userBirthday);
}
