package cn.palmap.jilinscience.api;

import cn.palmap.jilinscience.model.ApiCode;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by stone on 2017/5/12.
 */

public interface UserSexService {
    @Multipart
    @POST("/jlsci/user/updateSex")
    Call<ApiCode> uploadSex(@Header("authentication-customer") String customer, @Part("sex") int sexId);
}
