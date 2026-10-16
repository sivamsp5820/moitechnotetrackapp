package com.moitech.notetracker.data.model

import com.google.gson.annotations.SerializedName

data class TrackedFunction(
    @SerializedName(value = "systemName", alternate = ["SystemName"]) val systemName: String,
    @SerializedName(value = "nickName", alternate = ["NickName"]) val nickName: String? = null,
    @SerializedName(value = "funcId", alternate = ["Funcid", "FuncId"]) val funcId: Int,
    @SerializedName(value = "funcName", alternate = ["FuncName"]) val funcName: String,
    @SerializedName(value = "funcDate", alternate = ["FuncDate"]) val funcDate: String,
    @SerializedName(value = "funcTime", alternate = ["FuncTime"]) val funcTime: String? = null,
    @SerializedName(value = "isTyped", alternate = ["Typed", "typed"]) val typed: Int = 0,
    @SerializedName(value = "isPrint", alternate = ["Print", "print"]) val print: Int = 0,
    @SerializedName(value = "isGivenBinding", alternate = ["givenForBinding", "binding"]) val givenForBinding: Int = 0,
    @SerializedName(value = "isGivenCustomer", alternate = ["givenToCustomer", "customer"]) val givenToCustomer: Int = 0,
    @SerializedName(value = "phoneNumber", alternate = ["phone", "PhoneNumber"]) val phoneNumber: String? = null,
    @SerializedName(value = "notes", alternate = ["Notes"]) val notes: String? = null,
    @SerializedName(value = "customerName", alternate = ["personName", "PersonName"]) val customerName: String? = null,
    @SerializedName(value = "totalAmount", alternate = ["TotalAmount"]) val totalAmount: Double? = null,
    @SerializedName(value = "isDeleted", alternate = ["IsDeleted"]) val isDeleted: Int = 0,
    @SerializedName(value = "updatedAt", alternate = ["lastSyncedAt", "UpdatedAt"]) val updatedAt: String? = null
) {
    val isTyped: Boolean get() = typed == 1
    val isPrint: Boolean get() = print == 1
    val isBinding: Boolean get() = givenForBinding == 1
    val isDelivered: Boolean get() = givenToCustomer == 1
    val isFullyCompleted: Boolean get() = isTyped && isPrint && isBinding && isDelivered

    val completedStagesCount: Int get() {
        var count = 0
        if (isTyped) count++
        if (isPrint) count++
        if (isBinding) count++
        if (isDelivered) count++
        return count
    }

    val stageProgress: Float get() = completedStagesCount / 4f

    val displaySystem: String get() = nickName?.takeIf { it.isNotBlank() } ?: systemName
}
