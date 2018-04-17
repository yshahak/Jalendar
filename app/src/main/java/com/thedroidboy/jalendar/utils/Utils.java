package com.thedroidboy.jalendar.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v4.content.PermissionChecker;

import net.sourceforge.zmanim.ZmanimCalendar;
import net.sourceforge.zmanim.hebrewcalendar.HebrewDateFormatter;
import net.sourceforge.zmanim.hebrewcalendar.JewishCalendar;
import net.sourceforge.zmanim.util.GeoLocation;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by B.E.L on 07/01/2016.
 */
public class Utils {
    public static int NOTIFICATION_PERCENTAGE_ID = 12345;
    public static int NOTIFICATION_OMER = 100;

//    public static Bitmap writeSmallIcon(Context context, String month, String day, boolean nightInversion) {
//        int dimen = Utils.getDpInPixels(context, 24);
//        Drawable drawable = context.getResources().getDrawable(R.drawable.small_icon_bg);
//        Bitmap bitmap = Bitmap.createBitmap(dimen, dimen, Bitmap.Config.ARGB_8888);
//        Canvas c = new Canvas(bitmap);
//        drawable.setBounds(0, 0, dimen, dimen);
//        drawable.draw(c);
//        Paint paint = new Paint();
////        paint.setTypeface(Typeface.create("Arial" ,Typeface.BOLD));
//
//        paint.setStyle(Paint.Style.FILL);
//        paint.setColor(Color.WHITE);
//        paint.setFakeBoldText(true);
//        paint.setTextSize(80);
//        drawTop(c, paint, day);
//        paint.setTextSize(80);
//        drawBottom(c, paint, month);
//        if (nightInversion) {
//            invertColorsOnBitmap(bitmap);
//        }
//        return bitmap;
//    }

//    public static Bitmap writeOnDrawable(Context context, String month, String day) {
//        Resources res = context.getResources();
//        int dimen;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
//            dimen = (int) res.getDimension(android.R.dimen.notification_large_icon_height);
//        } else {
//            dimen = Utils.getDpInPixels(context, 72);
//        }
//        Drawable drawable = context.getResources().getDrawable(R.drawable.icon_notification_bg);
//
//        Bitmap bitmap = Bitmap.createBitmap(dimen, dimen, Bitmap.Config.ARGB_8888);
//        Canvas c = new Canvas(bitmap);
//        drawable.setBounds(0, 0, dimen, dimen);
//        drawable.draw(c);
//        Paint paint = new Paint();
//        paint.setStyle(Paint.Style.FILL);
//        paint.setColor(Color.WHITE);
//        paint.setFakeBoldText(true);
//        paint.setTextSize(80);
//        drawTop(c, paint, day);
//        paint.setTextSize(80);
//        drawBottom(c, paint, month);
//        return bitmap;
//    }


//    public static Bitmap writeOnXiaomiDrawable(Context context, String month, String day) {
//        Resources res = context.getResources();
//        int dimen;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
//            dimen = (int) res.getDimension(android.R.dimen.notification_large_icon_height);
//        } else {
//            dimen = Utils.getDpInPixels(context, 72);
//        }
//        Drawable drawable = context.getResources().getDrawable(R.drawable.icon_notification_xiaomi_bg);
//
//        Bitmap bitmap = Bitmap.createBitmap(dimen, dimen, Bitmap.Config.ARGB_8888);
//        Canvas c = new Canvas(bitmap);
//        drawable.setBounds(0, 0, dimen, dimen);
//        drawable.draw(c);
//        Paint paint = new Paint();
//        paint.setTypeface(Typeface.create("Arial" , Typeface.BOLD));
//        paint.setStyle(Paint.Style.FILL);
//        paint.setColor(Color.BLACK);
//        paint.setFakeBoldText(true);
//        paint.setTextSize(80);
//        drawTop(c, paint, day);
//        paint.setTextSize(80);
//        drawBottom(c, paint, month);
//        return bitmap;
//    }

