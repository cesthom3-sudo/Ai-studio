package com.thomas.aistudio.data.api

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.thomas.aistudio.BuildConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "thomas_ai_studio_settings")

object AppSettings {
    private val BACKEND_URL_KEY = stringPreferencesKey("backend_url")
    private val ASPECT_RATIO_KEY = stringPreferencesKey("default_aspect_ratio")

    fun backendUrlFlow(context: Context): Flow<String> =
        context.dataStore.data.map { it[BACKEND_URL_KEY] ?: BuildConfig.DEFAULT_BACKEND_URL }

    suspend fun setBackendUrl(context: Context, url: String) {
        context.dataStore.edit { it[BACKEND_URL_KEY] = url }
    }

    fun defaultAspectRatioFlow(context: Context): Flow<String> =
        context.dataStore.data.map { it[ASPECT_RATIO_KEY] ?: "9:16" }

    suspend fun setDefaultAspectRatio(context: Context, ratio: String) {
        context.dataStore.edit { it[ASPECT_RATIO_KEY] = ratio }
    }
}
