package com.capstone.jeonshimclient

import com.google.gson.annotations.SerializedName
import java.sql.Date

data class HTTP_GET_Model(
    var something: String? = null,
    var users: ArrayList<UserModel>? = null
)

data class UserModel(
    var idx: Int? = null,
    var id: String? = null,
    var nick: String? = null
)

data class PostModel(
    var id: String? = null,
    var pwd: String? = null,
    var nick: String? = null
)

data class PostResult(
    var result: String? = null
)

///////////////////////////////////
// ArrayList<>로 받을 data class
///////////////////////////////////

data class USERS(
    var users: ArrayList<UserInformation>? = null
)

///////////////////////////////////

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

// 태양광 패널 정보
data class GeneratorInformation(
    var voltageId: Int, // 측정하는 전압 센서 id
    var currentId: Int  // 측정하는 전류 센서 id
)

// 데이터콜렉터
// DB에 접속하여 건물 사용자의 id와
// 배터리와 태양광 패널의 측정 센서의 id를 가져야 함
data class DataCollector(
    var userId: Int,
    var batteryVoltageId: Int,
    var generatorVoltageId: Int,
    var generatorCurrentId: Int
)

// PowerSupplyVerification 클래스를 위함
// 현재 건물 사용자가 어떤 전력을 사용 중인지 확인
data class PowerSupplyVerification(
    var userId: Int,
    var relayIsUsing: Boolean
)

// DR request 받았을 때
// 감축량과
// 시작 & 종료 date
data class DRRequester(
    var requestAmount: Float,
    var startTime: Date,
    var endTime: Date
)