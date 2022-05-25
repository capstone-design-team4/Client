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
    fun getPredictionUsage(
        @Path("userId") userId: String
    ): Call<List<PredictionUsage>>

    @GET("/graph/predictionGen")
    @Headers(
        "accept: application/json",
        "content-type: application/json"
    )
    fun getPredictionGen(
    ): Call<List<PredictionGen>>


    @GET("/graph/measurementUsageWeek/{userId}")
    @Headers(
        "accept: application/json",
        "content-type: application/json"
    )
    fun getMeasurementUsageWeek(
        @Path("userId") userId: String
    ): Call<List<MeasurementUsageWeek>>


    @GET("/graph/measurementUsageDay/{userId}")
    @Headers(
        "accept: application/json",
        "content-type: application/json"
    )
    fun getMeasurementUsageDay(
        @Path("userId") userId: String
    ): Call<List<MeasurementUsageDay>>

    @GET("/graph/measurementGenWeek")
    @Headers(
        "accept: application/json",
        "content-type: application/json"
    )
    fun getMeasurementGenWeek(
    ): Call<List<MeasurementGenWeek>>

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
    ): Call<List<DrRequestInfo>>

    // 특정 day에 떴던 DrRequest에 대한 정보를 get
    @GET("/drRequest/requestInfo/{day}")
    @Headers(
        "accept: application/json",
        "contemt-type: application/json"
    )
    fun getDrRequestInfoDay(
        @Path("day") day: String
    ): Call<List<DrRequestInfo>>

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
    fun getDrRequestInfoDayOf():Call<DrRequestInfo>

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