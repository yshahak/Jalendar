package com.thedroidboy.jalendar.utils

import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import android.preference.PreferenceManager
import android.support.annotation.RequiresApi
import java.util.*





/**
 * Created by Yaakov Shahak
 * on 02/04/2018.
 */
fun SharedPreferences.isLocaleHebrew(): Boolean {
    val local = getString(Constants.KEY_LANGUAGE, "iw_IL")
    return local == "iw_IL"
}

fun SharedPreferences.getLocale(): Locale {
    if (isLocaleHebrew()) {
        return Locale("iw","IL")
    }
    return Locale.US
}

@Suppress("DEPRECATION")
fun Context.getConfiguration(): ContextWrapper {
    val res = resources
    val configuration = res.configuration
    val locale = PreferenceManager.getDefaultSharedPreferences(this).getLocale()
    var context: Context = this
    when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
            configuration.setLocale(locale)
            val localeList = LocaleList(locale)
            LocaleList.setDefault(localeList)
            configuration.locales = localeList
            context = createConfigurationContext(configuration)
        }
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 -> {
            configuration.setLocale(locale)
            context = createConfigurationContext(configuration)

        }
        else -> {
            configuration.locale = locale
            res.updateConfiguration(configuration, res.displayMetrics)
        }
    }
    return ContextWrapper(context)
}

@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
fun Context.getConfig(): Configuration {
    val locale = PreferenceManager.getDefaultSharedPreferences(this).getLocale()
    val configuration = Configuration()
    configuration.setLocale(locale)
    return configuration
}

fun Context.updateConfig(app: Application) {
    val locale = PreferenceManager.getDefaultSharedPreferences(this).getLocale()
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
        //Wrapping the configuration to avoid Activity endless loop
        val config = Configuration(resources.configuration)
        // We must use the now-deprecated config.locale and res.updateConfiguration here,
        // because the replacements aren't available till API level 24 and 17 respectively.
        config.locale = locale
        val res = app.baseContext.resources
        res.updateConfiguration(config, res.displayMetrics)
    } else {
        Locale.setDefault(locale)
    }
}