package com.capstone.jeonshimclient

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface APIS {
    @POST("/userinfo/create")
    @Headers(
        "accept: application/json",
        "content-type: application/json"
    )
    fun postUserInformation(
        @Body jsonparams: UserInformation
    ): Call<Int>

    @GET("/graph/predictUsage/{userId}")
    @Headers(
        "accept: application/json",
        "content-type: application/json"
    )
    fun graphPredictUsageOf(
        @Path("userId") userId: String
    ): Call<Int>

    @GET("/graph/predictGen")
    @Headers(
        "accept: application/json",
        "content-type: application/json"
    )
    fun graphPredictGen(
    ): Call<Int>

    @GET("/graph/measurementUsage/{userId}")
    @Headers(
        "accept: application/json",
        "content-type: application/json"
    )
    fun graphMeasurementUsageOf(
        @Path("userId") userId: String
    ): Call<Int>

    @GET("/graph/measurementGenDay")
    @Headers(
        "accept: application/json",
        "content-type: application/json"
    )
    fun getMeasurementGenDay(
    ): Call<List<MeasurementGenDay>>


    // drRequest ********************************************

    // 지금 현재 뜬 DRrequest에 대한 정보를 get
    @GET("/drRequest/requestInfo")
    @Headers(
        "accept: application/json",
        "content-type: application/json"
    )
    fun getDrRequestInfo(
    ): Call<List<DRRequestInfo>>

    //
    @GET("/drRequest/decisionFlag")
    @Headers(
        "accept: application/json",
        "content-type: application/json"
    )
    fun getDrDecisionFlag(
    ): Call<Boolean>

    @GET("drRequest/requestInfo/day")
    @Headers(
        "accept: application/json",
        "content-type: application/json"
    )
    fun getDrRequestInfoDayOf():Call<DRRequestInfo>

    @GET("/drRequest/contractInfo")
    @Headers(
        "accept: application/json",
        "content-type: application/json"
    )
    fun getDrContractInfo(
    ): Call<Int>

    companion object { // static 처럼 공유객체로 사용가능함. 모든 인스턴스가 공유하는 객체로서 동작함.
        private const val BASE_URL = "http://158.247.216.131:8080" // 주소

        fun create(): APIS {

            val gson: Gson = GsonBuilder().setLenient().create();

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
//                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(APIS::class.java)
        }
    }
}