package com.capstone.jeonshimclient

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.lang.reflect.Type

interface APIS {
    @POST("/relay/relayController")
    @Headers(
        "accept: application/json",
        "content-type: application/json"
    )
    fun postRelayController(
        @Body params: RelayController
    ): Call<PostResult>

    @GET("/graph/predictionUsage")
    @Headers(
        "accept: application/json",
        "content-type: application/json"
    )
    fun getPredictionUsage(
    ): Call<List<Prediction>>

    @GET("/graph/predictionGen")
    @Headers(
        "accept: application/json",
        "content-type: application/json"
    )
    fun getPredictionGen(
    ): Call<List<Prediction>>


    @GET("/graph/measurementUsageWeek/{userId}")
    @Headers(
        "accept: application/json",
        "content-type: application/json"
    )
    fun getMeasurementUsageWeek(
        @Path("userId") userId: String
    ): Call<List<Measurement>>


    @GET("/graph/measurementUsageDay/{userId}")
    @Headers(
        "accept: application/json",
        "content-type: application/json"
    )
    fun getMeasurementUsageDay(
        @Path("userId") userId: String
    ): Call<List<Measurement>>

    @GET("/graph/measurementGen")
    @Headers(
        "accept: application/json",
        "content-type: application/json"
    )
    fun getMeasurementGen():Call<List<Measurement>>

    @GET("/graph/measurementUsage/{userId}")
    @Headers(
        "accept: application/json",
        "content-type: application/json"
    )
    fun getMeasurementUsage(
        @Path("userId") userId: Int
    ):Call<List<Measurement>>

    @GET("/userManagement/batteryCharge")
    @Headers(
        "accept: application/json",
        "content-type: application/json"
    )
    fun getBatteryCharge(
    ): Call<BatteryCharge>

    @GET("/graph/measurementGenWeek")
    @Headers(
        "accept: application/json",
        "content-type: application/json"
    )
    fun getMeasurementGenWeek(
    ): Call<List<Measurement>>

    @GET("/graph/yearGenData")
    @Headers(
        "accept: application/json",
        "content-type: application/json"
    )
    fun getYearGenData(
    ): Call<List<YearData>>

    @GET("/graph/measurementGenDay")
    @Headers(
        "accept: application/json",
        "content-type: application/json"
    )
    fun getMeasurementGenDay(
    ): Call<List<Measurement>>


    // drRequest ********************************************

    // ??? ????????? ???????????? ???????????? ?????? requestId??? ????????? ??????.
    // requestId??? userId??? ???????????? ??? request??? ???????????? user??? ????????? ???????????? ??? ??? ??????.
    @GET("/drRequest/usageAtDrTime/{requestId}/{userId}")
    @Headers(
        "accept: application/json",
        "content-type: application/json"
    )
    fun getUsageAtDrTime(
        @Path("requestId") requestId: String,
        @Path("userId") userId: String
    ): Call<List<Measurement>>


    // ?????? day??? ?????? DrRequest??? ?????? ????????? get
    @GET("/drRequest/requestInfo/{day}")
    @Headers(
        "accept: application/json",
        "content-type: application/json"
    )
    fun getDrRequestInfoDay(
        @Path("day") day: String
    ): Call<DrRequestInfo>

    //

    // user id??? ???????????? ????????? ????????? on?????? ??????
    @GET("/pi/relay/{userId}")
    @Headers(
        "accept: application/json",
        "content-type: application/json"
    )
    fun getRelayIsUsing(
        @Path("userId") userId: Int
    ): Call<RelayIsUsing>

    @GET("/userManagement/usageMonth/{userId}")
    @Headers(
        "accept: application/json",
        "content-type: application/json"
    )
    fun getUsageMonth(
        @Path("userId") userId: Int
    ): Call<List<Measurement>>

    @GET("/graph/yearUsageData")
    @Headers(
        "accept: application/json",
        "content-type: application/json"
    )
    fun getYearUsageData(
    ): Call<List<YearData>>

    @GET("/userManagement/usageDay/{userId}")
    @Headers(
        "accept: application/json",
        "content-type: application/json"
    )
    fun getUsageDay(
        @Path("userId") userId: Int
    ): Call<List<Measurement>>

    @GET("/userManagement/fee/{userId}")
    @Headers(
        "accept: application/json",
        "content-type: application/json"
    )
    fun getFee(
        @Path("userId") userId: Int
    ): Call<Fee>

    companion object { // static ?????? ??????????????? ???????????????. ?????? ??????????????? ???????????? ???????????? ?????????.
        private const val BASE_URL = "http://158.247.216.131:8080" // ??????
        private val nullOnEmptyConverterFactory = NullOnEmptyConverterFactory()
        fun create(): APIS {

            val gson: Gson = GsonBuilder().setLenient().create();

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
//                .client(client)
                .addConverterFactory(nullOnEmptyConverterFactory)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(APIS::class.java)
        }
    }
}

class NullOnEmptyConverterFactory : Converter.Factory() {
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *> {
        val delegate: Converter<ResponseBody, *> =
            retrofit.nextResponseBodyConverter<Any>(this, type, annotations)
        return Converter { body -> if (body.contentLength() == 0L) null else delegate.convert(body) }
    }
}