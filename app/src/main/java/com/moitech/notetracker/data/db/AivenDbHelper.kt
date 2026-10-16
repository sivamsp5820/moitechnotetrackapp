package com.moitech.notetracker.data.db

import android.content.Context
import android.util.Log
import com.moitech.notetracker.data.model.TrackedFunction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.DriverManager
import java.util.Properties

object AivenDbHelper {
    private const val TAG = "AivenDbHelper"

    init {
        ensureDriverLoaded()
    }

    private fun ensureDriverLoaded() {
        try {
            Class.forName("org.mariadb.jdbc.Driver")
        } catch (t: Throwable) {
            Log.e(TAG, "Error initializing MariaDB driver", t)
        }
    }

    private fun getConnection(context: Context): Connection {
        ensureDriverLoaded()
        val host = AivenConfigStorage.getHost(context)
        val port = AivenConfigStorage.getPort(context)
        val db = AivenConfigStorage.getDatabase(context)
        val user = AivenConfigStorage.getUser(context)
        val pass = AivenConfigStorage.getPassword(context)

        // Connect directly via TLS with trust mode for Aiven SSL
        val jdbcUrl = "jdbc:mariadb://$host:$port/$db?sslMode=trust&connectTimeout=10000&socketTimeout=15000"

        val props = Properties().apply {
            setProperty("user", user)
            setProperty("password", pass)
        }

        try {
            DriverManager.setLoginTimeout(10)
        } catch (t: Throwable) {}

        return DriverManager.getConnection(jdbcUrl, props)
    }

    suspend fun testConnection(context: Context): Result<String> = withContext(Dispatchers.IO) {
        try {
            getConnection(context).use { conn ->
                conn.createStatement().use { stmt ->
                    stmt.executeQuery("SELECT COUNT(*) AS total FROM pc_function_tracking").use { rs ->
                        if (rs.next()) {
                            val total = rs.getInt("total")
                            Result.success("Connected to Aiven MySQL Cloud! Total functions: $total")
                        } else {
                            Result.success("Connected to Aiven MySQL Cloud successfully!")
                        }
                    }
                }
            }
        } catch (t: Throwable) {
            Log.e(TAG, "Database test connection failed", t)
            Result.failure(t)
        }
    }

    suspend fun fetchTrackedFunctions(context: Context): Result<List<TrackedFunction>> = withContext(Dispatchers.IO) {
        try {
            val list = mutableListOf<TrackedFunction>()
            getConnection(context).use { conn ->
                val sql = """
                    SELECT 
                        systemName,
                        funcId,
                        nickName,
                        funcName,
                        personName,
                        funcHead,
                        funcLocation,
                        funcDate,
                        billNo,
                        isTyped,
                        isPrint,
                        isGivenBinding,
                        isGivenCustomer,
                        progressPercent,
                        phoneNumber,
                        notes,
                        lastSyncedAt,
                        updatedAt
                    FROM pc_function_tracking
                    ORDER BY funcDate DESC, funcId DESC
                """.trimIndent()

                conn.createStatement().use { stmt ->
                    stmt.executeQuery(sql).use { rs ->
                        while (rs.next()) {
                            val sysName = rs.getString("systemName") ?: ""
                            val funcId = rs.getInt("funcId")
                            val nickName = rs.getString("nickName")
                            val funcName = rs.getString("funcName") ?: ""
                            val personName = rs.getString("personName") ?: rs.getString("funcHead") ?: ""
                            val rawDate = rs.getString("funcDate") ?: ""
                            val cleanDate = if (rawDate.length >= 10) rawDate.substring(0, 10) else rawDate
                            val isTyped = rs.getInt("isTyped")
                            val isPrint = rs.getInt("isPrint")
                            val isGivenBinding = rs.getInt("isGivenBinding")
                            val isGivenCustomer = rs.getInt("isGivenCustomer")
                            val phoneNumber = rs.getString("phoneNumber")
                            val notes = rs.getString("notes")
                            val updatedAt = rs.getString("updatedAt")

                            list.add(
                                TrackedFunction(
                                    systemName = sysName,
                                    nickName = nickName,
                                    funcId = funcId,
                                    funcName = funcName,
                                    funcDate = cleanDate,
                                    typed = isTyped,
                                    print = isPrint,
                                    givenForBinding = isGivenBinding,
                                    givenToCustomer = isGivenCustomer,
                                    phoneNumber = phoneNumber,
                                    notes = notes,
                                    customerName = personName,
                                    updatedAt = updatedAt
                                )
                            )
                        }
                    }
                }
            }
            Result.success(list)
        } catch (t: Throwable) {
            Log.e(TAG, "Error fetching tracked functions from Aiven MySQL", t)
            Result.failure(t)
        }
    }

