package com.capstone.jeonshimclient

import com.google.gson.annotations.SerializedName
import java.sql.Date

data class PostResult(
    var result: String
)

// user 정보
data class UserInformation(
    @SerializedName("user_id") var userId: Int,
    @SerializedName("voltage_port") var voltageId: Int,
    @SerializedName("current_port") var currentId: Int,
    @SerializedName("relay1_id") var relay1Id: Int,
    @SerializedName("relay2_id") var relay2ID: Int,
    var fee: Int,
    @SerializedName("updated_date") var updatedDate: Date
)

// 배터리 정보
data class BatteryInformation(
    var voltageId: Int, //
    var charge: Float   // 배터리 저장량
)

data class Prediction(
    var amount: Float,
    var period: String
)

data class Measurement(
    var current: Float,
    @SerializedName("time_current") var timeCurrent: String,
    var voltage: Float, //
    @SerializedName("time_voltage") var timeVoltage: String
)

data class RelayController(
    var relayId: Int,
    var relayIsUsing: Boolean
)

// DR request Data
data class DrRequestInfo(
    var requestId: Int? = null,
    var requestStartTime: String,
    var requestEndTime: String,
    var amount: Float,
    var user1Flag: Boolean? = null,
    var user2Flag: Boolean? = null,
    var user3Flag: Boolean? = null,
    var decisionFlag: Boolean? = null
)

data class RelayIsUsing(
    var relayIsUsing: Boolean
)