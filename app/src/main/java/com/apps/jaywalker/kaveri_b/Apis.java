package com.apps.jaywalker.kaveri_b;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Apis {

    @FormUrlEncoded
    @POST("donation.php")
    Call<DonationResponse> donation(@Field("Cust_name") String customername,
                                            @Field("Address") String address,@Field("Phone") String phno,
                                          @Field("Payment_method") String paymet,
                                          @Field("Payment_id") String payid,@Field("amount") String amount);

}