    suspend fun updateStage(
        context: Context,
        systemName: String,
        funcId: Int,
        stage: String,
        newVal: Int
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val col = when (stage) {
                "Typed", "isTyped" -> "isTyped"
                "Print", "isPrint" -> "isPrint"
                "givenForBinding", "isGivenBinding" -> "isGivenBinding"
                "givenToCustomer", "isGivenCustomer" -> "isGivenCustomer"
                else -> return@withContext Result.failure(IllegalArgumentException("Unknown stage: $stage"))
            }

            getConnection(context).use { conn ->
                val sql = "UPDATE pc_function_tracking SET $col = ?, updatedAt = NOW() WHERE systemName = ? AND funcId = ?"
                conn.prepareStatement(sql).use { ps ->
                    ps.setInt(1, newVal)
                    ps.setString(2, systemName)
                    ps.setInt(3, funcId)
                    ps.executeUpdate()
                }
            }
            Result.success(Unit)
        } catch (t: Throwable) {
            Log.e(TAG, "Error updating stage in Aiven MySQL", t)
            Result.failure(t)
        }
    }

    suspend fun updateFunctionFull(
        context: Context,
        systemName: String,
        funcId: Int,
        phoneNumber: String?,
        nickName: String?,
        notes: String?,
        typed: Int,
        print: Int,
        binding: Int,
        customer: Int
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            getConnection(context).use { conn ->
                val sql = """
                    UPDATE pc_function_tracking 
                    SET phoneNumber = ?, 
                        nickName = ?, 
                        notes = ?, 
                        isTyped = ?, 
                        isPrint = ?, 
                        isGivenBinding = ?, 
                        isGivenCustomer = ?, 
                        updatedAt = NOW() 
                    WHERE systemName = ? AND funcId = ?
                """.trimIndent()

                conn.prepareStatement(sql).use { ps ->
                    ps.setString(1, phoneNumber?.takeIf { it.isNotBlank() })
                    ps.setString(2, nickName?.takeIf { it.isNotBlank() })
                    ps.setString(3, notes?.takeIf { it.isNotBlank() })
                    ps.setInt(4, typed)
                    ps.setInt(5, print)
                    ps.setInt(6, binding)
                    ps.setInt(7, customer)
                    ps.setString(8, systemName)
                    ps.setInt(9, funcId)
                    ps.executeUpdate()
                }
            }
            Result.success(Unit)
        } catch (t: Throwable) {
            Log.e(TAG, "Error updating function details in Aiven MySQL", t)
            Result.failure(t)
        }
    }

    suspend fun updatePcNickname(
        context: Context,
        systemName: String,
        nickName: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            getConnection(context).use { conn ->
                val sql = "UPDATE pc_function_tracking SET nickName = ?, updatedAt = NOW() WHERE systemName = ?"
                conn.prepareStatement(sql).use { ps ->
                    ps.setString(1, nickName.takeIf { it.isNotBlank() })
                    ps.setString(2, systemName)
                    ps.executeUpdate()
                }
            }
            Result.success(Unit)
        } catch (t: Throwable) {
            Log.e(TAG, "Error updating PC nickname in Aiven MySQL", t)
            Result.failure(t)
        }
    }
}
