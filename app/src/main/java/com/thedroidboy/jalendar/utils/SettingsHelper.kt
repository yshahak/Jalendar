package com.thedroidboy.jalendar.utils

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.preference.PreferenceManager
import java.util.*


/**
 * Created by Yaakov Shahak
 * on 02/04/2018.
 */

private val SELECTED_LANGUAGE = "Locale.Helper.Selected.Language"

fun Context.isLocaleHebrew(): Boolean {
    val local = getPersistedData(Locale.getDefault().language)
    return local == "iw"
}

fun Context.onAttach(): Context {
    val lang = getPersistedData(Locale.getDefault().language)
    return setLocale(lang)
}

fun Context.onAttach(defaultLanguage: String): Context {
    val lang = getPersistedData(defaultLanguage)
    return setLocale(lang)
}

fun Context.getLanguage(): String? {
    return getPersistedData(Locale.getDefault().language)
}

fun Context.setLocale(language: String?): Context {
    persist(language)
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        updateResources(language)
    } else
        updateResourcesLegacy(language)

}

private fun Context.getPersistedData(defaultLanguage: String): String? {
    val preferences = PreferenceManager.getDefaultSharedPreferences(this)
    return preferences.getString(SELECTED_LANGUAGE, defaultLanguage)
}

private fun Context.persist(language: String?) {
    val preferences = PreferenceManager.getDefaultSharedPreferences(this)
    val editor = preferences.edit()
    editor.putString(SELECTED_LANGUAGE, language)
    editor.apply()
}

@TargetApi(Build.VERSION_CODES.N)
private fun Context.updateResources(language: String?): Context {
    val locale = Locale(language)
    Locale.setDefault(locale)

    val configuration = resources.configuration
    configuration.setLocale(locale)
//    configuration.setLayoutDirection(locale)
    return createConfigurationContext(configuration)
}

@Suppress("DEPRECATION")
private fun Context.updateResourcesLegacy(language: String?): Context {
    val locale = Locale(language)
    Locale.setDefault(locale)
    val configuration = resources.configuration
    configuration.locale = locale
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//        configuration.setLayoutDirection(locale)
//    }
    resources.updateConfiguration(configuration, resources.displayMetrics)
    return this
}