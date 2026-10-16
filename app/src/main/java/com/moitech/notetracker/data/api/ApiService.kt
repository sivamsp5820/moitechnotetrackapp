package com.moitech.notetracker.data.api

import com.moitech.notetracker.data.model.ApiResponse
import com.moitech.notetracker.data.model.TrackedFunction
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("tracking/functions")
    suspend fun getTrackedFunctions(
        @Query("systemName") systemName: String? = null
    ): Response<ApiResponse<List<TrackedFunction>>>

    @PUT("tracking/functions/{systemName}/{funcId}")
    suspend fun updateFunctionTracking(
        @Path("systemName") systemName: String,
        @Path("funcId") funcId: Int,
        @Body body: Map<String, Any?>
    ): Response<ApiResponse<TrackedFunction>>

    @PUT("tracking/pc-nickname")
    suspend fun updatePcNickname(
        @Body body: Map<String, String>
    ): Response<ApiResponse<Any>>

    @POST("tracking/functions")
    suspend fun createTrackedFunction(
        @Body body: Map<String, Any?>
    ): Response<ApiResponse<TrackedFunction>>
}
