package com.capstone.jeonshimclient

import com.google.gson.annotations.SerializedName
import java.sql.Date

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
data class MeasurementGenDay(
    var current: Float,  // 측정하는 전류
    @SerializedName("time_current") var timeCurrent: String,
    var voltage: Float, // 측정하는 전압
    @SerializedName("time_voltage") var timeVoltage: String
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