    private static void drawTop(Canvas canvas, Paint paint, String text) {
        int cHeight = canvas.getClipBounds().height() / 2;
        int cWidth = canvas.getClipBounds().width();
        Rect r = new Rect();
        paint.setTextAlign(Paint.Align.LEFT);
        paint.getTextBounds(text, 0, text.length(), r);
        while (r.width() >= cWidth || r.height() >= cHeight) {
            paint.setTextSize(paint.getTextSize() - 1);
            paint.getTextBounds(text, 0, text.length(), r);
        }
        float x = (cWidth - r.width()) / 2;
        float y = cHeight - (cHeight - r.height()) / 2;
        canvas.drawText(text, x, y, paint);
    }

    private static void drawBottom(Canvas canvas, Paint paint, String text) {
        int cHeight = canvas.getClipBounds().height() / 2;
        int cWidth = canvas.getClipBounds().width();
        Rect r = new Rect();
        paint.setTextAlign(Paint.Align.LEFT);
        paint.getTextBounds(text, 0, text.length(), r);
        while (r.width() >= cWidth || r.height() >= cHeight) {
            paint.setTextSize(paint.getTextSize() - 1);
            paint.getTextBounds(text, 0, text.length(), r);
        }
        float x = (cWidth - r.width()) / 2;
        float y = cHeight * 2 - (cHeight - r.height()) / 2;
        canvas.drawText(text, x, y, paint);
    }

    private static void invertColorsOnBitmap(Bitmap bitmap) {
        int length = bitmap.getWidth() * bitmap.getHeight();
        int[] array = new int[length];
        bitmap.getPixels(array, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        for (int i = 0; i < length; i++) {
            if (array[i] != Color.TRANSPARENT) {
                array[i] = Color.TRANSPARENT;
            } else {
                array[i] = Color.WHITE;
            }
        }
        bitmap.setPixels(array, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
    }

    private static void drawCenter(Canvas canvas, Paint paint, String text, float yFactor) {
        int cHeight = canvas.getClipBounds().height();
        int cWidth = canvas.getClipBounds().width();
        Rect r = new Rect();
        paint.setTextAlign(Paint.Align.LEFT);
        paint.getTextBounds(text, 0, text.length(), r);
        while (r.width() >= cWidth) {
            paint.setTextSize(paint.getTextSize() - 1);
            paint.getTextBounds(text, 0, text.length(), r);
        }
        float x = cWidth / 2f - r.width() / 2f - r.left;
        float y = (cHeight * yFactor + r.height() / 2f - r.bottom);
        canvas.drawText(text, x, y, paint);
    }

    private static int getDpInPixels(Context context, int dpValue) {
        float d = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * d); // margin in pixels
    }

    public static boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        } catch (Exception e) {
            return false;
        }
    }


