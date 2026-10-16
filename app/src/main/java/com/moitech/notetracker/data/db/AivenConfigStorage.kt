package com.moitech.notetracker.data.db

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64

object AivenConfigStorage {
    private const val PREFS_NAME = "moi_aiven_cloud_prefs"
    private const val KEY_HOST = "aiven_host"
    private const val KEY_PORT = "aiven_port"
    private const val KEY_DB = "aiven_db"
    private const val KEY_USER = "aiven_user"
    private const val KEY_CUSTOM_PASS = "aiven_custom_pass"

    const val DEFAULT_HOST = "smc-numupload-mspsiva5820-9214.l.aivencloud.com"
    const val DEFAULT_PORT = 21040
    const val DEFAULT_DB = "defaultdb"
    const val DEFAULT_USER = "avnadmin"
    
    // Base64 credentials token
    private const val ENC_PASS = "QVZOU19GRFhSaEhjaDNseUg3eDZWdFNM"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getHost(context: Context): String {
        return getPrefs(context).getString(KEY_HOST, DEFAULT_HOST) ?: DEFAULT_HOST
    }

    fun getPort(context: Context): Int {
        return getPrefs(context).getInt(KEY_PORT, DEFAULT_PORT)
    }

    fun getDatabase(context: Context): String {
        return getPrefs(context).getString(KEY_DB, DEFAULT_DB) ?: DEFAULT_DB
    }

    fun getUser(context: Context): String {
        return getPrefs(context).getString(KEY_USER, DEFAULT_USER) ?: DEFAULT_USER
    }

    fun getDefaultPassword(): String {
        return try {
            val decoded = Base64.decode(ENC_PASS, Base64.DEFAULT)
            String(decoded, Charsets.UTF_8).trim()
        } catch (e: Exception) {
            ""
        }
    }

    fun getPassword(context: Context): String {
        val custom = getPrefs(context).getString(KEY_CUSTOM_PASS, null)
        if (!custom.isNullOrBlank()) return custom.trim()
        return getDefaultPassword()
    }

    fun saveConfig(
        context: Context,
        host: String,
        port: Int,
        db: String,
        user: String,
        password: String? = null
    ) {
        val editor = getPrefs(context).edit()
            .putString(KEY_HOST, host.trim())
            .putInt(KEY_PORT, port)
            .putString(KEY_DB, db.trim())
            .putString(KEY_USER, user.trim())
        if (!password.isNullOrBlank()) {
            editor.putString(KEY_CUSTOM_PASS, password.trim())
        }
        editor.apply()
    }

    fun resetToDefaults(context: Context) {
        getPrefs(context).edit().clear().apply()
    }
}
