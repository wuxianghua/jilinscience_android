package cn.palmap.jilinscience.api;


import cn.palmap.jilinscience.model.ApiCode;
import cn.palmap.jilinscience.model.User;
import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by 王天明 on 2017/5/8.
 */

public interface UserService {

    /**
     * 根据手机号码请求
     * @param mobile
     * @return
     */
    @FormUrlEncoded
    @POST("/jlsci/app/messageVerify")
    Observable<ApiCode> requestCode(@Field("phone") String mobile);

    /**
     * 用户注册
     * @param mobile
     * @param pwd
     * @param code
     * @return
     */
    @FormUrlEncoded
    @POST("/jlsci/app/registerUser")
    Observable<ApiCode> registerUser(@Field("phone") String mobile,
                                     @Field("password") String pwd,
                                     @Field("auth") String code);

    /**
     * 使用密码登录
     * @param mobile
     * @param pwd
     * @return
     */
    @FormUrlEncoded
    @POST("/jlsci/app/loginByPassword")
    Observable<ApiCode> loginByPassword(@Field("phone")String mobile,@Field("password")String pwd);

    /**
     * 使用验证码登录
     * @param mobile
     * @param code
     * @return
     */
    @FormUrlEncoded
    @POST("/jlsci/app/loginByAuth")
    Observable<ApiCode> loginByAuth(@Field("phone")String mobile,@Field("auth")String code);

    /**
     * 查询用户信息
     * @param customer  对应值为  登陆账号 + ”;” + 身份密匙(在登录的msg获取)
     * @return
     */
    @GET("/jlsci/user/getUser")
    Observable<User> getUser(@Header("authentication-customer") String customer);

}