//    public static void hideNotification(Context ctx) {
//        NotificationManager nm = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
//        nm.cancel(NOTIFICATION_PERCENTAGE_ID);
//        PreferenceManager.getDefaultSharedPreferences(ctx).edit().putInt(ctx.getString(R.string.key_last_day), -1).apply();
//    }

    public static String getSpecialDays(JewishCalendar jewishCalendar, HebrewDateFormatter hebrewDateFormatter) {
        if (hebrewDateFormatter.formatOmer(jewishCalendar).length() > 0) {
            return hebrewDateFormatter.formatOmer(jewishCalendar);
        }
        if (jewishCalendar.isRoshChodesh()) {
            return hebrewDateFormatter.formatRoshChodesh(jewishCalendar);
        }
        if (hebrewDateFormatter.formatYomTov(jewishCalendar).length() > 0) {
            return hebrewDateFormatter.formatYomTov(jewishCalendar);
        }
        if (jewishCalendar.isChanukah()) {
            return "נר " + Integer.toString(jewishCalendar.getDayOfChanukah()) + " של חנוכה";
        }
        int index = jewishCalendar.getYomTovIndex();
        if (index != -1) {
            return getSpecialDate(index, jewishCalendar);
        }
        return null;
    }

    public static String getSpecialDate(int jewishIndex, JewishCalendar jewishCalendar) {
        switch (jewishIndex) {
            case JewishCalendar.EREV_PESACH:
                return "ערב פסח";
            case JewishCalendar.PESACH:
                return "פסח";
            case JewishCalendar.CHOL_HAMOED_PESACH:
                switch (jewishCalendar.getJewishDayOfMonth()) {
                    case (16):
                        return "א חול המועד פסח";
                    case (17):
                        return "ב חול המועד פסח";
                    case (18):
                        return "ג' חול המועד פסח";
                    case (19):
                        return "ד חול המועד פסח";
                    case (20):
                        return "ה חול המועד פסח";
                }
            case JewishCalendar.YOM_HASHOAH:
                return "יום השואה";
            case JewishCalendar.YOM_HAZIKARON:
                return "יום הזכרון לחללי צהל";
            case JewishCalendar.YOM_HAATZMAUT:
                return "יום העצמאות";
            case JewishCalendar.YOM_KIPPUR:
                return "יום כיפור";
            case JewishCalendar.YOM_YERUSHALAYIM:
                return "יום ירושלים";
            case JewishCalendar.EREV_YOM_KIPPUR:
                return "ערב יום הכיפורים";
            case JewishCalendar.PESACH_SHENI:
                return "פסח שני";
            case JewishCalendar.SHAVUOS:
                return "שבועות";
            case JewishCalendar.EREV_SHAVUOS:
                return "ערב שבועות";
            case JewishCalendar.SEVENTEEN_OF_TAMMUZ:
                return "צום יז בתמוז";
            case JewishCalendar.TISHA_BEAV:
                return "תשעה באב";
            case JewishCalendar.TU_BEAV:
                return "טו באב";
            case JewishCalendar.ROSH_HASHANA:
                return "ראש השנה";
            case JewishCalendar.EREV_ROSH_HASHANA:
                return "ערב ראש השנה";
            case JewishCalendar.FAST_OF_GEDALYAH:
                return "צום גדליה";
            case JewishCalendar.FAST_OF_ESTHER:
                return "תענית אסתר";
            case JewishCalendar.EREV_SUCCOS:
                return "ערב סוכות";
            case JewishCalendar.SUCCOS:
                return "סוכות";
            case JewishCalendar.CHOL_HAMOED_SUCCOS:
                switch (jewishCalendar.getJewishDayOfMonth()) {
                    case (17):
                        return "א חול המועד סוכות";
                    case (18):
                        return "ב חול המועד סוכות";
                    case (19):
                        return "ג' חול המועד סוכות";
                    case (20):
                        return "ד חול המועד סוכות";
                    case (21):
                        return "הושענא רבא";
                }
            case JewishCalendar.HOSHANA_RABBA:
                return "הושענא רבא";
            case JewishCalendar.SIMCHAS_TORAH:
            case JewishCalendar.SHEMINI_ATZERES:
                return "שמחת תורה";
            case JewishCalendar.TENTH_OF_TEVES:
                return "צום עשרה בטבת";
            case JewishCalendar.TU_BESHVAT:
                return "טו בשבט";
            case JewishCalendar.PURIM:
                return "פורים";
            case JewishCalendar.SHUSHAN_PURIM:
                return "שושן פורים";


        }
        return null;

    }

    /**
     * 200         * Returns an index of the Jewish holiday or fast day for the current day, or a null if there is no holiday for this
     * 201         * day.
     * 202         *
     * 203         * @return A String containing the holiday name or an empty string if it is not a holiday.
     * 204
     */
    public int getYomTovIndex(JewishCalendar jewishCalendar) {
        // check by month (starts from Nissan)
        switch (jewishCalendar.getJewishMonth()) {
            case JewishCalendar.NISSAN:
                if (jewishCalendar.getJewishDayOfMonth() == 14) {
                    return JewishCalendar.EREV_PESACH;
                } else if (jewishCalendar.getJewishDayOfMonth() == 15 || jewishCalendar.getJewishDayOfMonth() == 21
                        || (!jewishCalendar.getInIsrael() && (jewishCalendar.getJewishDayOfMonth() == 16 || jewishCalendar.getJewishDayOfMonth() == 22))) {
                    return JewishCalendar.PESACH;
                } else if (jewishCalendar.getJewishDayOfMonth() >= 17 && jewishCalendar.getJewishDayOfMonth() <= 20
                        || (jewishCalendar.getJewishDayOfMonth() == 16 && jewishCalendar.getInIsrael())) {
                    return JewishCalendar.CHOL_HAMOED_PESACH;
                }
                if (jewishCalendar.isUseModernHolidays()
                        && ((jewishCalendar.getJewishDayOfMonth() == 26 && jewishCalendar.getDayOfWeek() == 5)
                        || (jewishCalendar.getJewishDayOfMonth() == 28 && jewishCalendar.getDayOfWeek() == 1)
                        || (jewishCalendar.getJewishDayOfMonth() == 27 && jewishCalendar.getDayOfWeek() == 3) || (jewishCalendar.getJewishDayOfMonth() == 27 && jewishCalendar.getDayOfWeek() == 5))) {
                    return JewishCalendar.YOM_HASHOAH;
                }
                break;
            case JewishCalendar.IYAR:
                if (jewishCalendar.isUseModernHolidays()
                        && ((jewishCalendar.getJewishDayOfMonth() == 4 && jewishCalendar.getDayOfWeek() == 3)
                        || ((jewishCalendar.getJewishDayOfMonth() == 3 || jewishCalendar.getJewishDayOfMonth() == 2) && jewishCalendar.getDayOfWeek() == 4) || (jewishCalendar.getJewishDayOfMonth() == 5 && jewishCalendar.getDayOfWeek() == 2))) {
                    return JewishCalendar.YOM_HAZIKARON;
                }
                // if 5 Iyar falls on Wed Yom Haatzmaut is that day. If it fal1s on Friday or Shabbos it is moved back to
                // Thursday. If it falls on Monday it is moved to Tuesday
                if (jewishCalendar.isUseModernHolidays()
                        && ((jewishCalendar.getJewishDayOfMonth() == 5 && jewishCalendar.getDayOfWeek() == 4)
                        || ((jewishCalendar.getJewishDayOfMonth() == 4 || jewishCalendar.getJewishDayOfMonth() == 3) && jewishCalendar.getDayOfWeek() == 5) || (jewishCalendar.getJewishDayOfMonth() == 6 && jewishCalendar.getDayOfWeek() == 3))) {
                    return JewishCalendar.YOM_HAATZMAUT;
                }
                if (jewishCalendar.getJewishDayOfMonth() == 14) {
                    return JewishCalendar.PESACH_SHENI;
                }
                if (jewishCalendar.isUseModernHolidays() && jewishCalendar.getJewishDayOfMonth() == 28) {
                    return JewishCalendar.YOM_YERUSHALAYIM;
                }
                break;
            case JewishCalendar.SIVAN:
                if (jewishCalendar.getJewishDayOfMonth() == 5) {
                    return JewishCalendar.EREV_SHAVUOS;
                } else if (jewishCalendar.getJewishDayOfMonth() == 6 || (jewishCalendar.getJewishDayOfMonth() == 7 && !jewishCalendar.getInIsrael())) {
                    return JewishCalendar.SHAVUOS;
                }
                break;
            case JewishCalendar.TAMMUZ:
                // push off the fast day if it falls on Shabbos
                if ((jewishCalendar.getJewishDayOfMonth() == 17 && jewishCalendar.getDayOfWeek() != 7)
                        || (jewishCalendar.getJewishDayOfMonth() == 18 && jewishCalendar.getDayOfWeek() == 1)) {
                    return JewishCalendar.SEVENTEEN_OF_TAMMUZ;
                }
                break;
            case JewishCalendar.AV:
                // if Tisha B'av falls on Shabbos, push off until Sunday
                if ((jewishCalendar.getDayOfWeek() == 1 && jewishCalendar.getJewishDayOfMonth() == 10)
                        || (jewishCalendar.getDayOfWeek() != 7 && jewishCalendar.getJewishDayOfMonth() == 9)) {
                    return JewishCalendar.TISHA_BEAV;
                } else if (jewishCalendar.getJewishDayOfMonth() == 15) {
                    return JewishCalendar.TU_BEAV;
                }
                break;
            case JewishCalendar.ELUL:
                if (jewishCalendar.getJewishDayOfMonth() == 29) {
                    return JewishCalendar.EREV_ROSH_HASHANA;
                }
                break;
            case JewishCalendar.TISHREI:
                if (jewishCalendar.getJewishDayOfMonth() == 1 || jewishCalendar.getJewishDayOfMonth() == 2) {
                    return JewishCalendar.ROSH_HASHANA;
                } else if ((jewishCalendar.getJewishDayOfMonth() == 3 && jewishCalendar.getDayOfWeek() != 7)
                        || (jewishCalendar.getJewishDayOfMonth() == 4 && jewishCalendar.getDayOfWeek() == 1)) {
                    // push off Tzom Gedalia if it falls on Shabbos
                    return JewishCalendar.FAST_OF_GEDALYAH;
                } else if (jewishCalendar.getJewishDayOfMonth() == 9) {
                    return JewishCalendar.EREV_YOM_KIPPUR;
                } else if (jewishCalendar.getJewishDayOfMonth() == 10) {
                    return JewishCalendar.YOM_KIPPUR;
                } else if (jewishCalendar.getJewishDayOfMonth() == 14) {
                    return JewishCalendar.EREV_SUCCOS;
                }
                if (jewishCalendar.getJewishDayOfMonth() == 15 || (jewishCalendar.getJewishDayOfMonth() == 16 && !jewishCalendar.getInIsrael())) {
                    return JewishCalendar.SUCCOS;
                }
                if (jewishCalendar.getJewishDayOfMonth() >= 17 && jewishCalendar.getJewishDayOfMonth() <= 20 || (jewishCalendar.getJewishDayOfMonth() == 16 && jewishCalendar.getInIsrael())) {
                    return JewishCalendar.CHOL_HAMOED_SUCCOS;
                }
                if (jewishCalendar.getJewishDayOfMonth() == 21) {
                    return JewishCalendar.HOSHANA_RABBA;
                }
                if (jewishCalendar.getJewishDayOfMonth() == 22) {
                    return JewishCalendar.SHEMINI_ATZERES;
                }
                if (jewishCalendar.getJewishDayOfMonth() == 23 && !jewishCalendar.getInIsrael()) {
                    return JewishCalendar.SIMCHAS_TORAH;
                }
                break;
            case JewishCalendar.KISLEV: // no yomtov in CHESHVAN
                // if (getJewishDayOfMonth() == 24) {
                // return EREV_CHANUKAH;
                // } else
                if (jewishCalendar.getJewishDayOfMonth() >= 25) {
                    return JewishCalendar.CHANUKAH;
                }
                break;
            case JewishCalendar.TEVES:
                if (jewishCalendar.getJewishDayOfMonth() == 1 || jewishCalendar.getJewishDayOfMonth() == 2
                        || (jewishCalendar.getJewishDayOfMonth() == 3 && jewishCalendar.isKislevShort())) {
                    return JewishCalendar.CHANUKAH;
                } else if (jewishCalendar.getJewishDayOfMonth() == 10) {
                    return JewishCalendar.TENTH_OF_TEVES;
                }
                break;
            case JewishCalendar.SHEVAT:
                if (jewishCalendar.getJewishDayOfMonth() == 15) {
                    return JewishCalendar.TU_BESHVAT;
                }
                break;
            case JewishCalendar.ADAR:
                if (!jewishCalendar.isJewishLeapYear()) {
                    // if 13th Adar falls on Friday or Shabbos, push back to Thursday
                    if (((jewishCalendar.getJewishDayOfMonth() == 11 || jewishCalendar.getJewishDayOfMonth() == 12) && jewishCalendar.getDayOfWeek() == 5)
                            || (jewishCalendar.getJewishDayOfMonth() == 13 && !(jewishCalendar.getDayOfWeek() == 6 || jewishCalendar.getDayOfWeek() == 7))) {
                        return JewishCalendar.FAST_OF_ESTHER;
                    }
                    if (jewishCalendar.getJewishDayOfMonth() == 14) {
                        return JewishCalendar.PURIM;
                    } else if (jewishCalendar.getJewishDayOfMonth() == 15) {
                        return JewishCalendar.SHUSHAN_PURIM;
                    }
                } else { // else if a leap year
                    if (jewishCalendar.getJewishDayOfMonth() == 14) {
                        return JewishCalendar.PURIM_KATAN;
                    }
                }
                break;
            case JewishCalendar.ADAR_II:
                // if 13th Adar falls on Friday or Shabbos, push back to Thursday
                if (((jewishCalendar.getJewishDayOfMonth() == 11 || jewishCalendar.getJewishDayOfMonth() == 12) && jewishCalendar.getDayOfWeek() == 5)
                        || (jewishCalendar.getJewishDayOfMonth() == 13 && !(jewishCalendar.getDayOfWeek() == 6 || jewishCalendar.getDayOfWeek() == 7))) {
                    return JewishCalendar.FAST_OF_ESTHER;
                }
                if (jewishCalendar.getJewishDayOfMonth() == 14) {
                    return JewishCalendar.PURIM;
                } else if (jewishCalendar.getJewishDayOfMonth() == 15) {
                    return JewishCalendar.SHUSHAN_PURIM;
                }
                break;
        }
        // if we get to this stage, then there are no holidays for the given date return -1
        return -1;
    }


    public static boolean isPermissionsGranted(Context context, String... permissions) {
        for (String permission : permissions) {
            if (!isPermissionGranted(context, permission)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isPermissionGranted(Context context, String permission) {
        return PermissionChecker.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }


    @SuppressWarnings("MissingPermission")
    private static ZmanimCalendar getLocationBasedCalendar(Context context) {
        GeoLocation geoLocation = LocationHelper.getChosenGeoLocation(PreferenceManager.getDefaultSharedPreferences(context));
        return new ZmanimCalendar(geoLocation);
    }

    @SuppressWarnings("RedundantIfStatement")
    public static boolean isNight(Context context) {
        ZmanimCalendar zmanimCalendar = getLocationBasedCalendar(context);
        Date zetTime = zmanimCalendar.getSunset();
        zetTime.setTime(zetTime.getTime() + 1000 * 60 * 22); //zet hacochavim is 20 minutes after sunset
        Date alosTime = zmanimCalendar.getAlosHashachar();
        Date now = new Date();
        if (now.getTime() < alosTime.getTime()) {
            return true;
        }
        if (now.getTime() > zetTime.getTime()) {
            return true;
        }
        return false;
    }

    public static boolean isDay(Calendar zet) {
        Date zetTime = zet.getTime();
        return new Date().getTime() < zetTime.getTime();
    }

    public static boolean isDay(Date dusk) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dusk);
        return isCalendarSameDay(calendar) && new Date().getTime() < dusk.getTime();
    }

    private static boolean isCalendarSameDay(Calendar calendar) {
        int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
        int now = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        return dayOfYear == now;
    }


    public static boolean isLocaleHebrew(SharedPreferences prefs) {
        String local = prefs.getString(Constants.KEY_LANGUAGE, null);
        if (local != null) {
            return !local.equals("en_us");
        } else if (Locale.getDefault().getDisplayCountry().equals("IL")) {
            return true;
        }
        return false;
    }

    static SharedPreferences.Editor putDouble(final SharedPreferences.Editor edit, final String key, final double value) {
        return edit.putLong(key, Double.doubleToRawLongBits(value));
    }

    static double getDouble(final SharedPreferences prefs, final String key, final double defaultValue) {
        return Double.longBitsToDouble(prefs.getLong(key, Double.doubleToLongBits(defaultValue)));
    }
}
