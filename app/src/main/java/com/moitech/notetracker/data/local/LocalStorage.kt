package com.moitech.notetracker.data.local

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.moitech.notetracker.data.model.TrackedFunction

object LocalStorage {
    private const val PREFS_NAME = "moi_note_tracker_prefs"
    private const val KEY_BASE_URL = "base_url"
    private const val KEY_CACHED_FUNCTIONS = "cached_functions"
    private const val KEY_NICKNAMES = "nicknames_map"
    private const val KEY_LAST_SYNC = "last_sync_time"

    // Default to common LAN IP or local emulator
    const val DEFAULT_BASE_URL = "http://10.0.2.2:3000/api/"

    private val gson = Gson()

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getBaseUrl(context: Context): String {
        return getPrefs(context).getString(KEY_BASE_URL, DEFAULT_BASE_URL) ?: DEFAULT_BASE_URL
    }

    fun setBaseUrl(context: Context, url: String) {
        val sanitized = if (!url.endsWith("/")) "$url/" else url
        getPrefs(context).edit().putString(KEY_BASE_URL, sanitized).apply()
    }

    fun getCachedFunctions(context: Context): List<TrackedFunction> {
        val json = getPrefs(context).getString(KEY_CACHED_FUNCTIONS, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<TrackedFunction>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveCachedFunctions(context: Context, list: List<TrackedFunction>) {
        val json = gson.toJson(list)
        getPrefs(context).edit()
            .putString(KEY_CACHED_FUNCTIONS, json)
            .putLong(KEY_LAST_SYNC, System.currentTimeMillis())
            .apply()
    }

    fun getLastSyncTime(context: Context): Long {
        return getPrefs(context).getLong(KEY_LAST_SYNC, 0L)
    }

    fun getNicknames(context: Context): Map<String, String> {
        val json = getPrefs(context).getString(KEY_NICKNAMES, null) ?: return emptyMap()
        return try {
            val type = object : TypeToken<Map<String, String>>() {}.type
            gson.fromJson(json, type) ?: emptyMap()
        } catch (e: Exception) {
            emptyMap()
        }
    }

    fun saveNickname(context: Context, systemName: String, nickname: String) {
        val current = getNicknames(context).toMutableMap()
        current[systemName] = nickname
        getPrefs(context).edit().putString(KEY_NICKNAMES, gson.toJson(current)).apply()
    }

    fun clearCache(context: Context) {
        getPrefs(context).edit()
            .remove(KEY_CACHED_FUNCTIONS)
            .remove(KEY_LAST_SYNC)
            .apply()
    }
}
