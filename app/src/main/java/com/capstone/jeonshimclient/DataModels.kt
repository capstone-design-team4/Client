package com.capstone.jeonshimclient

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

data class Battery(
    var batteryId: Int,
    var voltagePort: Int,
    var charge: Float,
    var updatedDate: Date
)

data class Generator(
    var generatorId: Int,
    var voltagePort: Int,
    var currentPort: Int
)

data class MeasurementCurrent(
    var generatorId: Int,
    var voltagePort: Int,
    var Current_Port: Int
)

data class MeasurmentVoltage(
    var voltageId: Int,
    var portId: Int,
    var voltage: Float,
    var updatedDate: Date
)

data class Relay(
    var relayId: Int,
    var port: Int,
    var relayIsUsing: Boolean,
    var updatedDate: Date
)

data class UserData(
    val name: String,
    val supplied: Float,
    val update: String
)
