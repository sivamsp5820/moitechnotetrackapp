package com.moitech.notetracker.data.model

import com.google.gson.annotations.SerializedName

data class TrackedFunction(
    @SerializedName("systemName") val systemName: String,
    @SerializedName("NickName") val nickName: String? = null,
    @SerializedName("funcId") val funcId: Int,
    @SerializedName("FuncName") val funcName: String,
    @SerializedName("FuncDate") val funcDate: String,
    @SerializedName("FuncTime") val funcTime: String? = null,
    @SerializedName("Typed") val typed: Int = 0,
    @SerializedName("Print") val print: Int = 0,
    @SerializedName("givenForBinding") val givenForBinding: Int = 0,
    @SerializedName("givenToCustomer") val givenToCustomer: Int = 0,
    @SerializedName("phoneNumber") val phoneNumber: String? = null,
    @SerializedName("notes") val notes: String? = null,
    @SerializedName("customerName") val customerName: String? = null,
    @SerializedName("totalAmount") val totalAmount: Double? = null,
    @SerializedName("isDeleted") val isDeleted: Int = 0,
    @SerializedName("updatedAt") val updatedAt: String? = null
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
